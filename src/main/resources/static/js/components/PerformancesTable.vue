<template>
    <v-data-table
            :headers="admin ? adminPerformancesHeaders : performancesHeaders"
            :items="performances"
            sort-by="id"
            class="elevation-1"
    >
        <template v-slot:top>
            <v-toolbar flat color="white">
                <v-row>
                    <v-col cols="2" class="mt-12">
                        <v-select
                                :items="marks"
                                v-model="selectedMark"
                                label="Оценка"
                                @change="markChange"
                        ></v-select>
                    </v-col>
                    <v-col cols="4" class="mt-12">
                        <v-text-field
                                v-model="studentName"
                                label="Поиск по фамилии"
                        ></v-text-field>
                    </v-col>
                    <v-col cols="3" class="mt-12">
                        <v-btn
                                color="primary darken-1"
                                text
                                @click="updateList"
                        >Поиск
                        </v-btn>
                    </v-col>
                </v-row>
                <v-spacer/>
                <v-dialog v-if="admin" v-model="performancesDialog" max-width="500px">
                    <template v-slot:activator="{ on }">
                        <v-btn color="primary" dark class="mb-2" v-on="on">Добавить оценку</v-btn>
                    </template>
                    <v-card>
                        <v-card-title>
                            <span class="headline">{{ performancesFormTitle }}</span>
                        </v-card-title>

                        <v-form ref="form">
                            <v-card-text>
                                <v-container>
                                    <v-row>
                                        <v-col cols="12" sm="6" md="4">
                                            <v-select
                                                    :rules="[rules.requiredSelect]"
                                                    v-model="editedPerformance.student"
                                                    :items="studentNames"
                                                    label="Студент"
                                                    :disabled="editedPerformanceIndex > -1"
                                            />
                                        </v-col>
                                        <v-col cols="12" sm="6" md="4">
                                            <v-select
                                                    :rules="[rules.requiredSelect]"
                                                    v-model="editedPerformance.subject"
                                                    :items="subjectNames"
                                                    label="Предмет"
                                                    :disabled="editedPerformanceIndex > -1"
                                            />
                                        </v-col>
                                        <v-col cols="12" sm="6" md="4">
                                            <v-text-field
                                                    :rules="[rules.required]"
                                                    v-model="editedPerformance.semesterNumber"
                                                    :disabled="editedPerformanceIndex > -1"
                                                    label="Номер семестра"/>
                                        </v-col>
                                        <v-col cols="12" sm="6" md="4">
                                            <v-text-field
                                                    :rules="[rules.required]"
                                                    v-model="editedPerformance.mark"
                                                    label="Оценка"/>
                                        </v-col>
                                    </v-row>
                                </v-container>
                            </v-card-text>

                            <v-card-actions>
                                <v-spacer/>
                                <v-btn color="primary darken-1" text @click="performanceClose">Отмена</v-btn>
                                <v-btn color="primary darken-1" text @click="performanceSave">Сохранить</v-btn>
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
                    @click="editPerformance(item)"
            >
                edit
            </v-icon>
            <v-icon
                    small
                    @click="deletePerformance(item)"
            >
                delete
            </v-icon>
        </template>
    </v-data-table>
</template>

<script>
    import performancesApi from "../api/performance";
    import studentsApi from "../api/student";
    import subjectsApi from "../api/subject";
    import {mapState} from "vuex";

    export default {
        name: "PerformancesTable",
        data() {
            return {
                performances: [],
                subjectNames: [],
                studentNames: [],
                performancesDialog: false,
                adminPerformancesHeaders: [
                    {
                        text: 'Имя студента',
                        value: 'student.studentName'
                    },
                    {
                        text: 'Название предмета',
                        value: 'subject.subjectName'
                    },
                    {
                        text: 'Номер семестра',
                        value: 'semesterNumber'
                    },
                    {
                        text: 'Оценка',
                        value: 'mark'
                    },
                    {
                        text: 'Действия',
                        value: 'action'
                    }
                ],
                performancesHeaders: [
                    {
                        text: 'Имя студента',
                        value: 'student.studentName'
                    },
                    {
                        text: 'Название предмета',
                        value: 'subject.subjectName'
                    },
                    {
                        text: 'Номер семестра',
                        value: 'semesterNumber'
                    },
                    {
                        text: 'Оценка',
                        value: 'mark'
                    }
                ],
                editedPerformanceIndex: -1,
                editedPerformance: {
                    student: {},
                    subject: {},
                    semesterNumber: '',
                    mark: ''
                },
                defaultPerformance: {
                    student: {},
                    subject: {},
                    semesterNumber: '',
                    mark: ''
                },
                marks: ['Все', '2', '3', '4', '5'],
                selectedMark: 'Все',
                studentName: '',
                rules: {
                    required: value => (!!(value.length > 0 || parseInt(value))) || 'Заполните поле!',
                    requiredSelect: value => ("studentName" in value || "subjectName" in value) || 'Заполните поле!'
                },
                admin: true
            };
        },
        computed: {
            ...mapState(['profile']),
            performancesFormTitle() {
                return this.editedPerformanceIndex === -1 ? 'Новая оценка' : 'Редактировать';
            }
        },
        watch: {
            performancesDialog(val) {
                val || this.performanceClose()
            },
        },
        methods: {
            performanceClose() {
                this.performancesDialog = false;
                this.editedPerformanceIndex = -1;
                this.editedPerformance = Object.assign({}, this.defaultPerformance);
            },
            performanceSave() {
                if ((this.$refs.form.validate())) {
                    if (this.editedPerformanceIndex > -1) {
                        Object.assign(this.performances[this.editedPerformanceIndex], this.editedPerformance);
                        performancesApi.update(this.editedPerformance).catch(reason => alert('ERROR'));
                    } else {
                        performancesApi.save(this.editedPerformance).then(result =>
                            result.json().then(data => this.performances.push(data))
                        ).catch(reason => alert('ERROR'));
                    }
                    this.performanceClose();
                }
            },
            editPerformance(item) {
                this.editedPerformanceIndex = this.performances.indexOf(item);
                this.editedPerformance = Object.assign({}, item);
                this.performancesDialog = true;
            },
            deletePerformance(item) {
                const index = this.performances.indexOf(item);
                let isDeleted = confirm('Удалить оценку?') && this.performances.splice(index, 1);
                if (isDeleted) {
                    performancesApi.remove(item).catch(reason => alert('ERROR'));
                }
            },
            markChange() {
                this.performances = [];
                performancesApi.getByMark(this.selectedMark).then(result => {
                    result.json().then(data => {
                        data.forEach(perf => {
                            if (this.studentName === '' || perf.student.studentName.includes(this.studentName)) {
                                this.performances.push(perf);
                            }
                        })
                    })
                }).catch(reason => alert('ERROR'));
            },
            updateList() {
                this.performances = [];
                performancesApi.getByStudentName(this.studentName).then(result =>
                    result.json().then(data => {
                        data.forEach(element => {
                            if (this.selectedMark === 'Все' || this.selectedMark === element.mark) {
                                this.performances.push(element);
                            }
                        });
                    })
                ).catch(reason => alert('ERROR'));
            }
        },
        created() {
            this.admin = this.profile.role === 'ADMIN';

            studentsApi.get().then(result =>
                result.json().then(data => {
                    data.forEach(element => {
                        this.studentNames.push({text: element.studentName, value: element});
                    });
                })
            ).catch(reason => alert('ERROR'));
            subjectsApi.get().then(result =>
                result.json().then(data => {
                    data.forEach(element => {
                        this.subjectNames.push({text: element.subjectName, value: element});
                    });
                })
            ).catch(reason => alert('ERROR'));
            performancesApi.get().then(result =>
                result.json().then(data => {
                    data.forEach(element => {
                        this.performances.push(element);
                    });
                })
            ).catch(reason => alert('ERROR'));
        }
    }
</script>

<style scoped>

</style>