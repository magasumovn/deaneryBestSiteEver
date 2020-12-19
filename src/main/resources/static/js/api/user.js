import Vue from 'vue'

const users = Vue.resource('/api/user/{id}');
const photos = Vue.resource('/api/user/photo');

export default {
    save: user => users.save({}, user),
    update: user => users.update({id: user.id}, user),
    deletePhoto: () => photos.delete(),
    uploadPhoto: (photo) => photos.save(photo)
}