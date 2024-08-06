import { createApp } from "vue";
import * as _ from "./gen/app-web.js"; _;
import "./style.css";
import App from "./App.vue";
import router from "./router";

window.com = module.exports["web"].com;

com.latenighthack.main("test.com", import.meta.env.VITE_API_SECURE != "false", router, (core, navigator) => {
    createApp(App)
        .provide("core", core)
        .provide("navigator", navigator)
        .use(router)
        .mount("#app");
});
