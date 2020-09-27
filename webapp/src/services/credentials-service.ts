import axios from "axios"
import handleApiError from "../utils/error-handling"
import jwt_decode from "jwt-decode"
import React from "react"

export interface CredentialsService {
    login(credentials: Credentials): Promise<AuthInfo>

    logout(): Promise<void>
}

interface LoginResult {
    token: string
}

export interface AuthInfo {
    userId: number
    name: string
    userName: string
}

export interface Credentials {
    userName: string
    password: string
}

const login = async (credentials: Credentials): Promise<AuthInfo> => {
    try {
        const response = await axios.post<LoginResult>("/user/login", credentials,
            {
                baseURL: "http://localhost:9000",
            })
        localStorage.setItem("token", response.data.token)
        const {sub, name, userName} = jwt_decode(response.data.token)
        return {userId: Number(sub), name, userName}
    } catch (err) {
        throw handleApiError(err)
    }
}

const logout = async (): Promise<void> => localStorage.removeItem("token")

const createCredentialsService = (): CredentialsService => ({
    login: login,
    logout: logout
})

const AuthContext = React.createContext<AuthInfo | undefined>(undefined)
export {AuthContext}

export default createCredentialsService