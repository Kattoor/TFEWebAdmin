import Vue from 'vue'
import App from './App.vue'

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

import VueMaterial from 'vue-material'
import 'vue-material/dist/vue-material.min.css'
import 'vue-material/dist/theme/default.css'

Vue.use(VueMaterial)

new Vue({
  render: h => h(App),
}).$mount('#app')
