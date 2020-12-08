const path = require("path")

module.exports = {
  preset: "ts-jest",
  roots: [
    "<rootDir>/src"
  ],
  transform: {
    "^.+\\.t(s|sx)?$": "ts-jest",
  },
  clearMocks: true,
  setupFilesAfterEnv: ["@testing-library/jest-dom/extend-expect"],
  moduleDirectories: ["node_modules", path.join(__dirname, "src"), path.join(__dirname, "tests")],
  testPathIgnorePatterns: [
    "<rootDir>/node_modules/"
  ],
  moduleFileExtensions: [
    "ts",
    "tsx",
    "js"
  ],
  moduleNameMapper: {
    "^.+\\.(jpg|png|gif|svg)$": "identity-obj-proxy"
  },
  watchPlugins: [
    "jest-watch-typeahead/filename",
    "jest-watch-typeahead/testname",
  ]
}