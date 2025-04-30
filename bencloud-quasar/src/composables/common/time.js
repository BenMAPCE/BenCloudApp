// Using Luxon for timezone handling
import { DateTime } from 'luxon';

/**
 * Converts a date string to the user's local timezone using Luxon
 * @param {string|Date} dateInput - The date string or Date object to convert
 * @param {string} [formatPattern='yyyy-MM-dd HH:mm:ss'] - Format pattern (Luxon format)
 * @returns {string} Formatted date string with timezone
 */
export const convertToUserTimezone = (dateInput, formatPattern = 'yyyy-MM-dd HH:mm:ss') => {
    try {
        // Get user timezone through Luxon's detection mechanism
        const userTimezone = getUserTimezone();
        
        // Parse the input date based on its type
        let dateTime;
        if (dateInput instanceof Date) {
            dateTime = DateTime.fromJSDate(dateInput);
        } else if (typeof dateInput === 'string') {
            // Try to parse as ISO first
            dateTime = DateTime.fromISO(dateInput);
            
            // If invalid, try other formats
            if (!dateTime.isValid) {
                dateTime = DateTime.fromSQL(dateInput);
            }
            
            // As a last resort, use JS Date parsing
            if (!dateTime.isValid) {
                const jsDate = new Date(dateInput);
                if (!isNaN(jsDate.getTime())) {
                    dateTime = DateTime.fromJSDate(jsDate);
                }
            }
        }
        
        if (!dateTime || !dateTime.isValid) {
            throw new Error(`Failed to parse date: ${dateInput}`);
        }
        
        // Set the timezone
        const zonedDateTime = dateTime.setZone(userTimezone);
        
        // Format the date
        const formattedDate = zonedDateTime.toFormat(formatPattern);
        
        // Get the timezone abbreviation
        const tzAbbr = zonedDateTime.toFormat('ZZZZ');
        
        return `${formattedDate} (${userTimezone}, ${tzAbbr})`;
    } catch (error) {
        console.error('Error in Luxon timezone conversion:', error);
        // Fallback to UTC ISO format
        return `${new Date(dateInput).toISOString()} (UTC)`;
    }
};

/**
 * Get user timezone using Luxon with fallbacks
 * @returns {string} Detected timezone or fallback
 */
function getUserTimezone() {
    try {
        // Method 1: Use Luxon's built-in detection
        const detected = DateTime.local().zoneName;
        
        // Verify it's not the "default" fallback that Luxon uses
        if (detected && detected !== 'system' && detected !== 'UTC') {
            return detected;
        }
    } catch (e) {
        console.warn('Luxon timezone detection failed:', e);
    }
    
    try {
        // Method 2: Try browser's Intl API
        if (Intl && Intl.DateTimeFormat) {
            return Intl.DateTimeFormat().resolvedOptions().timeZone;
        }
    } catch (e) {
        console.warn('Intl API timezone detection failed:', e);
    }
    
    // // Method 3: Fallback to manual detection 
    // try {
    //     // Generate mapping of common timezones based on offsets
    //     const now = new Date();
    //     const winterDate = new Date(now.getFullYear(), 0, 1);
    //     const summerDate = new Date(now.getFullYear(), 6, 1);
        
    //     const winterOffset = -winterDate.getTimezoneOffset();
    //     const summerOffset = -summerDate.getTimezoneOffset();
        
    //     // Common timezone mapping (could be expanded)
    //     const timezoneMap = {
    //         'America/New_York': { winter: 300, summer: 240 },
    //         'America/Chicago': { winter: 360, summer: 300 },
    //         'America/Denver': { winter: 420, summer: 360 },
    //         'America/Los_Angeles': { winter: 480, summer: 420 },
    //         'Europe/London': { winter: 0, summer: -60 },
    //         'Europe/Paris': { winter: -60, summer: -120 },
    //         // Add more as needed
    //     };
        
        // for (const [tz, offsets] of Object.entries(timezoneMap)) {
    //         if (offsets.winter === winterOffset && offsets.summer === summerOffset) {
    //             return tz;
    //         }
    //     }
        
    //     // If no match, create an offset-based name
    //     const offset = now.getTimezoneOffset();
    //     const hourOffset = Math.abs(Math.floor(offset / 60));
    //     const minuteOffset = Math.abs(offset % 60);
        
    //     const sign = offset <= 0 ? '+' : '-';
    //     return `UTC${sign}${String(hourOffset).padStart(2, '0')}:${String(minuteOffset).padStart(2, '0')}`;
    // } catch (e) {
    //     console.warn('Manual timezone detection failed:', e);
    // }
    
    // Ultimate fallback
    return 'UTC';
}