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
                        <tr v-for="(item, key) in (selectedRow !== null ? insertIntoArray(items, selectedRow + 1, {expandedSection: true}) : items)"
                            :class="getClass(item, key)"
                            :key="item.name"
                            @click="!item.expandedSection && setSelectedRow(selectedRow === null || key <= selectedRow ? key : key - 1)">
                            <template v-if="!item.expandedSection">
                                <td>{{ item.roomName }}</td>
                                <td>{{ item.gameMode }}</td>
                                <td>{{ item.map }}</td>
                                <td>{{item.blueTeam.length + item.redTeam.length}}</td>
                            </template>
                            <template v-else>
                                <td v-if="someRoomIsSelected" :colspan="headers.length"
                                    style="padding-left: 20px; padding-right: 20px; background-color: #F5F5F6">
                                    <RoomPlayersCard :blue-team="selected.blueTeam"
                                                     :red-team="selected.redTeam"></RoomPlayersCard>
                                </td>
                            </template>
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
                        <v-btn class="ma-2" tile text color="red" @click="manageRoom(selected)">
                            MANAGE
                            <v-icon style="margin-left: 2px;">mdi-cog</v-icon>
                        </v-btn>
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
    </div>
</template>

<script>
    import RoomPlayersCard from "./RoomPlayersCard";
    /*import RoomBlacklistCard from "./RoomBlacklistCard";
    import RoomMapCard from "./RoomMapCard";
    import RoomExtraDataCard from "./RoomExtraDataCard";*/

    export default {
        name: "RoomCard",
        components: {/*RoomExtraDataCard, RoomMapCard, RoomBlacklistCard, */RoomPlayersCard},
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
                    {text: '# players', value: 'playercount'}
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
            },
            manageRoom(item) {
                this.$emit('showRoomDetails', item);
            },
            insertIntoArray(arr, index, newItem) {
                return [...arr.slice(0, index), newItem, ...arr.slice(index)];
            },
            getClass(item, key) {
                let classList = '';
                if (this.selectedRow !== null && key === this.selectedRow)
                    classList += 'primary-background white-text';
                if (!item.expandedSection)
                    classList += ' pointer-on-hover';
                if (item.expandedSection)
                    classList += ' expanded-section';
                return classList;
            }
        }
    }
</script>

<style lang="scss" scoped>
    .primary-background {
        background-color: #2196f3 !important;
    }

    .white-text {
        color: white;
        font-weight: bold;
    }

    .pointer-on-hover :hover {
        cursor: pointer;
    }

    .expanded-section :hover {
        background: #F5F5F6;
    }
</style>
