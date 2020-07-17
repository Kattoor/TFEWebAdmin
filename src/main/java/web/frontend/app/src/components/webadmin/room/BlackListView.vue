<template>
    <div style="display: flex; flex-direction: row;">
        <div style="height: 100%; width: 100%; color: black;">
            <span style="font-weight: bold; margin-bottom: 15px; display: inline-block">Blacklisted Players: {{blackList.length}}</span>
            <v-hover v-slot:default="{ hover }" v-for="player in blackList" :key="player.displayName">
                <div style="display: flex; align-items: center; height: 36px;">
                    <p style="margin: 0 5px;">{{player.displayName}}</p>
                    <v-btn v-if="hover" icon color="red" @click="removePlayerFromBlackList(player.pid)">
                        <v-icon>mdi-account-remove</v-icon>
                    </v-btn>
                </div>
            </v-hover>
        </div>
    </div>
</template>

<script>
    export default {
        name: "BlackListView",
        props: ['blackList', 'roomId'],
        methods: {
            removePlayerFromBlackList(playerId) {
                fetch(this.serverIp + '/api/removefromblacklist?roomId=' + this.roomId + '&playerId=' + playerId, {
                    headers: {"token": localStorage.token}
                })
                    .then(data => data.text())
                    .then(() => {
                        this.$emit('removedFromBlackList', playerId);
                    });
            }
        }
    }
</script>
