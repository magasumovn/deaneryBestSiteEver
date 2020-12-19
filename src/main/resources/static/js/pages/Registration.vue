<template>
    <v-card
        class="mx-auto mt-10"
        max-width="400"
    >
        <v-card-title>
            <span class="headline">Регистрация</span>
        </v-card-title>
        <v-card-text>
            <v-container>
                <v-row>
                    <v-col cols="12">
                        <v-text-field
                            v-model="user.email"
                            name="email"
                            label="Email"
                            required
                        />
                    </v-col>
                    <v-col cols="12">
                        <v-text-field
                            v-model="user.name"
                            name="username"
                            label="Логин"
                            required
                        />
                    </v-col>
                    <v-col cols="12">
                        <v-text-field
                            v-model="user.password"
                            name="password"
                            label="Пароль"
                            type="password"
                            required
                        />
                    </v-col>
                </v-row>
                <div class="text-center ml-5">
                    <v-btn @click="register" color="primary" dark type="submit">Зарегистрироваться</v-btn>
                    <v-alert
                        class="mt-5"
                        :value="alert"
                        :color="alertColor"
                        dark
                        border="top"
                        transition="scale-transition"
                    >
                        {{alertMessage}}
                    </v-alert>
                </div>
                <div class="text-center mt-5">
                    <v-btn href="/login">Назад</v-btn>
                </div>
            </v-container>
        </v-card-text>
    </v-card>
</template>

<script>
import userApi from "../api/user";

export default {
    name: "Registration",
    data() {
        return {
            user: {
                name: "",
                password: "",
                email: ""
            },
            alert: false,
            alertColor: "green",
            alertMessage: "Регистрация прошла успешно"
        }
    },
    methods: {
        register() {
            userApi.save(this.user).then(result => {
                result.json().then(data => {
                    if (data) {
                        this.alertColor = "green";
                        this.alertMessage = "Регистрация прошла успешно";
                        this.alert = true;
                    } else {
                        this.alertColor = "red";
                        this.alertMessage = "Логин занят!";
                        this.alert = true;
                    }
                })
            }).catch(reason => alert('ERROR'));
        }
    }
}
</script>

<style scoped>

</style>