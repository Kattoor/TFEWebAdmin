<template>
<div>
        <v-card-text>
            <ExtraDataView :selected="selected"/>
        </v-card-text>
</div>
</template>

<script>
    import ExtraDataView from "../ExtraDataView";
    export default {
        name: "RoomExtraDataCard",
        components: {ExtraDataView},
        props: ['selected']
    }
</script>
