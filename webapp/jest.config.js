const path = require("path")

module.exports = {
    preset: "ts-jest",
    roots: [
        "<rootDir>/tests"
    ],
    transform: {
        "\\.test.ts?$": "ts-jest",
        "\\.test.tsx?$": "ts-jest"
    },
    clearMocks: true,
    setupFilesAfterEnv: [
        "@testing-library/jest-dom/extend-expect",
        "jest-extended",
        "jest-chain"
    ],
    moduleDirectories: ["node_modules", path.join(__dirname, "src"), path.join(__dirname, "tests")],
    testPathIgnorePatterns: [
        "<rootDir>/node_modules/"
    ],
    moduleFileExtensions: [
        "ts",
        "tsx",
        "js",
        "jsx"
    ],
    moduleNameMapper: {
        "^.+\\.(css|less|scss)$": "babel-jest",
        "^.+\\.(jpg|png|gif|svg)$": "identity-obj-proxy"
    },
    watchPlugins: [
        "jest-watch-typeahead/filename",
        "jest-watch-typeahead/testname",
    ]
}