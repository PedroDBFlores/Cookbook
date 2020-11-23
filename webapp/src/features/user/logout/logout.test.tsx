import React from "react"
import {WrapperWithRoutes} from "../../../../tests/render-helpers"
import Logout from "./logout"
import {render, screen} from "@testing-library/react"
import {Link} from "react-router-dom"
import {AuthContext, AuthInfo} from "../../../services/credentials-service/credentials-service"
import userEvent from "@testing-library/user-event"

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
        render(
            <WrapperWithRoutes routeConfiguration={[
                {
                    path: "/", exact: true, component: () => <>
                        {localStorage.getItem("token")
                            ? <Link to="/logout">Logout</Link>
                            : <span>Logged Out</span>}
                    </>
                },
                {
                    path: "/logout", exact: false, component: () => wrapLogoutInContext()
                }
            ]}/>
        )

        userEvent.click(screen.getByText(/logout/i))

        expect(screen.getByText(/logging you out/i)).toBeInTheDocument()
        expect(await screen.findByText(/logged out/i)).toBeInTheDocument()
        expect(logoutMock).toHaveBeenCalled()
        expect(updateAuthContextMock).toHaveBeenCalledWith(undefined)
    })
})