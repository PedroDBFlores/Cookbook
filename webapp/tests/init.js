jest.mock("react-i18next", () => ({
  // this mock makes sure any components using the translate hook can use it without a warning being shown
  useTranslation: () => {
    return {
      t: (str, opts) => `translated ${str}`
        + (opts ? "#".padStart(2) + Object.values(opts ?? "").toString() + "#" : ""),
      i18n: {
        changeLanguage: () => new Promise(() => void (0)),
      },
    }
  },
  withTranslation: () => Component => {
    Component.defaultProps = { ...Component.defaultProps, t: () => "" }
    return Component
  },
}))
