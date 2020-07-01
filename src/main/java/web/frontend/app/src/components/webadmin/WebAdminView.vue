<template>
    <div style="width: 100%;" class="fill-height">
        <v-container v-if="!loggedIn" class="fill-height" fluid>
            <v-row align="center" justify="center">
                <LogIn id="login" v-on:loggedIn="onLoggedIn"></LogIn>
            </v-row>
        </v-container>
        <RoomsView id="roomsView" v-if="loggedIn"></RoomsView>
    </div>
</template>

<script>
    import LogIn from "./LogIn";
    import RoomsView from "./RoomsView";

    export default {
        name: "WebAdminView",
        components: {LogIn, RoomsView},
        data() {
            return {
                loggedIn: false,
            };
        },
        methods: {
            onLoggedIn() {
                this.loggedIn = true;
            }
        },
        computed: {
            username() {
                return localStorage.username;
            }
        }
    };
</script>
