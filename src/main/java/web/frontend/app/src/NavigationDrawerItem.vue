<template>
    <v-list-item :key="item.text" link @click="$emit('clicked')"
                 :class="isSelectedRow ? 'bb' : ''">
        <v-list-item-action>
            <v-icon>{{ item.icon }}</v-icon>
        </v-list-item-action>
        <v-list-item-content>
            <v-list-item-title>{{item.text}}</v-list-item-title>
        </v-list-item-content>
    </v-list-item>
</template>

<script>

    export default {
        name: 'NavigationDrawerItem',
        props: ['item', 'isSelectedRow']
    }
</script>

<style lang="scss" scoped>

</style>
