import axios, {AxiosRequestConfig} from "axios"
import MockAdapter from "axios-mock-adapter"
import ApiHandler from "../../src/services/api-handler"
import {localStorageMock} from "../utils/mocks"

const mockedAxios = new MockAdapter(axios)
Object.defineProperty(window, "localStorage", {value: localStorageMock})
const getItemSpy = jest.spyOn(localStorage, "getItem")
const removeItemSpy = jest.spyOn(localStorage, "removeItem")

describe("API handler", () => {
    it("has a default behavior for the Cookbook API", () => {
        const api = ApiHandler("http://localhost:8000")

        expect(api.defaults.baseURL).toBe("http://localhost:8000")
        expect(api.defaults.headers.post)
            .toEqual(expect.objectContaining({"Content-Type": "application/json"}))
        expect(api.defaults.headers.put)
            .toEqual(expect.objectContaining({"Content-Type": "application/json"}))

    })

    describe("Interceptors", () => {
        const handlerBody = (config: AxiosRequestConfig): unknown[] | Promise<unknown[]> => {
            if (config.headers.Authorization
                && config.headers.Authorization.startsWith("Bearer")) {
                return [200, []]
            } else {
                return [401]
            }
        }

        beforeEach(() => {
            jest.clearAllMocks()
            mockedAxios.reset()
        })

        describe("Request", () => {
            it("adds the bearer token if it has one to the API calls", async () => {
                localStorage.setItem("token", "A_VALID_TOKEN")
                mockedAxios.onGet("/recipe")
                    .replyOnce(handlerBody)

                const api = ApiHandler("http://localhost:8000")
                const response = await api.get("/recipe")

                expect(response.status).toBe(200)
                expect(response.data).toStrictEqual([])
                expect(getItemSpy).toHaveBeenCalledWith("token")
            })

            it("should cancel the operation if the token isn't set", async () => {
                mockedAxios.onGet("/recipe")
                    .replyOnce(handlerBody)

                const api = ApiHandler("http://localhost:8000")
                await expect(api.get("/recipe")).rejects.toBeInstanceOf(axios.Cancel)
                expect(getItemSpy).toHaveBeenCalledWith("token")
            })
        })

        describe("Response", () => {
            it("clears the token if the server responds with 401", async () => {
                localStorage.setItem("token", "EXPIRED_TOKEN")
                mockedAxios.onGet("/recipe")
                    .replyOnce(() => [401])

                const api = ApiHandler("http://localhost:8000")
                await api.get("/recipe").catch(() => {
                    expect(removeItemSpy).toHaveBeenCalled()
                })
            })
        })
    })
})