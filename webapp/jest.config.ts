import path from "path"
import type { Config } from "@jest/types"

const jestConfig: Config.InitialOptions = {
	// preset: "ts-jest",
	roots: [
		"<rootDir>/src"
	],
	transform: {
		"^.+\\.t(s|sx)?$": "@swc/jest",
	},
	testEnvironment: "jsdom",
	clearMocks: true,
	coveragePathIgnorePatterns: [path.join(__dirname, "tests")],
	setupFilesAfterEnv: ["@testing-library/jest-dom/extend-expect", "./tests/init.js"],
	moduleDirectories: ["node_modules", path.join(__dirname, "src"), path.join(__dirname, "tests")],
	testPathIgnorePatterns: [
		"<rootDir>/node_modules/"
	],
	moduleFileExtensions: [
		"ts",
		"tsx",
		"js"
	]
}

export default jestConfig
