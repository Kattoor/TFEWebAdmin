<template>
    <div>
        <div style="margin-top: 50px;" v-if="!showRoomCreationView">
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
                            <tr v-for="(item, key) in items"
                                :class="key === selectedRow ? 'blue white-text' : ''"
                                :key="item.name"
                                @click="setSelectedRow(key)">
                                <td>{{ item.roomName }}</td>
                                <td>{{ item.gameMode }}</td>
                                <td>{{ item.map }}</td>
                            </tr>
                            </tbody>
                        </template>
                    </v-data-table>

                    <div style="width: 100%; display: flex; flex-direction: row; justify-content: space-between">
                        <v-btn class="ma-2" tile text color="blue" @click="createRoom()">
                            CREATE
                            <v-icon style="margin-left: 2px;">mdi-plus</v-icon>
                        </v-btn>

                        <div v-if="someRoomIsSelected">
                            <v-btn class="ma-2" tile text color="red" @click="restartRoom(selected)">
                                RESTART
                                <v-icon style="margin-left: 2px;">mdi-backup-restore</v-icon>
                            </v-btn>
                            <v-btn class="ma-2" tile text color="red" @click="closeRoom(selected)">
                                CLOSE
                                <v-icon style="margin-left: 2px;">mdi-delete</v-icon>
                            </v-btn>

                            <!--                        <md-button class="md-accent" @click="restartRoom(selected)">
                                                        <span style="vertical-align: middle; padding-right: 5px;">RESTART</span>
                                                        <md-icon>settings_backup_restore</md-icon>
                                                    </md-button>
                                                    <md-button class="md-accent" style="margin-left: 8px;" @click="closeRoom(selected)">
                                                        <span style="vertical-align: middle; padding-right: 5px;">CLOSE</span>
                                                        <md-icon>delete</md-icon>
                                                    </md-button>-->
                        </div>
                    </div>

                </v-card-text>
            </v-card>

            <v-card v-if="someRoomIsSelected" width="60%" class="mx-auto mt-5">
                <v-card-title class="pb-0">
                    <div class="overline mb-4">Players</div>
                </v-card-title>
                <v-card-text>
                    <PlayersView :red="selected.redTeam" :blue="selected.blueTeam"/>
                </v-card-text>
            </v-card>

            <v-card v-if="someRoomIsSelected" width="60%" class="mx-auto mt-5">
                <v-card-title class="pb-0">
                    <div class="overline mb-4">Map</div>
                </v-card-title>
                <v-card-text>
                    <MapView :room="selected.roomID" :dsc="selected.dsc" :current="selected.map"
                             :rotation="selected.mapRotation"/>
                </v-card-text>
            </v-card>

            <v-card v-if="someRoomIsSelected" width="60%" class="mx-auto mt-5">
                <v-card-title class="pb-0">
                    <div class="overline mb-4">Extra Data</div>
                </v-card-title>
                <v-card-text>
                    <ExtraDataView :selected="selected"/>
                </v-card-text>
            </v-card>

            <v-card v-if="someRoomIsSelected" width="60%" class="mx-auto mt-5">
                <v-card-title class="pb-0">
                    <div class="overline mb-4">Create Room</div>
                </v-card-title>
                <v-card-text>
                </v-card-text>
            </v-card>
        </div>

        <div v-if="showRoomCreationView">
            <CreateMapView @created="showRoomCreationView = false" @cancel="showRoomCreationView = false"/>
        </div>
    </div>
</template>

<script>

    import PlayersView from "./PlayersView";
    import MapView from "./MapView";
    import ExtraDataView from "./ExtraDataView";
    import CreateMapView from "./CreateMapView";

    export default {
        name: "HelloWorld",
        components: {PlayersView, MapView, ExtraDataView, CreateMapView},
        created() {
            this.load();
        },
        data() {
            return {
                selectedRow: null,

                rooms: [],
                loading: false,
                showRoomCreationView: false,

                headers: [
                    {text: 'Name', value: 'roomName'},
                    {text: 'Game Mode', value: 'gameMode'},
                    {text: 'Active Map', value: 'map'},
                ]
            };
        },
        computed: {
            someRoomIsSelected: function () {
                return this.selected && this.selected.roomID;
            },
            selected: function () {
                return this.rooms[this.selectedRow];
            }
        },
        methods: {
            setSelectedRow(key) {
                this.selectedRow = this.selectedRow === key ? null : key;
            },
            load() {
                const startTime = new Date().getMilliseconds();
                this.rooms = [];
                this.loading = true;
                fetch(this.serverIp + '/api/getrooms', {
                    headers: {"token": localStorage.token}
                })
                    .then(data => data.json())
                    .then(data => {
                        setTimeout(() => {
                            this.rooms = data.rooms.map(room => Object.assign(room, {gameMode: this.getGameModeString(room.gameMode)}));
                            this.loading = false;
                        }, 1000 - (new Date().getMilliseconds() - startTime));
                    });
            },
            getClass(item) {
                return {'md-primary': this.selected && item.roomID === this.selected.roomID};
            },
            getGameModeString(gameMode) {
                return ['DM', 'TDM', 'TKOTH', 'TC'][gameMode];
            },
            closeRoom(item) {
                fetch(this.serverIp + '/api/closeroom?id=' + item.roomID, {
                    headers: {"token": localStorage.token}
                })
                    .then(data => data.text())
                    .then(() => {
                        this.load();
                    });
            },
            restartRoom(item) {
                fetch(this.serverIp + '/api/restartroom?id=' + item.roomID, {
                    headers: {"token": localStorage.token}
                })
                    .then(data => data.text())
                    .then(() => {
                        this.load();
                    });
            },
            createRoom() {
                this.showRoomCreationView = true;
            }
        }
    };
</script>

<style lang="scss" scoped>
    .white-text {
        color: white;
        font-weight: bold;
    }

    tr :hover {
        cursor: pointer;
    }
</style>
