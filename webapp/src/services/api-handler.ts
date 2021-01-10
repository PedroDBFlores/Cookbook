import axios, { AxiosInstance } from "axios"
import React from "react"

const ApiHandler = (baseURL: string): AxiosInstance => {
    const api = axios.create({
        baseURL,
        headers: {
            post: {
                "Content-Type": "application/json"
            },
            put: {
                "Content-Type": "application/json"
            }
        }
    })

    return api
}

export default ApiHandler
export const ApiHandlerContext = React.createContext<AxiosInstance>(ApiHandler("localhost:9000"))
