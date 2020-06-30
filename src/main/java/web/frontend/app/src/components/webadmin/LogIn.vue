<template>

    <v-card width="400" class="mx-auto mt-5" outlined :loading="loading">
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
                    label="Enable Discord bot integration"
                    disabled></v-checkbox>
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
                    discordBotIntegration: false
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
                            this.$emit('loggedIn');
                        }
                    });

                this.loading = true;
            }
        }
    };
</script>

<style lang="scss" scoped>

</style>
