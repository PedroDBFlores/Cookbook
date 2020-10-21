import React from "react"
import {render, screen, waitFor} from "@testing-library/react"
import Login from "./login"
import {renderWithRoutes} from "../../../../tests/render"
import {Link} from "react-router-dom"
import {AuthContext, AuthInfo} from "../../../services/credentials-service"
import userEvent from "@testing-library/user-event"

const loginMock = jest.fn()
const updateAuthContextMock = jest.fn()

describe("Login component", () => {
    beforeEach(() => {
        jest.clearAllMocks()
    })

    const wrapLoginInContext = (authInfo: AuthInfo | undefined = undefined) =>
        <AuthContext.Provider value={authInfo}>
            <Login loginFn={loginMock} onUpdateAuth={updateAuthContextMock}/>
        </AuthContext.Provider>

    it("renders the initial component", () => {
        render(wrapLoginInContext())

        expect(screen.getByText(/login user/i)).toBeInTheDocument()
        expect(screen.getByLabelText(/^username$/i)).toHaveAttribute("type", "text")
        expect(screen.getByLabelText(/^password$/i)).toHaveAttribute("type", "password")
        expect(screen.getByLabelText(/login to application/i)).toHaveAttribute("type", "submit")
    })

    it("displays 'You are already logged in as X' if you're already logged in", async () => {
        render(
            wrapLoginInContext({
                    userId: 1,
                    name: "Jacinto",
                    userName: "jac"
                }
            )
        )

        expect(screen.getByText(/you are already logged in as Jacinto \(jac\)/i)).toBeInTheDocument()
    })

    test.each([
        ["the username is undefined or empty", "", "password", "Username is required"],
        ["the password is undefined or empty", "username", "", "Password is required"]
    ])("displays an error when %s", async (_, userName, password, expectedMessage) => {
        render(wrapLoginInContext())

        await userEvent.type(screen.getByLabelText(/^username$/i), userName)
        await userEvent.type(screen.getByLabelText(/^password$/i), password)
        userEvent.click(screen.getByLabelText(/login to application/i))

        expect(await screen.findByText(expectedMessage)).toBeInTheDocument()
    })

    it("logs in to the application and navigates back from where you came from", async () => {
        loginMock.mockResolvedValueOnce({
            userId: 1,
            name: "Jacinto",
            userName: "jac"
        })
        renderWithRoutes({
            "/login": () => wrapLoginInContext(),
            "/recipetype": () => <>
                <div>I'm the recipe type page</div>
                <Link to="/login" aria-label="Go to login">Go to login</Link></>
        }, "/recipetype")

        userEvent.click(screen.getByLabelText(/go to login/i))
        expect(await screen.findByText(/login user/i)).toBeInTheDocument()

        await userEvent.type(screen.getByLabelText(/^username$/i), "jac")
        await userEvent.type(screen.getByLabelText(/^password$/i), "password")
        userEvent.click(screen.getByLabelText(/login to application/i))

        await waitFor(() => {
            expect(loginMock).toHaveBeenCalledWith({userName: "jac", password: "password"})
            expect(updateAuthContextMock).toHaveBeenCalledWith({
                userId: 1,
                name: "Jacinto",
                userName: "jac"
            })
            expect(screen.getByText(/I'm the recipe type page/i)).toBeInTheDocument()
        })
    })
})