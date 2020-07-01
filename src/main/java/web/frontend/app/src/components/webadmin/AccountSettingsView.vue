<template>
    <div style="width: 60%; margin: 50px auto auto;">

        <v-card class="mx-auto mt-5" v-if="!showAccountCreationView" :loading="loading">
            <v-card-title class="pb-0">
                <div class="overline mb-4">ACCOUNTS</div>
                <v-btn icon color="blue" @click="loadData()" style="margin-bottom: 16px;">
                    <v-icon>mdi-refresh</v-icon>
                </v-btn>
            </v-card-title>
            <v-card-text>
                <v-data-table
                        :headers="headers"
                        :items="adminList"
                        hide-default-footer
                        disable-pagination
                        disable-filtering
                        disable-sort>
                    <template v-slot:body="{ items }">
                        <tbody>
                        <tr v-for="(item, index) in items"
                            :key="item.userName"
                            :class="index === selectedRow ? 'blue white-text' : ''"
                            @click="setSelectedRow(index)">
                            <td>{{ item.userName }}</td>
                            <td>{{ item.role }}</td>
                            <td>{{ item.name }}</td>
                            <td>{{ item.description }}</td>
                            <td>{{ item.bOnline }}</td>
                        </tr>
                        </tbody>
                    </template>
                </v-data-table>

                <div style="width: 100%; display: flex; flex-direction: row; justify-content: space-between">
                    <v-btn class="ma-2" tile text color="blue" @click="createAccount()">
                        CREATE
                        <v-icon style="margin-left: 2px;">mdi-plus</v-icon>
                    </v-btn>

                    <div v-if="someAccountIsSelected">
                        <v-btn class="ma-2" tile text color="red" @click="deleteAccount(selected)">
                            DELETE
                            <v-icon style="margin-left: 2px;">mdi-delete</v-icon>
                        </v-btn>
                    </div>
                </div>
            </v-card-text>
        </v-card>

        <v-card class="mx-auto mt-5" v-if="showAccountCreationView">
            <v-card-title class="pb-0">
                <div class="overline mb-4">ACCOUNT CREATION</div>
            </v-card-title>
            <v-card-text>
                <v-text-field v-model="newAccount.userName" label="Username"
                              :rules="[rules.required]"></v-text-field>
                <v-text-field v-model="newAccount.password" label="Password"
                              :type="showPassword ? 'text' : 'password'"
                              :append-icon="showPassword ? 'mdi-eye' : 'mdi-eye-off'"
                              @click:append="showPassword = !showPassword"
                              :rules="[rules.required, rules.passwordLength, rules.upperAndLowerCase, rules.containsNumber, rules.containsSpecialCharacter]"></v-text-field>
                <v-text-field label="Confirm Password"
                              :append-icon="showConfirmPassword ? 'mdi-eye' : 'mdi-eye-off'"
                              @click:append="showConfirmPassword = !showConfirmPassword"
                              :type="showConfirmPassword ? 'text' : 'password'"
                              :rules="[rules.required, rules.equalsPassword]"></v-text-field>
                <v-text-field v-model="newAccount.name" label="Name"></v-text-field>
                <v-text-field v-model="newAccount.description" label="Description"></v-text-field>
                <v-combobox
                        :rules="[rules.required]"
                        v-model="newAccount.role"
                        :items="['Admin', 'Moderator', 'Basic']"
                        label="Role"
                        dense outlined></v-combobox>

                <v-btn color="primary" @click="finishAccountCreation()">Create Room</v-btn>
            </v-card-text>
        </v-card>
    </div>
</template>

<script>
    export default {
        name: "AccountSettingsView",
        created() {
            this.loadData();
        },
        data() {
            return {
                loading: true,
                selectedRow: null,

                showPassword: false,
                showConfirmPassword: false,
                showAccountCreationView: false,
                newAccount: {
                    userName: '',
                    name: '',
                    password: '',
                    description: '',
                    role: 'Admin'
                },
                rules: {
                    required: value => !!value || 'Required',
                    passwordLength: value => value.length >= 8 || 'Minimum 8 characters',
                    upperAndLowerCase: value => (/[a-z]/.test(value) && /[A-Z]/.test(value)) || 'Uppercase and lowercase letters',
                    containsNumber: value => /[0-9]/.test(value) || 'At least one number',
                    containsSpecialCharacter: value => [" ", "!", "\"", "#", "$", "%", "&", "'", "(", ")", "*", ",", "-", ".", "/", ":", ";", "<", "=", ">", "?", "@", "[", "\\", "]", "^", "_", "`", "{", "|", "}", "~"].filter(c => value.includes(c)).length > 0 || 'At least one special character',
                    equalsPassword: value => value === this.newAccount.password || 'Does not match password'
                },
                adminList: [],
                headers: [
                    {text: 'Username', value: 'username'},
                    {text: 'Role', value: 'role'},
                    {text: 'Name', value: 'name'},
                    {text: 'Description', value: 'description'},
                    {text: 'Online', value: 'online'}
                ]
            }
        },
        computed: {
            someAccountIsSelected: function () {
                return this.selected && this.selected.userName;
            },
            selected: function () {
                return this.adminList[this.selectedRow];
            }
        },
        methods: {
            loadData() {
                this.loading = true;
                fetch(this.serverIp + '/api/getadminlist', {
                    headers: {"token": localStorage.token}
                })
                    .then(data => data.json())
                    .then(data => {
                        this.adminList =
                            data.admins.map(account => Object.assign(account, {role: ['Admin', 'Moderator', 'Basic'][account.role]}));
                        this.loading = false;
                    });
            },
            setSelectedRow(key) {
                this.selectedRow = this.selectedRow === key ? null : key;
            },
            createAccount() {
                this.showAccountCreationView = true;
            },
            deleteAccount(account) {
                const body = JSON.stringify(Object.assign({}, account, {'role': ['Admin', 'Moderator', 'Basic'].indexOf(this.newAccount.role)}));

                const requestOptions = {
                    method: "POST",
                    headers: {
                        "Content-Type": "application/json",
                        "content-length": body.length,
                        "token": localStorage.token
                    },
                    body: body
                };
                fetch(this.serverIp + '/api/deleteadmin', requestOptions)
                    .then(response => response.text())
                    .then(() => {
                        this.showAccountCreationView = false;
                        this.loadData();
                    });
            },
            finishAccountCreation() {
                const body = JSON.stringify(Object.assign({}, this.newAccount, {'role': ['Admin', 'Moderator', 'Basic'].indexOf(this.newAccount.role)}));

                const requestOptions = {
                    method: "POST",
                    headers: {
                        "Content-Type": "application/json",
                        "content-length": body.length,
                        "token": localStorage.token
                    },
                    body: body
                };
                fetch(this.serverIp + '/api/createadmin', requestOptions)
                    .then(response => response.text())
                    .then(() => {
                        this.showAccountCreationView = false;
                        this.loadData();
                    });
            }
        }
    }
</script>

<style lang="scss" scoped>
    .white-text {
        color: white;
        font-weight: bold;
    }

    tr :hover {
        cursor: pointer;
    }
</style>
