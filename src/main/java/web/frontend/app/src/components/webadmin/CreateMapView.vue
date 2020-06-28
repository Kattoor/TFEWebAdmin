<template>
    <div>
        <md-steppers md-alternative style="min-width: 40vw" :md-active-step.sync="currentStep">
            <md-step id="general" md-label="General Settings">
                <div style="display: flex; width: 100%; justify-content: space-between">
                    <md-field style="width: 45%">
                        <label>Lobby Name</label>
                        <md-input v-model="newRoom.roomName"></md-input>
                    </md-field>

                    <md-field style="width: 45%">
                        <label>Game Mode</label>
                        <md-select v-model="newRoom.gameMode" name="Change Map" id="combobox">
                            <md-option value="Deathmatch">Deathmatch</md-option>
                            <md-option value="Team Deathmatch">Team Deathmatch</md-option>
                            <md-option value="Team King of the Hill">Team King of the Hill</md-option>
                            <md-option value="Team Conquer">Team Conquer</md-option>
                        </md-select>
                    </md-field>
                </div>

                <div style="display: flex; width: 100%; justify-content: space-between">
                    <md-field style="width: 45%">
                        <label>Number Of Bots</label>
                        <md-input v-model="newRoom.numOfBots" type="number"></md-input>
                    </md-field>

                    <md-field style="width: 45%">
                        <label>Game Length (seconds)</label>
                        <md-input v-model="newRoom.gameLength" type="number"></md-input>
                    </md-field>
                </div>

                <div style="display: flex; width: 100%; justify-content: space-between">
                    <md-field style="width: 45%">
                        <label>Kill Count</label>
                        <md-input v-model="newRoom.maxScore" type="number"></md-input>
                    </md-field>

                    <md-field style="width: 45%">
                        <label>Max Players</label>
                        <md-input v-model="newRoom.maxPlayer" type="number"></md-input>
                    </md-field>
                </div>

                <div style="display: flex; width: 100%; justify-content: space-between">
                    <md-field style="width: 45%">
                        <label>Warmup Time (seconds)</label>
                        <md-input v-model="newRoom.warmupTime" type="number"></md-input>
                    </md-field>

                    <md-field style="width: 45%">
                        <label>Wounded Timer (seconds - always 120)</label>
                        <md-input v-model="newRoom.injuryTime" type="number"></md-input>
                    </md-field>
                </div>

                <div style="display: flex; width: 100%; justify-content: space-between">
                    <md-field style="width: 45%">
                        <label>Round End Scoreboard Timer (seconds)</label>
                        <md-input v-model="newRoom.timeBetweenMatches" type="number"></md-input>
                    </md-field>

                    <md-field style="width: 45%">
                        <label>Spawn Armor (seconds)</label>
                        <md-input v-model="newRoom.spawnProtectionTime" type="number"></md-input>
                    </md-field>
                </div>

                <div style="display: flex; width: 100%; justify-content: space-between">
                    <md-field style="width: 45%">
                        <label>Region (always local)</label>
                        <md-input v-model="newRoom.region"></md-input>
                    </md-field>

                    <md-field style="width: 45%">
                        <label>Password</label>
                        <md-input v-model="newRoom.password"></md-input>
                    </md-field>
                </div>

                <div style="display: flex; width: 100%; justify-content: space-between">
                    <md-field style="width: 45%">
                        <label>PSP Taking Time (seconds)</label>
                        <md-input v-model="newRoom.pspTakingTime" type="number"></md-input>
                    </md-field>

                    <md-field style="width: 45%">
                        <label>Zone Timer (seconds) (only if TKOTH)</label>
                        <md-input v-model="newRoom.goalTakenTime" type="number"></md-input>
                    </md-field>
                </div>

                <md-checkbox v-model="newRoom.bEnableWordCensorship" disabled>Enable Word Censorship</md-checkbox>
                <md-checkbox v-model="newRoom.bSaveChatLog">Save Chat Logs</md-checkbox>
                <md-checkbox v-model="newRoom.bAllowSpectator">Allow Spectators</md-checkbox>
            </md-step>

            <md-step id="maps" md-label="Map Settings">
                <md-table>
                    <md-table-row>
                        <md-table-head>Map</md-table-head>
                        <md-table-head>Include</md-table-head>
                        <md-table-head>Starting Map</md-table-head>
                    </md-table-row>
                    <md-table-row v-for="map in mapsForGameMode" :key="map">
                        <md-table-cell>{{map}}</md-table-cell>
                        <md-table-cell>
                            <md-checkbox v-model="newRoom.map.include"
                                         @change="includedRoomsChanged()"
                                         :value="map"></md-checkbox>
                        </md-table-cell>
                        <md-table-cell>
                            <md-radio v-if="canDrawRadioButton(map)" v-model="newRoom.map.start"
                                      :value="map"></md-radio>
                        </md-table-cell>
                    </md-table-row>
                </md-table>
            </md-step>

            <md-step id="weapons" md-label="Weapon Settings">
                <md-table>
                    <md-table-row>
                        <md-table-head>Weapon</md-table-head>
                        <md-table-head>Allow</md-table-head>
                    </md-table-row>
                    <md-table-row v-for="rule in weaponRules" :key="rule.id">
                        <md-table-cell>{{rule.name}}</md-table-cell>
                        <md-table-cell>
                            <md-checkbox v-model="newRoom.weapons.allow"
                                         :value="rule.id"></md-checkbox>
                        </md-table-cell>
                    </md-table-row>
                </md-table>
            </md-step>
            <md-step id="create" md-label="Overview">
                <div>
                    <p>Lobby Name: <b>{{newRoom.roomName}}</b></p>
                    <p>Game Mode: <b>{{newRoom.gameMode}}</b></p>
                    <p>Max Amount of Players: <b>{{newRoom.maxPlayer}}</b></p>
                    <p>Amount of Bots: <b>{{newRoom.numOfBots}}</b></p>
                    <p>Amount of Maps: <b>{{newRoom.map.include.length}}</b></p>
                    <p>Start Map: <b>{{newRoom.map.start}}</b></p>
                    <md-button class="md-raised md-primary" @click="createRoom()">
                        Create Room
                        <md-tooltip md-direction="right">It might take about 20 seconds for the room to be created and displayed in the server list - use the refresh button!</md-tooltip>
                    </md-button>
                </div>
            </md-step>
        </md-steppers>

        <md-dialog-actions>
            <md-button class="md-primary" @click="previousStep()" v-if="currentStep !== 'general'">PREVIOUS</md-button>
            <md-button class="md-primary" @click="nextStep()" v-if="currentStep !== 'create'">NEXT</md-button>
        </md-dialog-actions>
    </div>
</template>

<script>
    import json from './WeaponRules.json';

    export default {
        name: "CreateMapView",
        created() {
            fetch(this.serverIp + '/api/mapsandmodes', {
                headers: {"token": localStorage.token}
            })
                .then(data => data.json())
                .then(data => this.mapsAndModes = data);

            this.newRoom.weapons.allow = json.map(rule => rule.id);
        },
        data() {
            return {
                currentStep: 'general',
                mapsAndModes: {},
                weaponRules: json,
                newRoom: {
                    roomName: 'My Server',
                    gameMode: 'Team Deathmatch',
                    numOfBots: 10,
                    gameLength: 1200,
                    maxScore: 250,
                    maxPlayer: 24,
                    warmupTime: 30,
                    injuryTime: 120,
                    timeBetweenMatches: 30,
                    spawnProtectionTime: 5,
                    region: 'local',
                    password: '',
                    pspTakingTime: 10,
                    goalTakenTime: 480,
                    bEnableWordCensorship: false,
                    bSaveChatLog: false,
                    bAllowSpectator: false,
                    roomMessage: '',
                    map: {
                        include: [],
                        start: null
                    },
                    weapons: {
                        allow: []
                    }
                }
            }
        },
        computed: {
            mapsForGameMode: function () {
                if (this.mapsAndModes && this.mapsAndModes.modesDetail) {
                    const modeCode = this.mapsAndModes.modesDetail
                        .filter(modeDetail => modeDetail.name === this.newRoom.gameMode.replace(/ /g, "_"))
                        .map(modeDetail => modeDetail.code)[0];

                    return this.mapsAndModes.mapsAndModes.filter(mapMode => mapMode.modes.includes(modeCode)).map(mapMode => mapMode.map);
                }

                return [];
            },
            allowedWeapons: function () {
                return this.weaponRules.map(rule => ({
                    id: rule.id,
                    allow: this.newRoom.weapons.allow.includes(rule.id)
                }));
            }
        },
        watch: {
            mapsForGameMode(newValue) {
              this.newRoom.map.include = [...newValue];
              this.includedRoomsChanged();
            }
        },
        methods: {
            createRoom() {
                const matchType = ['Deathmatch', 'Team Deathmatch', 'Team King of the Hill', 'Team Conquer']
                    .indexOf(this.newRoom.gameMode);
                const weaponRules = [...this.allowedWeapons];
                const mapRotation = [...this.newRoom.map.include];
                const map = this.newRoom.map.start;
                const roomMessage = {
                    frequency: 5,
                    bInOrder: true,
                    bInChat: true,
                    messages: ['Hello World']
                };

                const body = JSON.stringify(Object.assign({}, this.newRoom, {
                    matchType,
                    weaponRules,
                    mapRotation,
                    map,
                    roomMessage
                }));

                const requestOptions = {
                    method: "POST",
                    headers: {"Content-Type": "application/json", "content-length": body.length, "token": localStorage.token},
                    body: body
                };
                fetch(this.serverIp + '/api/createroom', requestOptions)
                    .then(response => response.text())
                    .then(() => {
                        this.$emit('created');
                    });
            },
            previousStep() {
                const steps = ['general', 'maps', 'weapons', 'create'];
                this.currentStep = steps[steps.indexOf(this.currentStep) - 1];
            },
            nextStep() {
                const steps = ['general', 'maps', 'weapons', 'create'];
                this.currentStep = steps[steps.indexOf(this.currentStep) + 1];
            },
            canDrawRadioButton(map) {
                return this.newRoom.map.include.includes(map);
            },
            includedRoomsChanged() {
                if (!this.newRoom.map.include.includes(this.newRoom.map.start))
                    this.newRoom.map.start = null;

                if (!this.newRoom.map.start && this.newRoom.map.include.length > 0)
                    this.newRoom.map.start = this.newRoom.map.include[0];
            }
        }
    }
</script>

<style scoped>

</style>