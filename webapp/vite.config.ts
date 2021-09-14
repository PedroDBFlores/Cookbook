import { defineConfig } from "vite"
import reactRefresh from "@vitejs/plugin-react-refresh"
import tsconfigPaths from "vite-tsconfig-paths"
import copy from "rollup-plugin-copy"

// https://vitejs.dev/config/
export default defineConfig({
	plugins: [
		reactRefresh(),
		tsconfigPaths(),
		copy({
			targets: [
				{
					src: "src/assets/locales",
					dest: "dist/"
				}
			],
			hook: "writeBundle"
		})
	],
	publicDir: "dist",
	server: {
		port: 8080,
		proxy: {
			"/api": "http://localhost:9000"
		},
		fsServe: {
			root: "dist",
		}
	},
	base: "./"
})
