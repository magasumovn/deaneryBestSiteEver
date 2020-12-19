import Vue from 'vue';
import VueRouter from 'vue-router';
import LoginPage from "pages/LoginPage.vue"
import Groups from "pages/Groups.vue";
import Performance from "pages/Performance.vue";
import Students from "pages/Students.vue";
import Subjects from "pages/Subjects.vue";
import Registration from "pages/Registration.vue";
import Profile from "pages/Profile.vue";
import Error404 from "pages/Error404.vue";

Vue.use(VueRouter)

const routes = [
    {path: '/', component: Groups},
    {path: '/login', component: LoginPage},
    {path: '/registration', component: Registration},
    {path: '/groups', component: Groups},
    {path: '/performances', component: Performance},
    {path: '/subjects', component: Subjects},
    {path: '/students', component: Students},
    {path: '/profile', component: Profile},
    {path: '*', component: Error404}
]

export default new VueRouter({
    mode: 'history',
    routes
})