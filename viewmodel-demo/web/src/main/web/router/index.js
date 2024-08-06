import { createRouter, createWebHistory } from "vue-router";
import HomeVue from "../components/Home.vue";

export const routes = [
    { name: "home", path: "/", components: { main: HomeVue }, props: true, params: {}, query: {} },
];

const router = createRouter({
    history: createWebHistory(),
    routes,
});

export default router;
