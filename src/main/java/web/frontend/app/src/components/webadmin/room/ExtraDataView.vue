<template>
    <div>
        <div class="room-data-wrapper">
            <p>RoomID: {{ selected.roomID}}</p>
            <p>SessionType: {{ selected.sessionType }}</p>
            <p>Allow Spectators: {{selected.bAllowSpectator}}</p>
            <p>Enable Word Censor: {{selected.bEnableWordCensorship}}</p>
            <p>Save Chat Log: {{selected.bSaveChatLog}}</p>
            <p>Blacklist: {{selected.blackList}}</p>
            <p>Blue Team: {{selected.blueTeam}}</p>
            <p>Red Team: {{selected.redTeam}}</p>
            <p>Game Length: {{selected.gameLength}}</p>
            <p>Goal Taken Time: {{selected.goalTakenTime}}</p>
            <p>Injury Time: {{selected.injuryTime}}</p>
            <p>Map Rotation: {{selected.mapRotation}}</p>
            <p>Max Player: {{selected.maxPlayer}}</p>
            <p>Max Score: {{selected.maxScore}}</p>
            <p>Number of Bots: {{selected.numOfBots}}</p>
            <p v-if="selected.password !== ''">Password: {{selected.password}}</p>
            <p>PSP Taking Time: {{selected.pspTakingTime}}</p>
            <p>Region: {{selected.region}}</p>
            <p>Spawn Protection Time: {{selected.spawnProtectionTime}}</p>
            <p>Steam Session: {{selected.steamSession}}</p>
            <p>Time Between Matches: {{selected.timeBetweenMatches}}</p>
            <p>Warmup Time: {{selected.warmupTime}}</p>
            <p>Weapon Rules: {{selected.weaponRules}}</p>
        </div>
    </div>
</template>

<script>
    export default {
        name: "ExtraDataView",
        props: ['selected']
    }
</script>

<style lang="scss" scoped>

    .md-layout-item {
        height: 40px;
    }
</style>