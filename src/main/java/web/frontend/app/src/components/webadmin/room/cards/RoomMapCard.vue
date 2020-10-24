<template>
<div>
        <v-card-text>
            <MapView :room="roomId" :dsc="dsc" :current="map" :rotation="mapRotation"/>
        </v-card-text>
</div>
</template>

<script>
    import MapView from "../MapView";

    export default {
        name: "RoomMapCard",
        components: {MapView},
        props: ['roomId', 'dsc', 'map', 'mapRotation']
    }
</script>
