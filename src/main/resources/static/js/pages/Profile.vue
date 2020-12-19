<template>
    <v-container>
        <v-card
            class="mx-auto mt-10"
            max-width="600"
        >
            <v-card-title>
                <span class="headline">Данные пользователя</span>
            </v-card-title>
            <form>
                <v-card-text>
                    <v-container class="mx-auto">
                        <v-row justify="space-around">
                            <v-avatar size="150">
                                <img
                                    :src="getFilePath(photoUrl)"
                                    alt="John"
                                >
                            </v-avatar>
                        </v-row>
                        <v-row justify="space-around">
                            <v-btn @click="deletePhoto">Удалить</v-btn>
                            <v-btn @click="editPhoto">Добавить</v-btn>
                        </v-row>

                        <v-row class="d-flex align-center">
                            <v-col cols="3">
                                <v-label>Имя</v-label>
                            </v-col>
                            <v-col cols="7">
                                <span>{{name}}</span>
                            </v-col>
                        </v-row>
                    </v-container>
                </v-card-text>
            </form>
        </v-card>
    </v-container>
</template>

<script>
import {mapState} from 'vuex';
import userApi from 'api/user'

const appUrl = "http://localhost:8080";

export default {
    name: "Profile",
    data() {
        return {
            name: '',
            photoUrl: ''
        }
    },
    computed: mapState(['profile']),
    methods: {
        async editPhoto() {
            let input = document.createElement('input');
            input.type = 'file';
            input.accept = "image/jpeg, image/png";

            input.onchange = e => {
                let file = e.target.files[0];

                if (file.type !== 'image/jpeg' && file.type !== 'image/png') {
                    return;
                } else {
                    let formData = new FormData();
                    formData.append('file', file);
                    this.uploadFile(formData);
                }
            }

            input.click();
        },
        uploadFile(formData) {
            userApi.uploadPhoto(formData).then(result =>
                this.photoUrl = result.bodyText
            );
        },
        getFilePath(filename) {
            return appUrl + "/files/" + filename;
        },
        deletePhoto() {
            userApi.deletePhoto().then(result =>
                this.photoUrl = result.bodyText
            );
        }
    },
    created() {
        this.name = this.profile.name;
        this.photoUrl = this.profile.photoUrl;
    }
}
</script>

<style scoped>
.dev_badge .v-badge__wrapper .v-badge__badge {
    border-radius: 100px !important;
    height: 40px !important;
}

.edit_photo_btn {
    z-index: 100;
}
</style>