import ApiHandler from "./api-handler"

describe("API handler", () => {
    it("has a default behavior for the Cookbook API", () => {
        const api = ApiHandler("http://localhost:8000")

        expect(api.defaults.baseURL).toBe("http://localhost:8000")
        expect(api.defaults.headers.post)
            .toEqual(expect.objectContaining({"Content-Type": "application/json"}))
        expect(api.defaults.headers.put)
            .toEqual(expect.objectContaining({"Content-Type": "application/json"}))

    })
})
