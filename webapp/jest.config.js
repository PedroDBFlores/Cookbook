const path = require("path")

module.exports = {
  roots: [
    "<rootDir>/src"
  ],
  transform: {
    "^.+\\.t(s|sx)?$": ["@swc-node/jest", { target: "es2019" }],
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