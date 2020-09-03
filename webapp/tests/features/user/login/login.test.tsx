import React from "react"
import {fireEvent, render, screen, waitFor} from "@testing-library/react"
import Login from "../../../../src/features/user/login/login"
import {CredentialsService} from "../../../../src/services/credentials-service"
import clearAllMocks = jest.clearAllMocks;
import {renderWithRoutes} from "../../../render"
import {Link} from "react-router-dom"

const loginMock = jest.fn()
const credentialsServiceMock = {
    login: loginMock,
    logout: jest.fn()
} as CredentialsService

describe("Login component", () => {
    beforeEach(() => {
        clearAllMocks()
    })

    it("renders the initial component", () => {
        render(<Login credentialsService={credentialsServiceMock}/>)

        expect(screen.getByText(/login user/i)).toBeInTheDocument()
        expect(screen.getByText(/username/i)).toBeInTheDocument()
        expect(screen.getByLabelText(/username/i)).toBeInTheDocument()
        expect(screen.getByText(/password/i)).toBeInTheDocument()
        expect(screen.getByLabelText(/password/i)).toBeInTheDocument()
        const submitButton = screen.getByLabelText(/login to application/i)
        expect(submitButton).toHaveAttribute("type", "submit")
    })

    test.each([
        ["the username is undefined or empty", undefined, "password", "Username is required"],
        ["the password is undefined or empty", "username", undefined, "Password is required"]
    ])("displays an error when %s", async (_, userName, password, expectedMessage) => {
        render(<Login credentialsService={credentialsServiceMock}/>)
        const userNameInput = screen.getByLabelText(/username/i)
        const passwordInput = screen.getByLabelText(/password/i)
        const submitButton = screen.getByLabelText(/login to application/i)

        fireEvent.change(userNameInput, {target: {value: userName}})
        fireEvent.change(passwordInput, {target: {value: password}})
        fireEvent.submit(submitButton)

        await waitFor(() => expect(screen.getByText(expectedMessage)).toBeInTheDocument())
    })

    it("logs in to the application and navigates back from where you came from", async () => {
        loginMock.mockResolvedValueOnce({})
        renderWithRoutes({
            "/login": () => <Login credentialsService={credentialsServiceMock}/>,
            "/recipetype": () => <>
                <div>I'm the recipe type page</div>
                <Link to="/login" aria-label="Go to login">Go to login</Link></>
        }, "/recipetype")
        fireEvent.click(screen.getByLabelText(/go to login/i))
        await waitFor(() => expect(screen.getByText(/login user/i)).toBeInTheDocument())
        const userNameInput = screen.getByLabelText(/username/i)
        const passwordInput = screen.getByLabelText(/password/i)
        const submitButton = screen.getByLabelText(/login to application/i)

        fireEvent.change(userNameInput, {target: {value: "username"}})
        fireEvent.change(passwordInput, {target: {value: "password"}})
        fireEvent.submit(submitButton)

        await waitFor(() => {
            expect(loginMock).toHaveBeenCalledWith({userName: "username", password: "password"})
            expect(screen.getByText(/I'm the recipe type page/i)).toBeInTheDocument()
        })
    })
})