import React from "react"
import {renderWithRoutes} from "../../../render"
import Logout from "../../../../src/features/user/logout/logout"
import {fireEvent, screen, waitFor} from "@testing-library/react"
import {Link} from "react-router-dom"
import {AuthContext, AuthInfo} from "../../../../src/services/credentials-service"

const logoutMock = jest.fn().mockImplementation(() => {
    localStorage.removeItem("token")
    return Promise.resolve()
})
const updateAuthContextMock = jest.fn()

describe("Logout component", () => {
    beforeEach(() => {
        jest.clearAllMocks()
    })

    const wrapLogoutInContext = (authInfo: AuthInfo = {
        userId: 1,
        name: "name",
        userName: "username"
    }) =>
        <AuthContext.Provider value={authInfo}>
            <Logout onLogout={logoutMock} onUpdateAuth={updateAuthContextMock}/>
        </AuthContext.Provider>

    it("ends your session", async () => {
        localStorage.setItem("token", "A_TOKEN")
        renderWithRoutes({
            "/": () => <>
                {localStorage.getItem("token")
                    ? <Link to="/logout">Logout</Link>
                    : <span>Logged Out</span>}
            </>,
            "/logout": () => wrapLogoutInContext()
        }, "/")

        fireEvent.click(screen.getByText(/logout/i))

        expect(screen.getByText(/logging you out/i)).toBeInTheDocument()
        await waitFor(() => {
            expect(logoutMock).toHaveBeenCalled()
            expect(updateAuthContextMock).toHaveBeenCalledWith(undefined)
            expect(screen.getByText(/logged out/i)).toBeInTheDocument()
        })
    })
})