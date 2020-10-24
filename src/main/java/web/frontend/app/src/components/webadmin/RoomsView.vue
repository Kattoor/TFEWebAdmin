<template>
    <div>
        <div style="margin-top: 50px;" v-if="!showRoomCreationView && !showAccountCreationView && !showRoomDetails">
            <AccountCard @showAccountCreationView="showAccountCreationView = true"></AccountCard>
            <RoomCard @showRoomCreationView="showRoomCreationView = true"
                      @showRoomDetails="doShowRoomDetails"></RoomCard>
        </div>

        <div v-if="showRoomCreationView">
            <CreateMapView @created="showRoomCreationView = false" @cancel="showRoomCreationView = false"/>
        </div>

        <div v-if="showAccountCreationView">
            <AccountCreationView @created="showAccountCreationView = false" @cancel="showAccountCreationView = false"/>
        </div>

        <div v-if="showRoomDetails">
            <RoomDetailsView :selected="roomToShowDetailsFor" @reload="reloadRoomDetailsRoom()"
                             @cancel="showRoomDetails = false"></RoomDetailsView>
        </div>
    </div>
</template>

<script>
    import CreateMapView from "./CreateMapView";
    import AccountCreationView from "./account/AccountCreationView";
    import AccountCard from "./account/AccountCard";
    import RoomCard from "./room/cards/RoomCard";
    import RoomDetailsView from "./room/RoomDetailsView";

    export default {
        name: "HelloWorld",
        components: {
            RoomDetailsView,
            RoomCard, AccountCard, AccountCreationView, CreateMapView
        },

        data() {
            return {
                username: localStorage.username,

                showRoomCreationView: false,

                showAccountCreationView: false,

                showRoomDetails: false,
                roomToShowDetailsFor: null
            };
        },

        methods: {
            doShowRoomDetails(room) {
                this.roomToShowDetailsFor = room;
                this.showRoomDetails = true;
            },
            reloadRoomDetailsRoom() {
                fetch(this.serverIp + '/api/getrooms', {
                    headers: {"token": localStorage.token}
                })
                    .then(data => data.json())
                    .then(data => this.roomToShowDetailsFor = data.rooms.filter(room => room.roomID === this.roomToShowDetailsFor.roomID).map(room => Object.assign(room, {gameMode: this.getGameModeString(room.gameMode)}))[0]);
            },
            getGameModeString(gameMode) {
                return ['DM', 'TDM', 'TKOTH', 'TC'][gameMode];
            }
        }
    };
</script>
