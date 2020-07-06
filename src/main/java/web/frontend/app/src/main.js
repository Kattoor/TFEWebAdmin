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
                return 'http://localhost:80';
            }
        }
    }
});

const routes = [
    {path: '/', component: ServerListView},
    {path: '/webadmin', component: WebAdminView},
    {path: '/serverlist', component: ServerListView}
]

const router = new VueRouter({routes})

import vuetify from './plugins/vuetify';
import ServerListView from "./components/serverlist/ServerListView";
import WebAdminView from "./components/webadmin/WebAdminView";

new Vue({
    router,
    vuetify,
    render: h => h(App)
}).$mount('#app')
