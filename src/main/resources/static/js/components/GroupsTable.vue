<template>
    <v-container>
        <v-dialog v-model="dialog" max-width="500px">
            <v-card>
                <v-card-title>
                    <span class="headline">Группы{{selectedGroup}}</span>
                </v-card-title>

                <v-card-text>
                    <v-container class="mx-auto">
                        <v-row>
                            <v-col cols="4">
                                <v-label>ФИО</v-label>
                            </v-col>
                            <v-col cols="4">
                                <v-label>Номер телефона</v-label>
                            </v-col>
                            <v-col cols="4">
                                <v-label>Паспорт</v-label>
                            </v-col>
                        </v-row>
                        <v-divider/>
                        <v-row class="d-flex align-center" v-for="student in groupStudents">
                            <v-col cols="4">
                                <v-label>{{student.studentName}}</v-label>
                            </v-col>
                            <v-col cols="4">
                                <v-label>{{student.phoneNumber}}</v-label>
                            </v-col>
                            <v-col cols="4">
                                <v-label>{{student.passport}}</v-label>
                            </v-col>
                        </v-row>
                    </v-container>
                </v-card-text>

                <v-card-actions>
                    <v-spacer></v-spacer>
                    <v-btn color="primary darken-1" text @click="closeDialog">Отмена</v-btn>
                </v-card-actions>
            </v-card>
        </v-dialog>

        <v-data-table
                :headers="admin ? adminGroupsHeaders : groupsHeaders"
                :items="groups"
                @click:row="openDialog"
                sort-by="id"
                class="elevation-1"
        >
            <template v-if="admin" v-slot:top>
                <v-toolbar flat color="white">
                    <v-spacer/>
                    <v-dialog v-model="groupsDialog" max-width="500px">
                        <template v-slot:activator="{ on }">
                            <v-btn color="primary" dark class="mb-2" v-on="on">Добавить группу</v-btn>
                        </template>
                        <v-card>
                            <v-card-title>
                                <span class="headline">{{ groupsFormTitle }}</span>
                            </v-card-title>

                            <v-form ref="form">
                                <v-card-text>
                                    <v-container>
                                        <v-row>
                                            <v-col cols="12" sm="6" md="4">
                                                <v-text-field :rules="[rules.required]" v-model="editedGroup.groupName"
                                                              label="Название группы"/>
                                            </v-col>
                                        </v-row>
                                    </v-container>
                                </v-card-text>

                                <v-card-actions>
                                    <v-spacer/>
                                    <v-btn color="primary darken-1" text @click="groupsClose">Отмена</v-btn>
                                    <v-btn color="primary darken-1" text @click="groupsSave">Сохранить</v-btn>
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
                        @click="editGroup(item)"
                >
                    edit
                </v-icon>
                <v-icon
                        small
                        @click="deleteGroup(item)"
                >
                    delete
                </v-icon>
            </template>
        </v-data-table>
    </v-container>
</template>

<script>
    import groupsApi from "../api/group";
    import studentApi from "../api/student";
    import {mapState} from 'vuex';

    export default {
        name: "GroupsTable",
        props: ['groups'],
        data() {
            return {
                groupsDialog: false,
                adminGroupsHeaders: [
                    {
                        text: 'Номер группы',
                        value: 'groupNumber'
                    },
                    {
                        text: 'Название группы',
                        value: 'groupName'
                    },
                    {
                        text: 'Действия',
                        value: 'action'
                    }
                ],
                groupsHeaders: [
                    {
                        text: 'Номер группы',
                        value: 'groupNumber'
                    },
                    {
                        text: 'Название группы',
                        value: 'groupName'
                    }
                ],
                editedGroupIndex: -1,
                editedGroup: {
                    groupName: ''
                },
                defaultGroup: {
                    groupName: ''
                },
                selectedGroup: '',
                groupStudents: [],
                dialog: false,
                action: 0,
                rules: {
                    required: value => value.length > 0 || 'Заполните поле!'
                },
                admin: true
            }
        },
        created() {
            this.admin = this.profile.role === 'ADMIN';
        },
        computed: {
            ...mapState(['profile']),
            groupsFormTitle() {
                return this.editedGroupIndex === -1 ? 'Новая группа' : 'Редактировать';
            }
        },
        watch: {
            groupsDialog(val) {
                val || this.groupsClose()
            },
        },
        methods: {
            groupsClose() {
                this.groupsDialog = false;
                this.editedGroupIndex = -1;
                this.editedGroup = Object.assign({}, this.defaultGroup);
            },
            groupsSave() {
                if ((this.$refs.form.validate())) {
                    if (this.editedGroupIndex > -1) {
                        Object.assign(this.groups[this.editedGroupIndex], this.editedGroup);
                        groupsApi.update(this.editedGroup).catch(reason => alert('ERROR'));
                    } else {
                        groupsApi.save(this.editedGroup).then(result =>
                            result.json().then(data => this.groups.push(data))
                        ).catch(reason => alert('ERROR'));
                    }
                    this.groupsClose();
                }
            },
            editGroup(item) {
                this.action = 1;
                this.editedGroupIndex = this.groups.indexOf(item);
                this.editedGroup = Object.assign({}, item);
                this.groupsDialog = true;
            },
            deleteGroup(item) {
                this.action = 1;
                const index = this.groups.indexOf(item);
                let isDeleted = confirm('Удалить группу ?') && this.groups.splice(index, 1);
                if (isDeleted) {
                    groupsApi.remove(item.groupNumber).catch(reason => alert('ERROR'));
                }
            },
            openDialog(value) {
                if (!this.admin) {
                    return;
                }
                if (this.action !== 1) {
                    this.groupStudents = [];
                    this.selectedGroup = value.groupName;
                    studentApi.getByGroup(value.groupName).then(result => {
                        result.json().then(data => {
                            data.forEach(student => {
                                this.groupStudents.push(student);
                            })
                        })
                    }).catch(reason => alert('ERROR'));
                    this.dialog = true;
                } else {
                    this.action = 0;
                }
            },
            closeDialog() {
                this.dialog = false;
            }
        },
    }
</script>

<style scoped>

</style>