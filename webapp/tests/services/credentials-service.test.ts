import axios from "axios"
import MockAdapter from "axios-mock-adapter"
import * as errorHandler from "../../src/utils/error-handling"
import {internet} from "faker"
import {Credentials} from "../../src/model"
import {localStorageMock} from "../utils/mocks"
import createCredentialsService from "../../src/services/credentials-service"

const mockedAxios = new MockAdapter(axios)
Object.defineProperty(window, "localStorage", {value: localStorageMock})

const service = createCredentialsService()
const handleErrorsSpy = jest.spyOn(errorHandler, "default")
const setItemSpy = jest.spyOn(localStorage, "setItem")
const removeItemSpy = jest.spyOn(localStorage, "removeItem")

describe("Credentials service", () => {
    beforeEach(() => {
        mockedAxios.reset()
        localStorage.clear()
    })

    describe("Login", () => {
        const credentials = {
            userName: internet.userName(),
            password: internet.password()
        } as Credentials

        it("logs in the API and saves the JWT Token", async () => {
            mockedAxios.onPost("/user/login", credentials)
                .replyOnce(200, {token: "A_TOKEN"})

            await service.login(credentials)

            expect(mockedAxios.history.post.length).toBe(1)
            expect(mockedAxios.history.post[0].url).toBe("/user/login")
            expect(setItemSpy).toHaveBeenCalledWith("token", "A_TOKEN")
        })

        it("calls the error handler", async () => {
            mockedAxios.onPost("/user/login")
                .replyOnce(401, {
                    code: "UNAUTHORIZED",
                    message: "Invalid credentials"
                })

            await service.login(credentials).catch(() => {
                expect(handleErrorsSpy).toHaveBeenCalled()
                expect(setItemSpy).not.toHaveBeenCalled()
            })
        })
    })

    it("logs out the client", async () => {
        await service.logout()

        expect(removeItemSpy).toHaveBeenCalledWith("token")
    })
})