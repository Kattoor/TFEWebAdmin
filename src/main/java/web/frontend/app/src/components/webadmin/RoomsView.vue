<template>
    <div style="position: relative; width: 60%; margin: auto;">
        <div style="width: 100%; display: flex; flex-direction: column; ">
            <div v-if="!showDialog" style="margin: 10% 20px 20px;">
                <div class="md-size-100">
                    <md-content style="padding: 20px;" class="md-elevation-3">
                        <div>
                            <h1 class="md-title" style="vertical-align: middle">
                                <span style="vertical-align:middle;">Rooms</span>
                                <md-button class="md-icon-button md-dense md-primary" @click="load()">
                                    <md-icon>refresh</md-icon>
                                    <md-tooltip md-direction="right">Loading is actually faster, I slowed this down to
                                        show the animation lol
                                    </md-tooltip>
                                </md-button>
                            </h1>
                            <div v-if="loading"
                                 style="width: 100%; min-height: 100px; display: flex; flex-direction: row; align-items: center; justify-content: center">
                                <md-progress-spinner :md-diameter="30" :md-stroke="3"
                                                     md-mode="indeterminate"></md-progress-spinner>
                            </div>
                            <md-table v-if="!loading" v-model="rooms" @md-selected="onSelect">
                                <md-table-row slot="md-table-row" slot-scope="{ item }" :class="getClass(item)"
                                              md-selectable="single">
                                    <md-table-cell md-label="Name">{{item.roomName}}</md-table-cell>
                                    <md-table-cell md-label="Game Mode">{{getGameModeString(item.gameMode)}}
                                    </md-table-cell>
                                    <md-table-cell md-label="Active Map">{{item.map}}</md-table-cell>
                                </md-table-row>
                            </md-table>
                            <div style="display: flex; justify-content: space-between">
                                <md-button class="md-primary" @click="createRoom()">
                                    <span style="vertical-align: middle; padding-right: 5px;">CREATE</span>
                                    <md-icon>add</md-icon>
                                </md-button>
                                <div v-if="someRoomIsSelected">
                                    <md-button class="md-accent" @click="restartRoom(selected)">
                                        <span style="vertical-align: middle; padding-right: 5px;">RESTART</span>
                                        <md-icon>settings_backup_restore</md-icon>
                                    </md-button>
                                    <md-button class="md-accent" style="margin-left: 8px;" @click="closeRoom(selected)">
                                        <span style="vertical-align: middle; padding-right: 5px;">CLOSE</span>
                                        <md-icon>delete</md-icon>
                                    </md-button>
                                </div>
                            </div>
                        </div>
                    </md-content>
                </div>
            </div>

            <md-content v-if="!showDialog && someRoomIsSelected" style="padding: 20px; margin: 20px;"
                        class="md-elevation-3">
                <PlayersView :red="selected.redTeam" :blue="selected.blueTeam"/>
            </md-content>

            <div/>

            <md-content v-if="!showDialog && someRoomIsSelected" style="padding: 20px; margin: 20px;"
                        class="md-elevation-3">
                <MapView :room="selected.roomID" :dsc="selected.dsc" :current="selected.map"
                         :rotation="selected.mapRotation"/>
            </md-content>

            <div/>

            <md-content v-if="!showDialog && someRoomIsSelected" style="padding: 20px; margin: 20px;"
                        class="md-elevation-3">
                <ExtraDataView :selected="selected"/>
            </md-content>

            <div v-if="showDialog" style="margin: 20px;">
                <md-button class="md-primary" @click="showDialog = false" style="margin-left: 0; margin-bottom: 20px;">
                    <md-icon>arrow_back</md-icon>
                    <span style="vertical-align: middle; padding-left: 5px;">BACK</span>
                </md-button>

                <md-content class="md-elevation-3">
                    <CreateMapView @created="showDialog = false"/>
                </md-content>
            </div>
        </div>
    </div>
</template>

<script>
    import PlayersView from "./PlayersView";
    import ExtraDataView from "./ExtraDataView";
    import MapView from "./MapView";
    import CreateMapView from "./CreateMapView";

    export default {
        name: "HelloWorld",
        components: {CreateMapView, PlayersView, ExtraDataView, MapView},
        created() {
            this.load();
        },
        data() {
            return {
                rooms: [],
                selected: {},
                loading: true,
                showDialog: false
            };
        },
        computed: {
            someRoomIsSelected: function () {
                return this.selected && this.selected.roomID;
            },
        },
        methods: {
            load() {
                const startTime = new Date().getMilliseconds();
                this.selected = null;
                this.rooms = [];
                this.loading = true;
                fetch(this.serverIp + '/api/getrooms', {
                    headers: {"token": localStorage.token}
                })
                    .then(data => data.json())
                    .then(data => {
                        setTimeout(() => {
                            this.rooms = data.rooms;
                            this.loading = false;
                        }, 1000 - (new Date().getMilliseconds() - startTime));
                    });
            },
            onSelect(item) {
                this.selected = item;
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
                this.showDialog = true;
            }
        }
    };
</script>

<style lang="scss" scoped>
    /*    div {
            width: 80%;
            padding: 20px 0;
            margin: auto;
            position: relative;;
        }

        .md-content {
            z-index: 1;
        }

        .loading-overlay {
            z-index: 10;
            top: 0;
            left: 0;
            right: 0;
            position: absolute;
            width: 100%;
            height: 100%;
            background: rgba(255, 255, 255, 0.9);
            display: flex;
            align-items: center;
            justify-content: center;
        }

        .room-data-wrapper {
            width: 80%;
            margin: auto;
        }*/
</style>
