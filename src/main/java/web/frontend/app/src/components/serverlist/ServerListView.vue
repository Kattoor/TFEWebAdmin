<template>
    <div style="width: 80%; display: flex; flex-direction: column; justify-content: center; margin: auto;">
        <div style="margin-top: 50px;">
            <v-card width="60%" class="mx-auto mt-5" :loading="loading">
                <v-card-title class="pb-0">
                    <div class="overline mb-4">ROOMS</div>
                    <v-btn icon color="blue" @click="load()" style="margin-bottom: 16px;">
                        <v-icon>mdi-refresh</v-icon>
                    </v-btn>
                </v-card-title>
                <v-card-text>
                    <v-data-table
                            single-select
                            :headers="headers"
                            :items="rooms"
                            hide-default-footer
                            disable-pagination
                            disable-filtering
                            disable-sort>
                        <template v-slot:body="{ items }">
                            <tbody>
                            <tr v-for="(item, key) in (selectedRow !== null ? insertIntoArray(items, selectedRow + 1, {expandedSection: true}) : items)"
                                :class="getClass(item, key)"
                                :key="item.name"
                                @click="!item.expandedSection && setSelectedRow(selectedRow === null || key <= selectedRow ? key : key - 1)">
                                <template v-if="!item.expandedSection">
                                    <td><img :src="getCountryFlag(item.cc)" alt="Country flag"></td>
                                    <td>{{ item.roomName }}</td>
                                    <td>{{ item.gameMode }}</td>
                                    <td>{{ item.map }}</td>
                                    <td>{{item.blueTeam.length + item.redTeam.length}}</td>
                                </template>
                                <template v-else>
                                    <td :colspan="headers.length">
                                        <div style="display: flex; flex-direction: row; padding: 15px;">
                                            <div style="height: 100%; width: 100%; color: black;">
                                                <span style="font-weight: bold; margin-bottom: 15px; display: inline-block">Task Force Elite: {{items[selectedRow].blueTeam.length}}</span>
                                                <div v-for="player in items[selectedRow].blueTeam" :key="player.displayName">
                                                    <div style="display: flex; align-items: center; height: 36px;">
                                                        <p style="margin: 0 5px;">{{player.displayName}}</p>
                                                    </div>
                                                </div>
                                            </div>
                                            <div style="height: 100%; width: 100%; color: black;">
                                                <span style="font-weight: bold; margin-bottom: 15px; display: inline-block">Red Spear: {{items[selectedRow].redTeam.length}}</span>
                                                <div v-for="player in items[selectedRow].redTeam" :key="player.displayName">
                                                    <div style="display: flex; align-items: center; height: 36px;">
                                                        <p style="margin: 0 5px;">{{player.displayName}}</p>
                                                    </div>
                                                </div>
                                            </div>
                                        </div>
                                    </td>
                                </template>
                            </tr>
                            </tbody>
                        </template>
                    </v-data-table>
                </v-card-text>
            </v-card>

            <line-chart :chart-data="chartData"></line-chart>
        </div>
    </div>
</template>

<script>
    import LineChart from '../webadmin/LineChart'

    export default {
        name: "ServerListView",
        components: {LineChart},
        created() {
            fetch(this.serverIp + '/api/getplayercounts', {
                headers: {"token": localStorage.token}
            })
                .then(data => data.json())
                .then(data => {
                    const obj = {};
                    data.forEach(record => obj[record.time] = record.count);
                    this.chartData = obj;
                });

            this.load();
        },
        data() {
            return {
                loading: false,

                selectedRow: null,
                headers: [
                    {text: 'Country', value: 'country'},
                    {text: 'Name', value: 'roomName'},
                    {text: 'Game Mode', value: 'gameMode'},
                    {text: 'Active Map', value: 'map'},
                    {text: '# players', value: 'playercount'}
                ],
                rooms: [],

                chartData: {
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
        },
        methods: {
            load() {
                this.rooms = [];
                this.loading = true;
                fetch(this.serverIp + '/api/getroomsforserverlist')
                    .then(data => data.json())
                    .then(data => {
                        this.rooms = [];
                        data.forEach(server => {
                            server.rooms.forEach(room => {
                                this.rooms.push(Object.assign(room, {gameMode: this.getGameModeString(room.gameMode)}));
                            });
                        });

                        this.loading = false;
                    });
            },
            setSelectedRow(key) {
                this.selectedRow = this.selectedRow === key ? null : key;
            },
            getGameModeString(gameMode) {
                return ['DM', 'TDM', 'TKOTH', 'TC'][gameMode];
            },
            getCountryFlag(countryCode) {
                return 'https://www.countryflags.io/' + countryCode + '/flat/48.png';
            },
            insertIntoArray(arr, index, newItem) {
                return [...arr.slice(0, index), newItem, ...arr.slice(index)];
            },
            getClass(item, key) {
                let classList = '';
                if (this.selectedRow !== null && key === this.selectedRow)
                    classList += 'blue white-text';
                if (!item.expandedSection)
                    classList += ' pointer-on-hover'
                return classList;
            }
        },
        computed: {
            selected: function () {
                return this.rooms[this.selectedRow];
            }
        }
    };
</script>

<style lang="scss" scoped>
    .white-text {
        color: white;
    }

    .pointer-on-hover :hover {
        cursor: pointer;
    }
</style>
