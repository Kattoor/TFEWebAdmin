<template>
    <div style="width: 100%;" class="fill-height">
        <v-container style="margin-top: 50px;">
            <div style="display: flex; width: 100%; justify-content: center">
                <v-flex d-flex lg3 sm6 xs12 style="margin-right: 35px;">
                    <widget icon="mdi-account-network" :title="playerCount" subTitle='Currently'
                            supTitle="players online"
                            color="rgb(0, 178, 151)"/>
                </v-flex>

                <v-flex d-flex lg3 sm6 xs12 style="margin-left: 35px;">
                    <widget icon="mdi-server-network" title="100" subTitle='Currently' supTitle="servers online"
                            color="rgb(220, 53, 69)"/>
                </v-flex>
            </div>
            <line-chart :chart-data="data"></line-chart>
        </v-container>
    </div>
</template>

<script>
    import Widget from "../webadmin/Widget";
    import LineChart from '../webadmin/LineChart'

    export default {
        name: "ServerListView",
        components: {Widget, LineChart},
        created() {
            fetch(this.serverIp + '/api/serverlist/allplayercount', {
                headers: {"token": localStorage.token}
            })
                .then(data => data.text())
                .then(data => this.playerCount = JSON.parse(data).response['player_count'] + '');


            fetch(this.serverIp + '/api/getplayercounts', {
                headers: {"token": localStorage.token}
            })
                .then(data => data.json())
                .then(data => {
                    const obj = {};
                    data.forEach(record => obj[record.time] = record.count);
                    this.data = obj;
                });
        },
        data() {
            return {
                playerCount: '0',
                data: {
                    '2018-05-13': 640,
                    '2018-05-14': 200,
                    '2018-05-15': 250,
                    '2018-05-16': 350,
                    '2018-05-17': 200,
                    '2018-05-18': 670,
                    '2018-05-19': 600,
                    '2018-05-20': 800
                },
                chartOptions: {},
                myStyles: {
                    height: `250px`,
                    position: 'relative'
                }
            };
        }
    };
</script>
