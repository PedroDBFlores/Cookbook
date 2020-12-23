const path = require("path")

module.exports = {
  roots: [
    "<rootDir>/src"
  ],
  testMatch: ["**/*.test.ts", "**/*.test.tsx"],
  transform: {
    "^.+\\.t(s|sx)?$": ["@swc/jest"],
  },
  clearMocks: true,
  setupFilesAfterEnv: ["@testing-library/jest-dom/extend-expect", "jest-chain"],
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