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

export const handleError = (err: AxiosError): never => {
    throw {
        message: err.response?.data,
        status: err.response?.status
    } as ApiError
}

export {ApiError}
export default handleError