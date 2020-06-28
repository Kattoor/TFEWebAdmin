<template>
    <div class="centered-container">
        <md-content class="md-elevation-3">

            <div class="title">
                <img src="../../assets/img2.png">
                <div class="md-title">Task Force Elite - Web Admin</div>
                <div class="md-body-1">Version 0.1.0</div>
            </div>

            <div>
                <md-field>
                    <label>IP Address</label>
                    <md-input v-model="login.ip" autofocus></md-input>
                </md-field>

                <md-field>
                    <label>Username</label>
                    <md-input v-model="login.username"></md-input>
                </md-field>

                <md-field md-has-password>
                    <label>Password</label>
                    <md-input v-model="login.password" type="password"></md-input>
                </md-field>

                <div>
                    <md-checkbox style="margin-bottom: 0;" v-model="login.discordBotIntegration" disabled>
                        Enable Discord bot integration
                    </md-checkbox>
                    <md-tooltip md-direction="bottom">Not implemented yet, sorry!</md-tooltip>
                </div>
            </div>

            <div class="md-layout md-alignment-center-space-between" style="margin-top: 50px; margin-bottom: 20px;">
                <div/>
                <md-button class="md-raised md-primary" @click="auth">Log in</md-button>
                <div/>
            </div>

            <div class="loading-overlay" v-if="loading">
                <md-progress-spinner md-mode="indeterminate" :md-stroke="2"></md-progress-spinner>
            </div>
        </md-content>
    </div>
</template>

<script>
    export default {
        name: "LogIn",
        data() {
            return {
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
            auth() {
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
    .centered-container {
        display: flex;
        align-items: center;
        justify-content: center;
        position: relative;
        height: 100vh;
    }

    .title {
        text-align: center;
        margin-bottom: 30px;

        img {
            margin-bottom: 16px;
            max-width: 120px;
            padding: 10px;
        }
    }

    .actions {
        .md-button {
            margin: 0;
        }
    }

    .form {
        margin-bottom: 60px;
    }

    .md-content {
        z-index: 1;
        padding: 40px;
        width: 100%;
        max-width: 400px;
        position: relative;
    }

    .loading-overlay {
        z-index: 10;
        top: 0;
        left: 0;
        right: 0;
        position: absolute;
        width: 100%;
        height: 100%;
        background: rgba(255, 255, 255, 0.9);
        display: flex;
        align-items: center;
        justify-content: center;
    }
</style>
