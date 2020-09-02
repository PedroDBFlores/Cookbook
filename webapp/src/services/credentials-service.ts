import axios from "axios"
import {Credentials} from "../model"
import {LoginResult} from "../model/login-result"
import handleApiError from "../utils/error-handling"

export interface CredentialsService {
    login(credentials: Credentials): Promise<void>
    logout() : Promise<void>
}

const login = async (credentials: Credentials): Promise<void> => {
    try {
        const response = await axios.post<LoginResult>("/user/login", credentials)
        localStorage.setItem("token", response.data.token)
    } catch (err) {
        throw handleApiError(err)
    }
}

const logout = async () : Promise<void> => localStorage.removeItem("token")

const createCredentialsService = (): CredentialsService => ({
    login: login,
    logout: logout
})
export default createCredentialsService