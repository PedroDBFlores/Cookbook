import { AxiosError } from "axios"

export interface ApiError extends Error {
    code: string
}

export interface ResponseError {
    code: string
    message: string
}

export const handleApiError = (err: AxiosError<ResponseError>): never => {
    throw {
        message: err.response?.data.message,
        code: err.response?.data.code
    } as ApiError
}

export default handleApiError
