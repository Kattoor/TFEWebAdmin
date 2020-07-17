<template>
    <v-card width="60%" class="mx-auto mt-5">
        <v-card-title class="pb-0">
            <div class="overline mb-4">Extra Data</div>
        </v-card-title>
        <v-card-text>
            <ExtraDataView :selected="selected"/>
        </v-card-text>
    </v-card>
</template>

<script>
    import ExtraDataView from "./ExtraDataView";
    export default {
        name: "RoomExtraDataCard",
        components: {ExtraDataView},
        props: ['selected']
    }
</script>
