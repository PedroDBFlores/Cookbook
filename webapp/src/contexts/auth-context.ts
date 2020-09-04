import React from "react"

export interface AuthInfo {
    isLoggedIn: boolean
    userName?: string
}

const AuthContext = React.createContext<AuthInfo>({isLoggedIn: false})
export default AuthContext