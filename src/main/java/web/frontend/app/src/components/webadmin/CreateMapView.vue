<template>
    <div style="width: 60%; margin: 50px auto auto;">

        <v-stepper v-model="e1">
            <v-stepper-header>
                <v-stepper-step editable step="1">
                    General Settings
                </v-stepper-step>

                <v-divider></v-divider>

                <v-stepper-step editable step="2">
                    Map Settings
                </v-stepper-step>

                <v-divider></v-divider>

                <v-stepper-step editable step="3">
                    Weapon Settings
                </v-stepper-step>

                <v-divider></v-divider>

                <v-stepper-step editable step="4">
                    Overview
                </v-stepper-step>
            </v-stepper-header>

            <v-stepper-items>
                <v-stepper-content step="1">

                    <div style="display: flex; width: 100%; justify-content: space-around; margin-top: 30px;">
                        <v-text-field v-model="newRoom.roomName" label="Lobby Name"></v-text-field>
                        <v-spacer/>
                        <v-combobox v-model="newRoom.gameMode" :items="gameModes" label="Game Mode"
                                    outlined></v-combobox>
                    </div>

                    <div style="display: flex; width: 100%; justify-content: space-between">
                        <v-text-field v-model="newRoom.numOfBots" label="Number of Bots"></v-text-field>
                        <v-spacer/>
                        <v-text-field v-model="newRoom.gameLength" label="Game Length (seconds)"></v-text-field>
                    </div>

                    <div style="display: flex; width: 100%; justify-content: space-between">
                        <v-text-field v-model="newRoom.maxScore" label="Kill Count"></v-text-field>
                        <v-spacer/>
                        <v-text-field v-model="newRoom.maxPlayer" label="Max Players"></v-text-field>
                    </div>

                    <div style="display: flex; width: 100%; justify-content: space-between">
                        <v-text-field v-model="newRoom.warmupTime" label="Warmup Time (seconds)"></v-text-field>
                        <v-spacer/>
                        <v-text-field v-model="newRoom.injuryTime"
                                      label="Wounded Timer (seconds, always 120)"></v-text-field>
                    </div>

                    <div style="display: flex; width: 100%; justify-content: space-between">
                        <v-text-field v-model="newRoom.timeBetweenMatches"
                                      label="Round End Scoreboard Timer (seconds)"></v-text-field>
                        <v-spacer/>
                        <v-text-field v-model="newRoom.spawnProtectionTime"
                                      label="Spawn Armor (seconds)"></v-text-field>
                    </div>

                    <div style="display: flex; width: 100%; justify-content: space-between">
                        <v-text-field v-model="newRoom.region" label="Region (always local)"></v-text-field>
                        <v-spacer/>
                        <v-text-field v-model="newRoom.password" label="Password"></v-text-field>
                    </div>

                    <div style="display: flex; width: 100%; justify-content: space-between">
                        <v-text-field v-model="newRoom.pspTakingTime" label="PSP Taking Time (seconds)"></v-text-field>
                        <v-spacer/>
                        <v-text-field v-model="newRoom.goalTakenTime"
                                      label="Zone Timer (seconds, only if TKOTH)"></v-text-field>
                    </div>

                    <v-checkbox v-model="newRoom.bEnableWordCensorship" label="Enable Word Censorship"
                                disabled></v-checkbox>
                    <v-checkbox v-model="newRoom.bSaveChatLog" label="Save Chat Logs"></v-checkbox>
                    <v-checkbox v-model="newRoom.bAllowSpectator" label="Allow Spectators"></v-checkbox>

                    <v-btn color="primary" @click="e1 = 2">
                        Continue
                    </v-btn>

                    <v-btn text @click="cancel()">Cancel</v-btn>
                </v-stepper-content>

                <v-stepper-content step="2">

                    <v-data-table
                            single-select
                            :headers="headers"
                            :items="mapsForGameMode"
                            hide-default-footer
                            disable-pagination
                            disable-filtering
                            disable-sort>
                        <template v-slot:body="{ items }">
                            <tbody>
                            <tr v-for="item in items" :key="item.name">
                                <td>{{ item }}</td>
                                <td>
                                    <v-checkbox v-model="newRoom.map.include" :value="item"
                                                @change="includedRoomsChanged()"></v-checkbox>
                                </td>
                                <td>
                                    <v-checkbox v-if="canDrawRadioButton(item)" v-model="newRoom.map.start"
                                                :value="item"></v-checkbox>
                                </td>
                            </tr>
                            </tbody>
                        </template>
                    </v-data-table>

                    <v-btn color="primary" @click="e1 = 3">
                        Continue
                    </v-btn>

                    <v-btn text @click="cancel()">Cancel</v-btn>
                </v-stepper-content>

                <v-stepper-content step="3">

                    <v-data-table
                            single-select
                            :headers="step3TableHeaders"
                            :items="weaponRules"
                            hide-default-footer disable-pagination disable-filtering disable-sort>
                        <template v-slot:body="{ items }">
                            <tbody>
                            <tr v-for="rule in items" :key="rule.id">
                                <td>{{ rule.name }}</td>
                                <td>
                                    <v-checkbox v-model="newRoom.weapons.allow"
                                                :value="rule.id"></v-checkbox>
                                </td>
                            </tr>
                            </tbody>
                        </template>
                    </v-data-table>

                    <v-btn color="primary" @click="e1 = 4">
                        Continue
                    </v-btn>

                    <v-btn text @click="cancel()">Cancel</v-btn>
                </v-stepper-content>
                <v-stepper-content step="4">
                    <div>
                        <p>Lobby Name: <b>{{newRoom.roomName}}</b></p>
                        <p>Game Mode: <b>{{newRoom.gameMode}}</b></p>
                        <p>Max Amount of Players: <b>{{newRoom.maxPlayer}}</b></p>
                        <p>Amount of Bots: <b>{{newRoom.numOfBots}}</b></p>
                        <p>Amount of Maps: <b>{{newRoom.map.include.length}}</b></p>
                        <p>Start Map: <b>{{newRoom.map.start}}</b></p>
                        <v-btn color="primary" @click="createRoom()">Create Room</v-btn>
                    </div>

                    <v-btn text @click="cancel()">Cancel</v-btn>
                </v-stepper-content>
            </v-stepper-items>


        </v-stepper>

        <!--
                <md-dialog-actions>
                    <md-button class="md-primary" @click="previousStep()" v-if="currentStep !== 'general'">PREVIOUS</md-button>
                    <md-button class="md-primary" @click="nextStep()" v-if="currentStep !== 'create'">NEXT</md-button>
                </md-dialog-actions>-->
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
                headers: ['Map', 'Include', 'Starting Map'],
                step3TableHeaders: ['Weapon', 'Allow'],
                e1: 1,
                currentStep: 'general',
                mapsAndModes: {},
                weaponRules: json,
                gameModes: [
                    'Deathmatch',
                    'Team Deathmatch',
                    'Team King of the Hill',
                    'Team Conquer'
                ],
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
            cancel() {
              this.$emit('cancel');
            },
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
                    headers: {
                        "Content-Type": "application/json",
                        "content-length": body.length,
                        "token": localStorage.token
                    },
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
