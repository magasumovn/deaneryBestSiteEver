<template>
    <v-container>
        <v-tabs
                color="primary"
                v-model="tab"
        >
            <v-tab>Предметы</v-tab>
        </v-tabs>

        <v-tabs-items v-model="tab">
            <v-tab-item>
                <SubjectsTable/>
            </v-tab-item>
        </v-tabs-items>

    </v-container>
</template>

<script>
    import groupsApi from 'api/group';
    import studentsApi from 'api/student';
    import subjectsApi from 'api/subject';
    import performancesApi from "api/performance";
    import GroupsTable from "components/GroupsTable.vue";
    import StudentsTable from "components/StudentsTable.vue";
    import SubjectsTable from "components/SubjectsTable.vue";
    import PerformancesTable from "components/PerformancesTable.vue";

    export default {
        name: "TestPage",
        components: {
            StudentsTable,
            GroupsTable,
            SubjectsTable,
            PerformancesTable
        },
        data() {
            return {
                tab: null,
                groups: [],
                groupNames: [],
                students: [],
                studentNames: [],
                subjects: [],
                subjectNames: [],
                performances: []
            }
        },
        created() {
            groupsApi.get().then(result =>
                result.json().then(data => {
                    data.forEach(element => {
                        this.groups.push(element);
                        this.groupNames.push({text: element.groupName, value: element});
                    });
                })
            ).catch(reason => alert('ERROR'));
            studentsApi.get().then(result =>
                result.json().then(data => {
                    data.forEach(element => {
                        this.students.push(element);
                        this.studentNames.push({text: element.studentName, value: element});
                    });
                })
            ).catch(reason => alert('ERROR'));
            subjectsApi.get().then(result =>
                result.json().then(data => {
                    data.forEach(element => {
                        this.subjects.push(element);
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