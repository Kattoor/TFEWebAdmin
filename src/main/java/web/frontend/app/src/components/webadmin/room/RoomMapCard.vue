<template>
    <v-card width="60%" class="mx-auto mt-5">
        <v-card-title class="pb-0">
            <div class="overline mb-4">Map</div>
        </v-card-title>
        <v-card-text>
            <MapView :room="roomId" :dsc="dsc" :current="map" :rotation="mapRotation"/>
        </v-card-text>
    </v-card>
</template>

<script>
    import MapView from "./MapView";

    export default {
        name: "RoomMapCard",
        components: {MapView},
        props: ['roomId', 'dsc', 'map', 'mapRotation']
    }
</script>
