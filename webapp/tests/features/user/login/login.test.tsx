import React from "react"
import {fireEvent, render, screen, waitFor} from "@testing-library/react"
import Login from "../../../../src/features/user/login/login"
import {renderWithRoutes} from "../../../render"
import {Link} from "react-router-dom"
import {AuthContext, AuthInfo} from "../../../../src/services/credentials-service"

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
        expect(screen.getByLabelText(/username/i)).toBeInTheDocument()
        expect(screen.getByLabelText(/password/i)).toBeInTheDocument()
        const submitButton = screen.getByLabelText(/login to application/i)
        expect(submitButton).toHaveAttribute("type", "submit")
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
        ["the username is undefined or empty", undefined, "password", "Username is required"],
        ["the password is undefined or empty", "username", undefined, "Password is required"]
    ])("displays an error when %s", async (_, userName, password, expectedMessage) => {
        render(wrapLoginInContext())
        const userNameInput = screen.getByLabelText(/username/i)
        const passwordInput = screen.getByLabelText(/password/i)
        const submitButton = screen.getByLabelText(/login to application/i)

        fireEvent.change(userNameInput, {target: {value: userName}})
        fireEvent.change(passwordInput, {target: {value: password}})
        fireEvent.submit(submitButton)

        await waitFor(() => expect(screen.getByText(expectedMessage)).toBeInTheDocument())
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
        fireEvent.click(screen.getByLabelText(/go to login/i))
        await waitFor(() => expect(screen.getByText(/login user/i)).toBeInTheDocument())
        const userNameInput = screen.getByLabelText(/username/i)
        const passwordInput = screen.getByLabelText(/password/i)
        const submitButton = screen.getByLabelText(/login to application/i)

        fireEvent.change(userNameInput, {target: {value: "jac"}})
        fireEvent.change(passwordInput, {target: {value: "password"}})
        fireEvent.submit(submitButton)

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