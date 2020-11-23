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
    "ecmaVersion": 2019,
    "sourceType": "module"
  },
  "plugins": [
    "react",
    "jest",
    "testing-library"
  ],
  "settings": {
    "react": {
      "version": "detect" // Tells eslint-plugin-react to automatically detect the version of React to use
    }
  },
  "rules": {
    "react/no-unescaped-entities": "off",
    "react/display-name": ["off"],
    "react/prop-types": [2, { ignore: ['children'] }],
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
    "testing-library/no-debug": "error",
    "testing-library/prefer-find-by": "warn"
  },
  "overrides": [
    {
      "files": ["*.test.tsx", "*.test.ts"],
      "rules": {
        "react/display-name": "off",
        "no-restricted-globals": ["error", "console"],
      }
    }
  ]
}