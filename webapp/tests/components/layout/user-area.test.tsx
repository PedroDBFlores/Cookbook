import React from "react"
import {screen, render, fireEvent, waitFor} from "@testing-library/react"
import UserArea from "../../../src/components/layout/user-area"
import {renderWithRoutes} from "../../render"
import AuthContext, {AuthInfo} from "../../../src/contexts/auth-context"

describe("User area component", () => {
    const wrapUserAreaInContext = (authInfo: AuthInfo = {isLoggedIn: false}) =>
        <AuthContext.Provider value={authInfo}>
            <UserArea/>
        </AuthContext.Provider>

    describe("when the user isn't logged in", () => {
        it("renders the login button", () => {
            render(wrapUserAreaInContext())

            expect(screen.getByLabelText(/login/i, {selector: "button"})).toBeInTheDocument()
            expect(screen.getByText(/login/i)).toBeInTheDocument()
        })

        it("does the login action", async () => {
            renderWithRoutes({
                "/login": () => <>I'm the login page</>,
                "/": () => wrapUserAreaInContext()
            }, "/")
            fireEvent.click(screen.getByLabelText(/login/i, {selector: "button"}))

            await waitFor(() => {
                expect(screen.getByText(/i'm the login page/i)).toBeInTheDocument()
            })
        })
    })

    describe("when the user is logged in", () => {
        const loggedUserAuthInfo: AuthInfo = {isLoggedIn: true, userName: "johndoe"}

        it("renders the greeting with a logout button", () => {
            render(wrapUserAreaInContext(loggedUserAuthInfo))

            expect(screen.getByText(/welcome johndoe/i)).toBeInTheDocument()
            expect(screen.getByLabelText(/logout johndoe/i, {selector: "button"})).toBeInTheDocument()
        })

        it("does the logout action", async () => {
            renderWithRoutes({
                "/logout": () => <>I'm the logout page</>,
                "/": () => wrapUserAreaInContext(loggedUserAuthInfo)
            }, "/")
            fireEvent.click(screen.getByLabelText(/logout johndoe/i, {selector: "button"}))

            await waitFor(() => {
                expect(screen.getByText(/i'm the logout page/i)).toBeInTheDocument()
            })
        })
    })
})