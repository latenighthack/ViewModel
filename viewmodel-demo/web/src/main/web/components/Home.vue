<template>
  <div>
    <h1>{{ vm.text }}</h1>

    <ul>
      <template v-for="item in vm.items">
        <li v-if="tools.isHomeListItemA(item)">{{ item.titleA }}</li>
        <li v-if="tools.isHomeListItemB(item)">{{ item.titleB }}</li>
      </template>
    </ul>

    <div class="enabled" @click="vm.onStartTapped()">Start</div>
  </div>
</template>

<style scoped>
</style>

<script setup>
import { useRoute } from "vue-router";
import { inject, shallowReactive, onUnmounted, defineProps } from "vue";

const route = useRoute();
const core = inject("core");
const navigator = inject("navigator");
const query = route.query;

const props = defineProps({
});
const tools = com.latenighthack.viewmodel.vue;

const vm = window.vueModels.homeViewModel(Object.assign({}, props, query), core, navigator, shallowReactive, onUnmounted);
</script>

