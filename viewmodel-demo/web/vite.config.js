import { defineConfig } from "vite";
import vue from "@vitejs/plugin-vue";
import path from "path";

export default defineConfig({
  plugins: [vue()],
  resolve: {
    alias: {
      "@": path.resolve(__dirname, "src/main/web")
    }
  },
  root: "src/main/web",
  build: {
    outDir: path.resolve(__dirname, "dist"),
    rollupOptions: {
      // input: path.resolve(__dirname, 'src/main/web/main.js')
      input: path.resolve(__dirname, "src/main/web/index.html")
    }
  },
  server: {
    port: 3000,
    open: true
  }
});
