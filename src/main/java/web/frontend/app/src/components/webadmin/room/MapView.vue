<template>
    <div>
        <div style="display: flex; flex-direction: row;">
            <div style="height: 100%; width: 100%; color: black;">
                <div class="md-layout-item">
                    <p>Current Map: <b>{{current}}</b></p>
                </div>
            </div>

            <div style="height: 100%; width: 100%; color: black;">
                <v-combobox
                        v-model="selectedMap"
                        :items="rotation"
                        label="Change Map"
                        dense outlined></v-combobox>
                <v-btn small color="primary" :disabled="changeMapDisabled" @click="changeMapClick">Change Map</v-btn>
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
