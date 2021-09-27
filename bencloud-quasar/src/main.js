import { createApp } from 'vue'
import App from './App.vue'
import './index.css'
import bencloud from './store/bencloud'

import { library } from '@fortawesome/fontawesome-svg-core'

import { fas } from '@fortawesome/free-solid-svg-icons'
import { far } from '@fortawesome/free-regular-svg-icons'

//import { faHome } from '@fortawesome/free-solid-svg-icons'
//import { faCog } from '@fortawesome/free-solid-svg-icons'
//import { faPoll } from '@fortawesome/free-solid-svg-icons'
//import { faLanguage } from '@fortawesome/free-solid-svg-icons'
//import { faQuestionCircle } from '@fortawesome/free-solid-svg-icons'

import { FontAwesomeIcon } from '@fortawesome/vue-fontawesome'
import { FontAwesomeLayers } from '@fortawesome/vue-fontawesome'
import { FontAwesomeLayersText } from '@fortawesome/vue-fontawesome'

//library.add(faHome)
//library.add(faCog)
//library.add(faPoll)
//library.add(faLanguage)
//library.add(faQuestionCircle)

library.add(fas)
library.add(far)

import AppTopNavigation from './components/navigation/AppTopNavigation.vue'

const app = createApp(App)
    .use(store)
    .use(bencloud)
    .use(router)
    .mount('#app');

app.component('app-top-navigation', AppTopNavigation);
//app.component('font-awesome-icon', FontAwesomeIcon)
//app.component('font-awesome-layers', FontAwesomeLayers)
//app.component('font-awesome-layers-text', FontAwesomeLayersText)

app.config.productionTip = false

