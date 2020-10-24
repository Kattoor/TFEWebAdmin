<template>
    <div>
        <v-card width="60%" class="mx-auto mt-5">
            <v-card-title class="pb-0">
                <v-btn icon color="blue" @click="$emit('cancel')" style="margin-bottom: 16px">
                    <v-icon>mdi-arrow-left</v-icon>
                </v-btn>
                <v-btn icon color="blue" @click="$emit('reload')" style="margin-bottom: 16px; margin-right: 5px;">
                    <v-icon>mdi-refresh</v-icon>
                </v-btn>
                <div class="overline mb-4">ROOM: {{selected.roomName}}</div>
            </v-card-title>
        </v-card>

        <v-card width="60%" class="mx-auto mt-5">
            <v-card-title class="pb-0">
                <div class="overline mb-4">Players</div>
            </v-card-title>
            <v-card-text>
                <RoomPlayersCard :blue-team="selected.blueTeam"
                                 :red-team="selected.redTeam"></RoomPlayersCard>
            </v-card-text>
        </v-card>

        <v-card width="60%" class="mx-auto mt-5">
            <v-card-title class="pb-0">
                <div class="overline mb-4">Blacklist</div>
            </v-card-title>
            <v-card-text>
                <RoomBlacklistCard :room-id="selected.roomID"
                                   :blacklist="selected.blackList"
                                   @removeFromBlacklist="playerId => selected.blackList = selected.blackList.filter(p => p.pid !== playerId)"></RoomBlacklistCard>
            </v-card-text>
        </v-card>

        <v-card width="60%" class="mx-auto mt-5">
            <v-card-title class="pb-0">
                <div class="overline mb-4">Map</div>
            </v-card-title>
            <v-card-text>
                <RoomMapCard :room-id="selected.roomID" :dsc="selected.dsc"
                             :map="selected.map" :map-rotation="selected.mapRotation"></RoomMapCard>
            </v-card-text>
        </v-card>

        <v-card width="60%" class="mx-auto mt-5">
            <v-card-title class="pb-0">
                <div class="overline mb-4">Extra Data</div>
            </v-card-title>
            <v-card-text>
                <RoomExtraDataCard :selected="selected"></RoomExtraDataCard>
            </v-card-text>
        </v-card>
    </div>
</template>

<script>
    import RoomPlayersCard from "./cards/RoomPlayersCard";
    import RoomBlacklistCard from "./cards/RoomBlacklistCard";
    import RoomMapCard from "./cards/RoomMapCard";
    import RoomExtraDataCard from "./cards/RoomExtraDataCard";

    export default {
        name: "RoomDetailsView",
        components: {RoomExtraDataCard, RoomMapCard, RoomBlacklistCard, RoomPlayersCard},
        props: ['selected']
    }
</script>
