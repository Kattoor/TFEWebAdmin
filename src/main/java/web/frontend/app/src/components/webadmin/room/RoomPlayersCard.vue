<template>
    <v-card width="60%" class="mx-auto mt-5">
        <v-card-title class="pb-0">
            <div class="overline mb-4">Players</div>
        </v-card-title>
        <v-card-text>
            <PlayersView :red="redTeam" :blue="blueTeam"
                         @kickedRedPlayer="(playerId, displayName) => kickPlayer('red', playerId, displayName)"
                         @kickedBluePlayer="(playerId, displayName) => kickPlayer('blue', playerId, displayName)"/>
        </v-card-text>
    </v-card>
</template>

<script>
    import PlayersView from "./PlayersView";
    export default {
        name: "RoomPlayersCard",
        components: {PlayersView},
        props: ['blueTeam', 'redTeam'],
        methods: {
            kickPlayer(team, playerId, displayName) {
                if (team === 'blue')
                    this.selected.blueTeam = this.selected.blueTeam.filter(p => p.pid !== playerId);
                else
                    this.selected.redTeam = this.selected.redTeam.filter(p => p.pid !== playerId);

                this.selected.blackList.push({pid: playerId, displayName});
            }
        }
    }
</script>
