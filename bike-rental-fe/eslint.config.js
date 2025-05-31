import js from "@eslint/js";
import globals from "globals";
import reactHooks from "eslint-plugin-react-hooks";
import reactRefresh from "eslint-plugin-react-refresh";
import tseslint from "typescript-eslint";
import stylistic from "@stylistic/eslint-plugin";

export default tseslint.config(
  { ignores: ["dist"] },
  {
    extends: [js.configs.recommended, ...tseslint.configs.recommendedTypeChecked],
    files: ["**/*.{ts,tsx}"],
    languageOptions: {
      ecmaVersion: 2020,
      globals: globals.browser,
      parserOptions: {
        projectService: true,
        tsconfigRootDir: import.meta.dirname,
      },
    },
    plugins: {
      "react-hooks": reactHooks,
      "react-refresh": reactRefresh,
      "@stylistic": stylistic,
    },
    rules: {
      ...reactHooks.configs.recommended.rules,
      "react-refresh/only-export-components": [
        "warn",
        { allowConstantExport: true },
      ],
      "sort-imports": ["error", { "ignoreDeclarationSort": true }],
      "no-undef": ["error"],
      "no-var": ["error"],
      "no-use-before-define": ["error"],
      "@stylistic/no-tabs": ["error"],
      "@stylistic/quotes": ["error"],
      "@stylistic/no-trailing-spaces": ["error"],
      "@stylistic/semi": ["error"],
      "@stylistic/indent": ["error", 2, { "ignoredNodes": ["JSXAttribute"] }],
      "@stylistic/indent-binary-ops": ["error", 2],
      "@stylistic/keyword-spacing": ["error"],
      "@stylistic/brace-style": ["error"],
      "eol-last": ["error"],

      "@stylistic/key-spacing": ["error"],
      "@stylistic/object-curly-spacing": ["error", "always"],

      "@stylistic/jsx-indent-props": ["error", 2],
      "@stylistic/jsx-curly-spacing": [2, { "when": "never", "children": true }],
      "@stylistic/jsx-closing-bracket-location": ["error"],

      "@typescript-eslint/prefer-promise-reject-errors": "off",
      "@typescript-eslint/no-unnecessary-condition": ["error"],
    },
  },
)
