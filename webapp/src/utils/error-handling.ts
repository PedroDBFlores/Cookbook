import { AxiosError } from "axios"

interface ApiError extends Error {
    code: string
}

interface ApiErrorConstructor {
    new(message: string, code: string): ApiError
    (message: string): ApiError
    readonly prototype: ApiError
}

declare const ApiError: ApiErrorConstructor

export const handleApiError = (err: AxiosError): never => {
    throw {
        message: err.response?.data.message,
        code: err.response?.data.code
    } as ApiError
}

export {ApiError}
export default handleApiError
