<template>
    <v-data-table
            :headers="admin ? adminStudentsHeaders : studentsHeaders"
            :items="students"
            sort-by="id"
            class="elevation-1"
    >
        <template v-if="admin" v-slot:top>
            <v-toolbar flat color="white">
                <v-spacer/>
                <v-dialog v-model="studentsDialog" max-width="500px">
                    <template v-slot:activator="{ on }">
                        <v-btn color="primary" dark class="mb-2" v-on="on">Добавить студента</v-btn>
                    </template>
                    <v-card>
                        <v-card-title>
                            <span class="headline">{{ studentsFormTitle }}</span>
                        </v-card-title>

                        <v-form ref="form">
                            <v-card-text>
                                <v-container>
                                    <v-row>
                                        <v-col cols="12" sm="6" md="4">
                                            <v-select
                                                    :rules="[rules.requiredSelect]"
                                                    v-model="editedStudent.group"
                                                    :items="groupNames"
                                                    label="Группа"
                                            />
                                        </v-col>
                                        <v-col cols="12" sm="6" md="4">
                                            <v-text-field
                                                    :rules="[rules.required]"
                                                    v-model="editedStudent.studentName"
                                                    label="ФИО"/>
                                        </v-col>
                                    </v-row>
                                    <v-row>
                                        <v-col cols="12" sm="6" md="4">
                                            <v-text-field
                                                    :rules="[rules.required]"
                                                    v-model="passport"
                                                    label="Паспорт"
                                                    :error-messages="passportErrors"
                                                    @input="$v.passport.$touch()"
                                                    @blur="$v.passport.$touch()"
                                            />
                                        </v-col>
                                    </v-row>
                                    <v-row>
                                        <v-col cols="12" sm="6" md="4">
                                            <v-text-field
                                                    :rules="[rules.required]"
                                                    v-model="phoneNumber"
                                                    label="Номер телефона"
                                                    :error-messages="phoneErrors"
                                                    @input="$v.phoneNumber.$touch()"
                                                    @blur="$v.phoneNumber.$touch()"
                                            />
                                        </v-col>
                                    </v-row>
                                </v-container>
                            </v-card-text>

                            <v-card-actions>
                                <v-spacer/>
                                <v-btn color="primary darken-1" text @click="studentClose">Отмена</v-btn>
                                <v-btn color="primary darken-1" text @click="studentSave">Сохранить</v-btn>
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
                    @click="editStudent(item)"
            >
                edit
            </v-icon>
            <v-icon
                    small
                    @click="deleteStudent(item)"
            >
                delete
            </v-icon>
        </template>
    </v-data-table>
</template>

<script>
    import {validationMixin} from 'vuelidate'
    import {numeric} from 'vuelidate/lib/validators'
    import studentsApi from "../api/student";
    import {mapState} from "vuex";

    export default {
        name: "StudentsTable",
        mixins: [validationMixin],
        validations: {
            passport: {numeric},
            phoneNumber: {numeric},
        },
        props: ['students', 'groupNames'],
        data() {
            return {
                studentsDialog: false,
                adminStudentsHeaders: [
                    {
                        text: 'ФИО',
                        value: 'studentName'
                    },
                    {
                        text: 'Паспорт',
                        value: 'passport'
                    },
                    {
                        text: 'Номер телефона',
                        value: 'phoneNumber'
                    },
                    {
                        text: 'Название группы',
                        value: 'group.groupName'
                    },
                    {
                        text: 'Действия',
                        value: 'action'
                    }
                ],
                studentsHeaders: [
                    {
                        text: 'ФИО',
                        value: 'studentName'
                    },
                    {
                        text: 'Паспорт',
                        value: 'passport'
                    },
                    {
                        text: 'Номер телефона',
                        value: 'phoneNumber'
                    },
                    {
                        text: 'Название группы',
                        value: 'group.groupName'
                    }
                ],
                editedStudentIndex: -1,
                editedStudent: {
                    studentName: '',
                    group: {},
                    passport: '',
                    phoneNumber: ''
                },
                defaultStudent: {
                    groupName: '',
                    group: {},
                    passport: '',
                    phoneNumber: ''
                },
                phoneNumber: '',
                passport: '',
                rules: {
                    required: value => value.length > 0 || 'Заполните поле!',
                    requiredSelect : value => "groupName" in value || 'Заполните поле!'
                },
                admin: true
            }
        },
        created() {
            this.admin = this.profile.role === 'ADMIN';
        },
        computed: {
            ...mapState(['profile']),
            studentsFormTitle() {
                return this.editedStudentIndex === -1 ? 'Новый студент' : 'Редактировать';
            },
            phoneErrors() {
                const errors = [];
                if (!this.$v.phoneNumber.$dirty) return errors
                !this.$v.phoneNumber.numeric && errors.push('Номер телефона должен содержать только цифры')
                return errors
            },
            passportErrors() {
                const errors = [];
                if (!this.$v.passport.$dirty) return errors
                !this.$v.passport.numeric && errors.push('Пасспорт должен содержать только цифры')
                return errors
            },
        },
        watch: {
            studentsDialog(val) {
                val || this.studentClose()
            },
        },
        methods: {
            studentClose() {
                this.studentsDialog = false;
                this.editedStudentIndex = -1;
                this.editedStudent = Object.assign({}, this.defaultStudent);
                this.phoneNumber = '';
                this.passport = '';
            },
            studentSave() {
                if ((this.$refs.form.validate())) {
                    this.$v.$touch()
                    if (this.$v.$invalid) {

                    } else {
                        this.editedStudent.phoneNumber = this.phoneNumber;
                        this.editedStudent.passport = this.passport;
                        if (this.editedStudentIndex > -1) {
                            Object.assign(this.students[this.editedStudentIndex], this.editedStudent);
                            studentsApi.update(this.editedStudent).catch(reason => alert('ERROR'));
                        } else {
                            studentsApi.save(this.editedStudent).then(result =>
                                result.json().then(data => this.students.push(data))
                            ).catch(reason => alert('ERROR'));
                        }
                        this.studentClose();
                    }
                }
            },
            editStudent(item) {
                this.editedStudentIndex = this.students.indexOf(item);
                this.editedStudent = Object.assign({}, item);
                this.phoneNumber = this.editedStudent.phoneNumber;
                this.passport = this.editedStudent.passport;
                this.studentsDialog = true;
            },
            deleteStudent(item) {
                const index = this.students.indexOf(item);
                let isDeleted = confirm('Удалить студента ?') && this.students.splice(index, 1);
                if (isDeleted) {
                    studentsApi.remove(item.studentID).catch(reason => alert('ERROR'));
                }
            },
        },
    }
</script>

<style scoped>

</style>