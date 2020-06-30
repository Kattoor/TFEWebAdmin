<template>
    <div style="width: 100%;" class="fill-height">
        <p>test</p>
    </div>
</template>

<script>
    export default {
        name: "ServerListView",
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
