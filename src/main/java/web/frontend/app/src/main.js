import Vue from 'vue'
import VueRouter from 'vue-router'
import App from './App'

Vue.use(VueRouter)

Vue.config.productionTip = false

Vue.mixin({
    data: function () {
        return {
            get serverIp() {
                //return 'http://161.35.95.41';
                return 'https://tfe.tools';
                //return 'http://localhost';
            }
        }
    }
});

const routes = [
    {path: '/', component: ServerListView},
    {path: '/webadmin', component: WebAdminView},
    {path: '/serverlist', component: ServerListView},
    {path: '/webadmin/account', component: AccountSettingsView}
]

const router = new VueRouter({routes});

window.routeChangeListeners = [];
router.onReady(() => window.currentRoute = router.currentRoute.path);
router.afterEach((to) =>
    window.routeChangeListeners.forEach(listener => listener(to.path)));
window.router = router;

import vuetify from './plugins/vuetify';
import ServerListView from "./components/serverlist/ServerListView";
import WebAdminView from "./components/webadmin/WebAdminView";
import AccountSettingsView from "./components/webadmin/AccountSettingsView";

new Vue({
    router,
    vuetify,
    render: h => h(App)
}).$mount('#app')
