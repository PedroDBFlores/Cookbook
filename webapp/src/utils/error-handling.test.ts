import { AxiosError } from "axios"
import handleApiError from "./error-handling"

describe("Error handling", () => {
    it("handles the errors provided by the API", async() => {
        const axiosError = {
            response: {
                data: `{
                    "code" : "INTERNAL_SERVER_ERROR"
                    "message: "Database Error"
                }`,
                status: 500
            }
        } as AxiosError

        expect(() => handleApiError(axiosError)).toThrow()
    })
})
