import { Notify, Dialog } from 'quasar';
import router from '../../router';
import axios from 'axios'; 
const env = require('../../../.quasar.env.json');

const SESSION_TIMEOUT = env[process.env.NODE_ENV].SESSION_TIMEOUT * 1000; // Convert to ms
const SESSION_WARNING = env[process.env.NODE_ENV].SESSION_WARNING * 1000; // Convert to ms

// Create a singleton session manager
const SessionManager = (function() {
  let instance;
  let warningTimeout;
  let expirationTimeout;
  let warningDialog = null;
  let countdownInterval = null;
  let isSessionExpired = false;
  let debounceTimer = null;
  let isInitialized = false;
  
  // User activity events to monitor
  const userActivityEvents = [
    'mousedown', 'mousemove', 'keypress', 
    'scroll', 'touchstart', 'click'
  ];
  
  function clearAllTimers() {
    clearTimeout(warningTimeout);
    clearTimeout(expirationTimeout);
    clearTimeout(debounceTimer);
    clearInterval(countdownInterval);
    
    if (warningDialog) {
      warningDialog.hide();
      warningDialog = null;
    }
  }
  
  function handleUserActivity() {
    // Don't reset if session expired or warning is showing
    if (isSessionExpired || warningDialog) {
      return;
    }
    
    // Debounce the reset to prevent excessive resets
    clearTimeout(debounceTimer);
    debounceTimer = setTimeout(() => {
      resetSession();
    }, 1000); // 1-second debounce
  }
  
  function setupEventListeners() {
    // Remove any existing listeners first to prevent duplicates
    removeEventListeners();
    
    // Add new listeners
    userActivityEvents.forEach(event => {
      document.addEventListener(event, handleUserActivity, { passive: true });
    });
  }
  
  function removeEventListeners() {
    userActivityEvents.forEach(event => {
      document.removeEventListener(event, handleUserActivity);
    });
  }
  
  function resetSession() {
    clearAllTimers();
    startTimers();
  }
  
  function startTimers() {
    console.log(`Warning after: ${SESSION_WARNING / 1000} seconds`);
    console.log(`Expiration after: ${SESSION_TIMEOUT / 1000} seconds`);
    
    // Set warning timer
    warningTimeout = setTimeout(() => {
      if (warningDialog) {
        return;
      }
      
      
      let countdown = (SESSION_TIMEOUT - SESSION_WARNING) / 1000; // Calculate remaining time before expiration
      console.log(`Starting warning countdown: ${countdown} seconds`);

      warningDialog = Dialog.create({
        title: 'Session Expiring',
        message: `Your session will expire in ${countdown} seconds.`,
        ok: {
          label: 'Extend',
          color: 'primary',
        },
        cancel: false,
        persistent: true,
      }).onOk(async () => {
        console.log('Session extension requested by user');
        
        try {
          const response = await axios.get(`${env[process.env.NODE_ENV].API_SERVER}/api/user`);
          console.log('Session extension API call successful');
          
          // Reset the session
          resetSession();
          Notify.create({ type: 'positive', message: 'Session extended!' });
        } catch (error) {
          console.error('Session extension API call failed:', error);
          Notify.create({ type: 'negative', message: 'Failed to extend session. Please try again.' });
        } finally {
          warningDialog = null;
          clearInterval(countdownInterval);
        }
      }).onDismiss(() => {
        warningDialog = null;
        clearInterval(countdownInterval);
      });

      // Update the countdown every second
      countdownInterval = setInterval(() => {
        countdown -= 1;
        if (countdown <= 0) {
          clearInterval(countdownInterval);
        } else if (warningDialog) {
          warningDialog.update({
            message: `Your session will expire in ${countdown} seconds.`,
          });
        }
      }, 1000);
    }, SESSION_WARNING);
    
    // Set expiration timer
    expirationTimeout = setTimeout(() => {
      if (isSessionExpired) {
        return;
      }
      
      console.log('Session expired, showing expiration dialog');
      isSessionExpired = true;
      
      // Clean up
      removeEventListeners();
      
      if (warningDialog) {
        warningDialog.hide();
        warningDialog = null;
      }

      if (countdownInterval) {
        clearInterval(countdownInterval);
        countdownInterval = null;
      }
      
      // Add overlay
      const overlay = document.createElement('div');
      overlay.id = 'session-expired-overlay';
      overlay.style.position = 'fixed';
      overlay.style.top = '0';
      overlay.style.left = '0';
      overlay.style.width = '100%';
      overlay.style.height = '100%';
      overlay.style.backgroundColor = 'white';
      overlay.style.zIndex = '999';
      overlay.style.pointerEvents = 'none';
      document.body.appendChild(overlay);
      
      // Show expiration dialog
      Dialog.create({
        title: 'Session Expired',
        message: 'Your session has expired. Please log in again.',
        ok: {
          label: 'OK',
          color: 'primary',
        },
        cancel: false,
        persistent: true,
        class: 'custom-session-expired-modal'
      }).onOk(() => {
        
        // // Remove overlay
        // const existingOverlay = document.getElementById('session-expired-overlay');
        // if (existingOverlay) {
        //   document.body.removeChild(existingOverlay);
        // }
        
        // Redirect
        window.location.href = "/";
      });
    }, SESSION_TIMEOUT);
  }
  
  // Create the Singleton instance
  function createInstance() {
    return {
      // Public methods
      init: function() {
        if (isInitialized) {
          console.log('Session manager already initialized, skipping');
          return;
        }
        
        console.log('Initializing session manager');
        isInitialized = true;
        isSessionExpired = false;
        setupEventListeners();
        resetSession();
        
        // Return cleanup function
        return this.destroy;
      },
      
      destroy: function() {
        console.log('Destroying session manager');
        isInitialized = false;
        isSessionExpired = false;
        removeEventListeners();
        clearAllTimers();
      },
      
      reset: function() {
        if (!isInitialized) {
          console.warn('Session manager not initialized, cannot reset');
          return;
        }
        
        resetSession();
      },
      
      getStatus: function() {
        return {
          isInitialized,
          isExpired: isSessionExpired,
          hasWarningDialog: warningDialog !== null
        };
      }
    };
  }
  
  return {
    // Get the Singleton instance
    getInstance: function() {
      if (!instance) {
        instance = createInstance();
      }
      return instance;
    }
  };
})();

// Export the public API
export const sessionManager = SessionManager.getInstance();

// Export convenience methods
export function startSessionTimer() {
  return sessionManager.init();
}

export function clearTimers() {
  sessionManager.destroy();
}

export function resetSessionTimer() {
  sessionManager.reset();
}

export function getSessionStatus() {
  return sessionManager.getStatus();
}