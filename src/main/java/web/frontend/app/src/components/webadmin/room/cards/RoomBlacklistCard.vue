<template>
    <div>
        <v-card-text>
            <BlackListView :roomId="roomId" :blackList="blacklist"
                           @removedFromBlackList="playerId => $emit('removeFromBlacklist', playerId)"/>
        </v-card-text>
    </div>
</template>

<script>
    import BlackListView from "../BlackListView";

    export default {
        name: "RoomBlacklistCard",
        components: {BlackListView},
        props: ['roomId', 'blacklist'],
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
