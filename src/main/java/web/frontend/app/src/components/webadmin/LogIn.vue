<template>

    <v-card width="400" class="mx-auto mt-5" style="margin-top: 0 !important;" outlined :loading="loading">
        <v-card-title class="pb-0">
            <div class="overline mb-4">LOGIN</div>
        </v-card-title>
        <v-card-text>
            <v-text-field label="IP Address" prepend-icon="mdi-ip-network" v-model="login.ip"/>
            <v-text-field label="Username" prepend-icon="mdi-account-circle" v-model="login.username"/>
            <v-text-field
                    :type="showPassword ? 'text' : 'password'"
                    label="Password"
                    prepend-icon="mdi-lock"
                    :append-icon="showPassword ? 'mdi-eye' : 'mdi-eye-off'"
                    @click:append="showPassword = !showPassword"
                    v-model="login.password"/>

            <v-checkbox
                    v-model="login.discordBotIntegration"
                    label="Enable Discord bot integration"></v-checkbox>
        </v-card-text>

        <v-card-actions>
            <v-btn color="info" @click="logIn()">Login</v-btn>
        </v-card-actions>
    </v-card>
</template>

<script>
    export default {
        name: "LogIn",
        data() {
            return {
                showPassword: false,
                loading: false,
                login: {
                    ip: "",
                    username: "",
                    password: "",
                    discordBotIntegration: true
                }
            };
        },
        created() {
            if (localStorage.token && localStorage.username) {
                fetch(this.serverIp + '/api/verifytoken?token=' + localStorage.token)
                    .then(data => data.text())
                    .then(data => {
                        if (data === 'valid') {
                            console.log('Token is valid!');
                            this.$emit('loggedIn');
                        } else {
                            console.log('Token is invalid!');
                            localStorage.removeItem('token');
                            localStorage.removeItem('username');
                        }
                    });
            }
        },
        methods: {
            logIn() {
                fetch(this.serverIp + '/api/canlogin?ip=' + this.login.ip +
                    '&username=' + this.login.username +
                    '&password=' + this.login.password)
                    .then(data => data.text())
                    .then(data => {
                        this.loading = false;
                        if (data !== 'false') {
                            localStorage.token = data;
                            localStorage.username = this.login.username;

                            if (this.login.discordBotIntegration)
                                this.integrateDiscord();
                            else
                                this.$emit('loggedIn');
                        }
                    });

                this.loading = true;
            },
            alreadyIntegratedDiscord(cb) {
                fetch(this.serverIp + '/api/getadminlist', {
                    headers: {"token": localStorage.token}
                })
                    .then(data => data.json())
                    .then(data => {
                        cb(data.admins.filter(acc => acc.userName === 'DiscordBot').length > 0);
                    });
            },
            sendIntegrationRequest() {
                const newAccount = {
                    userName: 'DiscordBot',
                    name: 'DiscordBot',
                    password: '',
                    description: 'Account with basic permissions for Discord bot integration',
                    role: 'Basic'
                };

                const body = JSON.stringify(Object.assign({}, newAccount, {'role': ['Admin', 'Moderator', 'Basic'].indexOf(newAccount.role)}));

                const requestOptions = {
                    method: "POST",
                    headers: {
                        "Content-Type": "application/json",
                        "content-length": body.length,
                        "token": localStorage.token
                    },
                    body: body
                };

                fetch(this.serverIp + '/api/creatediscordaccount', requestOptions)
                    .then(response => response.text())
                    .then(password => {
                        console.log('Created account DiscordBot with password ' + password)
                        this.$emit('loggedIn');
                    });
            },
            integrateDiscord() {
                this.alreadyIntegratedDiscord(alreadyIntegrated => {
                    if (alreadyIntegrated) {
                        console.log('Already integrated!');
                        this.$emit('loggedIn');
                    } else {
                        this.sendIntegrationRequest();
                    }
                });

            }
        }
    };
</script>

<style lang="scss" scoped>

</style>
