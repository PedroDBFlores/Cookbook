import { AxiosError } from "axios"
import handleError, { ApiError } from "../../src/utils/error-handling"
import 'jest-extended'
import 'jest-chain'

describe("Error handling", () => {
    it("handles the errors provided by the API", () => {
        const axiosError = {
            response: {
                data: "Database error",
                status: 500
            }
        } as AxiosError

        expect(() => handleError(axiosError)).toThrowError(ApiError)
    })
})