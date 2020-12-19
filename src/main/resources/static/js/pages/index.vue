<template>
    <v-app>
        <v-app-bar app>
            <v-toolbar-title>Деканат</v-toolbar-title>

            <v-divider
                    class="mx-4"
                    vertical
            />

            <v-btn
                    v-if="!profile"
                    text
                    href="/login"
            >
                Войти
            </v-btn>

            <v-btn
                v-if="profile"
                text
                href="/logout"
            >
                Выйти
            </v-btn>
            <v-btn
                    v-if="profile"
                    :disabled="$route.path === '/groups' || $route.path === '/' || $route.path === '*'"
                    text
                    href="/groups"
            >
                Группы
            </v-btn>
            <v-btn
                    v-if="profile"
                    :disabled="$route.path === '/students'"
                    text
                    href="/students"
            >
                Студенты
            </v-btn>
            <v-btn
                    v-if="profile"
                    :disabled="$route.path === '/subjects'"
                    text
                    href="/subjects"
            >
                Предметы
            </v-btn>
            <v-btn
                    v-if="profile"
                    :disabled="$route.path === '/performances'"
                    text
                    href="/performances"
            >
                Оценки
            </v-btn>
            <v-btn
                v-if="profile"
                :disabled="$route.path === '/profile'"
                text
                href="/profile"
            >
                Профиль
            </v-btn>
        </v-app-bar>
        <v-content>
            <router-view></router-view>
        </v-content>
    </v-app>
</template>

<script>
    import {mapState} from 'vuex';

    export default {
        name: "index",
        computed: mapState(['profile'])
    }
</script>

<style scoped>

</style>