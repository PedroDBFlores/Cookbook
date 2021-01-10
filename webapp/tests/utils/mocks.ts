export const localStorageMock = (function() {
    let store: { [key: string]: string } = {}

    return {
        getItem: (key: string) => store[key],
        setItem: (key: string, value: string) => {
            store[key] = value.toString()
        },
        clear: () => {
            store = {}
        },
        removeItem: (key: string) => {
            delete store[key]
        }
    }
}())
