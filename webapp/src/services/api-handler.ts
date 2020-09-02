import axios, { AxiosInstance, AxiosRequestConfig, AxiosResponse, AxiosError } from "axios"

const ApiHandler = (baseURL: string): AxiosInstance => {
    const api = axios.create({
        baseURL: baseURL,
        headers: {
            post: {
                "Content-Type": "application/json"
            },
            put: {
                "Content-Type": "application/json"
            }
        }
    })

    api.interceptors.request.use(interceptRequestFulfilledHandler,
        interceptRequestRejectedHandler)
    api.interceptors.response.use(interceptResponseHandler,
        interceptResponseRejectedHandler)

    return api
}

const interceptRequestFulfilledHandler = (config: AxiosRequestConfig): AxiosRequestConfig => {
    const token = localStorage.getItem("token")
    if (token) {
        config.headers.Authorization = `Bearer ${token}`
    } else {
        throw new axios.Cancel("User token not defined")
    }
    return config
}

const interceptRequestRejectedHandler = (error: Error): Promise<Error> => {
    return Promise.reject(error)
}

const interceptResponseHandler = (response: AxiosResponse): AxiosResponse => {
    return response
}

const interceptResponseRejectedHandler = (error: AxiosError): Promise<AxiosError> => {
    if (error.response && error.response.status === 401) {
        localStorage.removeItem("token")
    }
    return Promise.reject(error)
}

export default ApiHandler
