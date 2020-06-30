import Vue from 'vue'
import App from './App'

Vue.config.productionTip = false

Vue.mixin({
  data: function() {
    return {
      get serverIp() {
        //return 'http://161.35.95.41';
        return 'http://localhost';
      }
    }
  }
});

import vuetify from './plugins/vuetify';

new Vue({
  vuetify,
  render: h => h(App)
}).$mount('#app')
