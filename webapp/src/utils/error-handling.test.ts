import { AxiosError, AxiosRequestConfig, AxiosResponseHeaders } from "axios"
import handleApiError, { ResponseError } from "./error-handling"

describe("Error handling", () => {
    it("handles the errors provided by the API", async () => {
        const axiosError = {
            response: {
                data: {
                    code: "INTERNAL_SERVER_ERROR",
                    message: "Database Error"
                },
                status: 500,
                statusText: "Internal Server Error",
                headers: {} as AxiosResponseHeaders,
                config: {} as AxiosRequestConfig,
            },
            config: {} as AxiosRequestConfig,
            isAxiosError: true,
            toJSON: jest.fn(),
            name: "",
            message: "",
        } as AxiosError<ResponseError>

        expect(() => handleApiError(axiosError)).toThrow()
    })
})
