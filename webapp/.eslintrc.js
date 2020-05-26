module.exports = {
    "parser": "@typescript-eslint/parser",
    "extends": [
        "eslint:recommended",
        "plugin:react/recommended",
        "plugin:@typescript-eslint/recommended",
        "plugin:jest/recommended",
        "plugin:testing-library/recommended"
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
        "react/no-unescaped-entities": "off"
    },
    "overrides": [
        {
            "files": ["*.test.tsx", "*.test.ts"],
            "rules": {
                "react/display-name": "off"
            }
        }
    ]
}