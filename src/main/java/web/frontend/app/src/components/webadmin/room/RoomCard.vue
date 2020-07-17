<template>
    <div>
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
                    <v-btn class="ma-2" tile text color="blue" @click="$emit('showRoomCreationView')">
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
                    </div>
                </div>

            </v-card-text>
        </v-card>

        <template v-if="someRoomIsSelected">

            <RoomPlayersCard :blue-team="selected.blueTeam"
                             :red-team="selected.redTeam"></RoomPlayersCard>

            <RoomBlacklistCard :room-id="selected.roomID"
                               :blacklist="selected.blackList"
                               @removeFromBlacklist="playerId => selected.blackList = selected.blackList.filter(p => p.pid !== playerId)"></RoomBlacklistCard>

            <RoomMapCard :room-id="selected.roomID" :dsc="selected.dsc"
                         :map="selected.map" :map-rotation="selected.mapRotation"></RoomMapCard>

            <RoomExtraDataCard :selected="selected"></RoomExtraDataCard>

        </template>
    </div>
</template>

<script>
    import RoomPlayersCard from "./RoomPlayersCard";
    import RoomBlacklistCard from "./RoomBlacklistCard";
    import RoomMapCard from "./RoomMapCard";
    import RoomExtraDataCard from "./RoomExtraDataCard";

    export default {
        name: "RoomCard",
        components: {RoomExtraDataCard, RoomMapCard, RoomBlacklistCard, RoomPlayersCard},
        created() {
            this.load();
        },
        data() {
            return {
                loading: true,
                headers: [
                    {text: 'Name', value: 'roomName'},
                    {text: 'Game Mode', value: 'gameMode'},
                    {text: 'Active Map', value: 'map'},
                ],
                rooms: [],
                selectedRow: null,
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
            load() {
                this.rooms = [];
                this.loading = true;

                fetch(this.serverIp + '/api/getrooms', {
                    headers: {"token": localStorage.token}
                })
                    .then(data => data.json())
                    .then(data => {
                        this.rooms = data.rooms ? data.rooms.map(room => Object.assign(room, {gameMode: this.getGameModeString(room.gameMode)})) : [];
                        this.loading = false;
                    });
            },
            setSelectedRow(key) {
                this.selectedRow = this.selectedRow === key ? null : key;
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
            }
        }
    }
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
