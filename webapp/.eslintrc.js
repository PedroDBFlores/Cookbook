module.exports = {
  "parser": "@typescript-eslint/parser",
  "extends": [
    "eslint:recommended",
    "plugin:react/recommended",
    "plugin:@typescript-eslint/recommended",
    "plugin:jest/recommended",
    "plugin:testing-library/react",
    "plugin:jest-dom/recommended"
  ],
  "env": {
    "browser": true,
    "es6": true,
    "jest": true
  },
  "globals": {
    "Atomics": "readonly",
    "SharedArrayBuffer": "readonly"
  },
  "parserOptions": {
    "ecmaFeatures": {
      "jsx": true
    },
    "ecmaVersion": 2020,
    "sourceType": "module"
  },
  "plugins": [
    "react",
    "jest",
    "testing-library"
  ],
  "settings": {
    "react": {
      "version": "detect"
    }
  },
  "rules": {
    "react/no-unescaped-entities": "off",
    "react/prop-types": "off",
    "prefer-const": "error",
    "semi": [2, "never"],
    "quotes": ["error", "double", { "allowTemplateLiterals": true }],
    "@typescript-eslint/member-delimiter-style": ["error", {
      multiline: {
        delimiter: "none",    // 'none' or 'semi' or 'comma'
        requireLast: true,
      },
      singleline: {
        delimiter: "semi",    // 'semi' or 'comma'
        requireLast: false,
      },
    }],
    "@typescript-eslint/ban-ts-comment": 0,
  },
  "overrides": [
    {
      "files": ["*.test.tsx", "*.test.ts", "tests/**"],
      "rules": {
        "testing-library/prefer-find-by": "error",
        "testing-library/no-debugging-utils": "error",
        "react/display-name": "off",
        "no-restricted-globals": ["error", "console"],
        "react/prop-types": "off",
      }
    }
  ]
}
