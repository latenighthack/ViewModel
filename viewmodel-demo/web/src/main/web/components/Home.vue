<template>
  <div>
    <h1>{{ vm.text }}</h1>

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

const vm = window.vueModels.homeViewModel(Object.assign({}, props, query), core, navigator, shallowReactive, onUnmounted);
</script>

