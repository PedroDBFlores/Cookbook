import { AxiosError } from "axios"

export interface ApiError extends Error {
    code: string
}

export const handleApiError = (err: AxiosError): never => {
    throw {
        message: err.response?.data.message,
        code: err.response?.data.code
    } as ApiError
}

export default handleApiError
