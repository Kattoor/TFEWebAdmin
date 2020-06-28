<template>
    <div>
        <h1 class="md-title">Players</h1>

        <div class="md-layout">
            <div class="md-layout-item" style="height: 100%;">
                <b>Task Force Elite: {{blue.length}}</b>
                <br/>
                <p v-for="player in blue" :key="player.displayName">{{player.displayName}}</p>
            </div>

            <div class="md-layout-item" style="height: 100%;">
                <b>Red Spear: {{red.length}}</b>
                <br/>
                <p v-for="player in red" :key="player.displayName">{{player.displayName}}</p>
            </div>
        </div>
    </div>
</template>

<script>
    export default {
        name: "PlayersView",
        props: ['red', 'blue']
    }
</script>

<style lang="scss" scoped>

    .md-layout-item {
        height: 40px;
    }
</style>