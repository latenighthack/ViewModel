import { createApp } from "vue";
import * as _ from "./gen/app-web.js"; _;
import "./style.css";
import App from "./App.vue";
import router from "./router";

const com = module.exports["web"].com;

com.latenighthack.main("test.com", import.meta.env.VITE_API_SECURE != "false", router, (core, navigator, vueModels) => {
    createApp(App)
        .use({
            install: (app) => {
                const $tools = {};

                Object.assign($tools, module.exports["web"].com.latenighthack.viewmodel.vue);

                app.config.globalProperties.$tools = $tools;
            }
        })
        .provide("models", vueModels)
        .provide("core", core)
        .provide("navigator", navigator)
        .use(router)
        .mount("#app");
});
