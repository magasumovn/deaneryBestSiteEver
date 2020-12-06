<template>
    <v-card
            class="mx-auto mt-10"
            max-width="400"
    >
        <v-card-title>
            <span class="headline">Вход</span>
        </v-card-title>
        <form action="/login" method="post">
            <v-card-text>
                <v-container>
                    <v-row>
                        <v-col cols="12">
                            <v-text-field
                                    name="username"
                                    label="Логин"
                                    required
                            />
                        </v-col>
                        <v-col cols="12">
                            <v-text-field
                                    name="password"
                                    label="Пароль"
                                    type="password"
                                    required
                            />
                        </v-col>
                    </v-row>
                    <div class="text-center ml-5">
                        <v-btn color="primary" dark type="submit">Войти</v-btn>
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
                    <div class="text-center ml-5 mt-5">
                        <a href="/registration">Зарегистрироваться</a>
                    </div>
                </v-container>
            </v-card-text>
        </form>
    </v-card>
</template>

<script>
    export default {
        name: "LoginPage",
        data() {
            return {
                alert: false,
                alertColor: "red",
                alertMessage: "Неверные логин / пароль"
            }
        },
        beforeMount() {
            if (this.$route.query.error === null) {
                this.alert = true;
            }
        }
    }
</script>

<style scoped>

</style>