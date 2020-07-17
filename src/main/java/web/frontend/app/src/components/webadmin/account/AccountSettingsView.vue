<template>
    <div>
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
            <v-btn class="ma-2" tile text color="blue" @click="$emit('showAccountCreationView')">
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
    </div>
</template>

<script>
    export default {
        name: "AccountSettingsView",
        props: ['loading', 'adminList'],
        data() {
            return {
                selectedRow: null,
                showAccountCreationView: false,
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
            setSelectedRow(key) {
                this.selectedRow = this.selectedRow === key ? null : key;
            },
            deleteAccount(account) {
                const body = JSON.stringify(Object.assign({}, account, {'role': ['Admin', 'Moderator', 'Basic'].indexOf(account.role)}));

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
                        this.$emit('deletedAccount');
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
