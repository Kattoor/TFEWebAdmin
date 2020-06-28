<template>
    <div class="roww">
        <h1 class="md-title">Map</h1>

        <div class="md-layout">
            <div class="md-layout-item">
                <p>Current Map: <b>{{current}}</b></p>
            </div>

            <div class="md-layout-item">
                <md-field style="width: 50%;">
                    <label>Change Map</label>
                    <md-select v-model="selectedMap" name="Change Map" id="combobox">
                        <md-option v-for="map in rotation" :key="map" :value="map">{{map}}</md-option>
                    </md-select>
                </md-field>
                <md-button class="md-raised md-primary" :disabled="changeMapDisabled" @click="changeMapClick()">Change
                    Map
                </md-button>
            </div>
        </div>
    </div>
</template>

<script>
    export default {
        name: "MapView",
        props: ['current', 'rotation', 'room', 'dsc'],
        watch: {
            rotation: function () {
                this.selectedMap = '';
            }
        },
        data() {
            return {
                selectedMap: ''
            }
        },
        computed: {
            changeMapDisabled: function () {
                return this.selectedMap === '' || this.selectedMap.toLowerCase() === this.current.toLowerCase();
            }
        },
        methods: {
            getClass(map) {
                return {'accent': this.current === map};
            },
            changeMapClick() {
                fetch(this.serverIp + '/api/changemap?roomId=' + this.room + '&dsc=' + this.dsc + '&map=' + this.selectedMap, {
                    headers: {"token": localStorage.token}
                })
                    .then(data => data.text())
                    .then(data => {
                        console.log(data);
                    });
            }
        }
    }
</script>

<style lang="scss" scoped>
    .accent {
        color: #ff1f1f;
        font-weight: bold;
    }
</style>
