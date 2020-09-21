import axios from "axios"
import MockAdapter from "axios-mock-adapter"
import * as errorHandler from "../../src/utils/error-handling"
import {internet} from "faker"
import {localStorageMock} from "../utils/mocks"
import createCredentialsService, {Credentials} from "../../src/services/credentials-service"

const mockedAxios = new MockAdapter(axios)
Object.defineProperty(window, "localStorage", {value: localStorageMock})

const service = createCredentialsService()
const handleErrorsSpy = jest.spyOn(errorHandler, "default")
const setItemSpy = jest.spyOn(localStorage, "setItem")
const removeItemSpy = jest.spyOn(localStorage, "removeItem")

const baseToken = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.eyJhdWQiOiJteS1hdWRpZW5jZSIsInN1YiI6IjEyMyIsInJvbGVzIjpbIlVTRVIiXSwiaXNzIjoiaHR0cDovL215LWRvbWFpbiIsIm5hbWUiOiJNYXJjbyBQYXVsbyIsImV4cCI6MTYwMDc5MTMwMCwidXNlck5hbWUiOiJtYXJjb19wYXVsbyJ9.ncoTy01EC8T-BBSCFQPHCODRni198AFPMcTz3f5WSYEliFjNuNaqoWq1YLgN8ARZwTFwn1W7udT07E9Sg-5UPg"

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
                .replyOnce(200, {token: baseToken})

            const result = await service.login(credentials)

            expect(mockedAxios.history.post.length).toBe(1)
            expect(mockedAxios.history.post[0].url).toBe("/user/login")
            expect(setItemSpy).toHaveBeenCalledWith("token", baseToken)
            expect(result).toStrictEqual({
                userId: 123,
                name: "Marco Paulo",
                userName: "marco_paulo",
            })
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