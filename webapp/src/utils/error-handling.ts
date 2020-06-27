import { AxiosError } from "axios"

interface ApiError extends Error {
    status: number
}

interface ApiErrorConstructor {
    new(message: string, status: number): ApiError
    (message: string): ApiError
    readonly prototype: ApiError
}

declare const ApiError: ApiErrorConstructor

export const handleApiError = (err: AxiosError): never => {
    console.log("CALLED")
    throw {
        message: err.response?.data,
        status: err.response?.status
    } as ApiError
}

export {ApiError}
export default handleApiError