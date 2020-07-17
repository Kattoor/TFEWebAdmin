<template>
    <v-card width="60%" class="mx-auto mt-5" :loading="loading">
        <v-card-title class="pb-0">
            <div class="overline mb-4">ACCOUNT</div>

            <v-btn icon color="blue" @click="loadAccountsData()" style="margin-bottom: 16px;">
                <v-icon>mdi-refresh</v-icon>
            </v-btn>

            <v-btn icon color="red" @click="logout()"
                   style="margin-bottom: 16px;">
                <v-icon>mdi-logout</v-icon>
            </v-btn>

            <div style="flex: 1"></div>

            <v-btn v-if="accountSectionCollapsed" icon style="margin-bottom: 16px;"
                   @click="accountSectionCollapsed = false">
                <v-icon>mdi-chevron-down</v-icon>
            </v-btn>

            <v-btn v-if="!accountSectionCollapsed" icon style="margin-bottom: 16px;"
                   @click="accountSectionCollapsed = true">
                <v-icon>mdi-chevron-up</v-icon>
            </v-btn>
        </v-card-title>

        <v-card-text style="color: black;">
            <AccountSettingsView v-if="!accountSectionCollapsed"
                                 @showAccountCreationView="$emit('showAccountCreationView')"
                                 :admin-list="accountList"
                                 @deletedAccount="loadAccountsData()"
            ></AccountSettingsView>
        </v-card-text>
    </v-card>
</template>

<script>
    import AccountSettingsView from "./AccountSettingsView";
    export default {
        name: "AccountCard",
        components: {AccountSettingsView},
        data() {
            return {
                loading: true,
                accountList: [],
                accountSectionCollapsed: true
            };
        },
        created() {
            this.loadAccountsData();
        },
        methods: {
            logout() {
                localStorage.removeItem('token');
                localStorage.removeItem('username');
                this.$emit('loggedOut');
            },
            loadAccountsData() {
                this.loading = true;
                fetch(this.serverIp + '/api/getadminlist', {
                    headers: {"token": localStorage.token}
                })
                    .then(data => data.json())
                    .then(data => {
                        this.accountList =
                            data.admins.map(account => Object.assign(account, {role: ['Admin', 'Moderator', 'Basic'][account.role]}));
                        this.loading = false;
                    });

            }
        }
    }
</script>
