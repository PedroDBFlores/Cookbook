import { defineConfig } from "vitest/config";
import solidPlugin from 'vite-plugin-solid';


export default defineConfig({
  plugins: [solidPlugin()],
  test: {
    deps: { registerNodeLoader: true, },
    environment: "jsdom",
    globals: true,
    setupFiles: ['node_modules/@testing-library/jest-dom/vitest'],
    transformMode: { web: [/\.[jt]sx?$/] },
  },
  resolve: {
    conditions: ["development", "browser"],
  },
});