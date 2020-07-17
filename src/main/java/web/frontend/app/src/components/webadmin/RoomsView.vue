<template>
    <div>
        <div style="margin-top: 50px;" v-if="!showRoomCreationView && !showAccountCreationView">
            <AccountCard @showAccountCreationView="showAccountCreationView = true"></AccountCard>

            <RoomCard @showRoomCreationView="showRoomCreationView = true"></RoomCard>
        </div>

        <div v-if="showRoomCreationView">
            <CreateMapView @created="showRoomCreationView = false" @cancel="showRoomCreationView = false"/>
        </div>

        <div v-if="showAccountCreationView">
            <AccountCreationView @created="showAccountCreationView = false" @cancel="showAccountCreationView = false"/>
        </div>
    </div>
</template>

<script>
    import CreateMapView from "./CreateMapView";
    import AccountCreationView from "./account/AccountCreationView";
    import AccountCard from "./account/AccountCard";
    import RoomCard from "./room/RoomCard";

    export default {
        name: "HelloWorld",
        components: {
            RoomCard, AccountCard, AccountCreationView, CreateMapView
        },

        data() {
            return {
                username: localStorage.username,

                showRoomCreationView: false,
                showAccountCreationView: false,
            };
        }
    };
</script>
