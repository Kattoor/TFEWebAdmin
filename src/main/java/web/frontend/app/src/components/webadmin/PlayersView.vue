<template>
    <div style="display: flex; flex-direction: row;">
        <div style="height: 100%; width: 100%; color: black;">
            <span style="font-weight: bold; margin-bottom: 15px; display: inline-block">Task Force Elite: {{blue.length}}</span>
            <v-hover v-slot:default="{ hover }" v-for="player in blue" :key="player.displayName">
                <div style="display: flex; align-items: center; height: 36px;">
                    <p style="margin: 0 5px;">{{player.displayName}}</p>
                    <v-btn v-if="hover" icon color="red" @click="kickPlayer(player.pid)">
                        <v-icon>mdi-account-remove</v-icon>
                    </v-btn>
                </div>
            </v-hover>
        </div>

        <div style="height: 100%; width: 100%; color: black;">
            <span style="font-weight: bold; margin-bottom: 15px; display: inline-block">Red Spear: {{red.length}}</span>
            <v-hover v-slot:default="{ hover }" v-for="player in red" :key="player.displayName">
                <div style="display: flex; align-items: center; height: 36px;">
                    <p style="margin: 0 5px;">{{player.displayName}}</p>
                    <v-btn v-if="hover" icon color="red" @click="kickPlayer(player.pid)">
                        <v-icon>mdi-account-remove</v-icon>
                    </v-btn>
                </div>
            </v-hover>
        </div>
    </div>
</template>

<script>
    export default {
        name: "PlayersView",
        props: ['red', 'blue'],
        methods: {
            kickPlayer(playerId) {
                fetch(this.serverIp + '/api/kickplayer?playerId=' + playerId, {
                    headers: {"token": localStorage.token}
                })
                    .then(data => data.text())
                    .then(() => {
                        this.red = this.red.filter(p => p.pid !== playerId);
                        this.blue = this.blue.filter(p => p.pid !== playerId);
                    });
            }
        }
    }
</script>
