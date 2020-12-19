<template>
    <v-data-table
            :headers="admin ? adminSubjectsHeaders : subjectsHeaders"
            :items="subjects"
            sort-by="id"
            class="elevation-1"
    >
        <template v-if="admin" v-slot:top>
            <v-toolbar flat color="white">
                <v-col cols="2" class="mt-5">
                    <v-select
                            :items="courses"
                            v-model="selectedCourse"
                            label="Курс"
                            @change="courseChange"
                    ></v-select>
                </v-col>
                <v-spacer/>
                <v-dialog v-model="subjectsDialog" max-width="500px">
                    <template v-slot:activator="{ on }">
                        <v-btn color="primary" dark class="mb-2" v-on="on">Добавить предмет</v-btn>
                    </template>
                    <v-card>
                        <v-card-title>
                            <span class="headline">{{ subjectsFormTitle }}</span>
                        </v-card-title>

                        <v-form ref="form">
                            <v-card-text>
                                <v-container>
                                    <v-row>
                                        <v-col cols="12" sm="6" md="4">
                                            <v-text-field
                                                    :rules="[rules.required]"
                                                    v-model="editedSubject.subjectName"
                                                    label="Название предмета"/>
                                        </v-col>
                                        <v-col cols="12" sm="6" md="4">
                                            <v-text-field
                                                    :rules="[rules.required]"
                                                    v-model="editedSubject.courseName"
                                                    label="Курс"/>
                                        </v-col>
                                    </v-row>
                                </v-container>
                            </v-card-text>

                            <v-card-actions>
                                <v-spacer/>
                                <v-btn color="primary darken-1" text @click="subjectsClose">Отмена</v-btn>
                                <v-btn color="primary darken-1" text @click="subjectsSave">Сохранить</v-btn>
                            </v-card-actions>
                        </v-form>
                    </v-card>
                </v-dialog>
            </v-toolbar>
        </template>
        <template v-if="admin" v-slot:item.action="{ item }">
            <v-icon
                    small
                    class="mr-2"
                    @click="editSubject(item)"
            >
                edit
            </v-icon>
            <v-icon
                    small
                    @click="deleteSubject(item)"
            >
                delete
            </v-icon>
        </template>
    </v-data-table>
</template>

<script>
    import subjectsApi from "../api/subject";
    import {mapState} from "vuex";

    export default {
        name: "SubjectsTable",
        data() {
            return {
                subjects: [],
                subjectsDialog: false,
                adminSubjectsHeaders: [
                    {
                        text: 'Название предмета',
                        value: 'subjectName'
                    },
                    {
                        text: 'Курс',
                        value: 'courseName'
                    },
                    {
                        text: 'Действия',
                        value: 'action'
                    }
                ],
                subjectsHeaders: [
                    {
                        text: 'Название предмета',
                        value: 'subjectName'
                    },
                    {
                        text: 'Курс',
                        value: 'courseName'
                    }
                ],
                editedSubjectIndex: -1,
                editedSubject: {
                    subjectName: '',
                    courseName: ''
                },
                defaultSubject: {
                    subjectName: '',
                    courseName: ''
                },
                courses: ['Все', '1', '2', '3', '4'],
                selectedCourse: 'Все',
                rules: {
                    required: value => value.length > 0 || 'Заполните поле!',
                },
                admin: true
            }
        },
        computed: {
            ...mapState(['profile']),
            subjectsFormTitle() {
                return this.editedSubjectIndex === -1 ? 'Новый предмет' : 'Редактировать';
            }
        },
        watch: {
            subjectsDialog(val) {
                val || this.subjectsClose()
            },
        },
        methods: {
            subjectsClose() {
                this.subjectsDialog = false;
                this.editedSubjectIndex = -1;
                this.editedSubject = Object.assign({}, this.defaultSubject);
            },
            subjectsSave() {
                if ((this.$refs.form.validate())) {
                    if (this.editedSubjectIndex > -1) {
                        Object.assign(this.subjects[this.editedSubjectIndex], this.editedSubject);
                        subjectsApi.update(this.editedSubject).catch(reason => alert('ERROR'));
                    } else {
                        subjectsApi.save(this.editedSubject).then(result =>
                            result.json().then(data => this.subjects.push(data))
                        ).catch(reason => alert('ERROR'));
                    }
                    this.subjectsClose();
                }
            },
            editSubject(item) {
                this.editedSubjectIndex = this.subjects.indexOf(item);
                this.editedSubject = Object.assign({}, item);
                this.subjectsDialog = true;
            },
            deleteSubject(item) {
                const index = this.subjects.indexOf(item);
                let isDeleted = confirm('Удалить предмет ?') && this.subjects.splice(index, 1);
                if (isDeleted) {
                    subjectsApi.remove(item.subjectID).catch(reason => alert('ERROR'));
                }
            },
            courseChange() {
                this.subjects = [];
                subjectsApi.getByCourse(this.selectedCourse).then(result => {
                    result.json().then(data => {
                        data.forEach(subject => {
                            this.subjects.push(subject);
                        })
                    })
                }).catch(reason => alert('ERROR'));
            }
        },
        created() {
            console.log(this.profile);
            this.admin = this.profile.role === 'ADMIN';

            subjectsApi.get().then(result =>
                result.json().then(data => {
                    data.forEach(element => {
                        this.subjects.push(element);
                    });
                })
            ).catch(reason => alert('ERROR'));
        }
    }
</script>

<style scoped>

</style>