<template>
    <v-card class="mx-auto mt-5" style="width: 20%; margin: 50px auto auto;">
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

            <v-btn color="primary" @click="finishAccountCreation()">Create</v-btn>

            <v-btn text @click="$emit('cancel')">Cancel</v-btn>
        </v-card-text>
    </v-card>
</template>

<script>
    export default {
        name: "AccountCreationView",
        data() {
            return {
                showPassword: false,
                showConfirmPassword: false,

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
                }
            }
        },
        methods: {
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
                        this.$emit('created');
                    });
            }
        }
    }
</script>
