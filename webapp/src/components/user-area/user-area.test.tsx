import React from "react"
import {screen, render} from "@testing-library/react"
import UserArea from "./user-area"
import {AuthContext, AuthInfo} from "../../services/credentials-service/credentials-service"
import userEvent from "@testing-library/user-event"
import {WrapWithCommonContexts, WrapperWithRoutes} from "../../../tests/render-helpers"

describe("User area component", () => {
    describe("when the user isn't logged in", () => {
        it("renders the login button", () => {
            render(<WrapWithCommonContexts authInfo={undefined}>
                <UserArea/>
            </WrapWithCommonContexts>)

            expect(screen.getByLabelText(/login/i, {selector: "button"})).toBeInTheDocument()
            expect(screen.getByText(/login/i)).toBeInTheDocument()
        })

        it("does the login action", async () => {
            render(<WrapWithCommonContexts authInfo={undefined}>
                <WrapperWithRoutes routeConfiguration={
                    [
                        {path: "/", exact: true, component: () => <UserArea/>},
                        {path: "/login", exact: true, component: () => <>I'm the login page</>}
                    ]
                }
                />
            </WrapWithCommonContexts>)

            userEvent.click(screen.getByLabelText(/login/i, {selector: "button"}))

            expect(await screen.findByText(/i'm the login page/i)).toBeInTheDocument()
        })
    })

    describe("when the user is logged in", () => {
        const loggedUserAuthInfo: AuthInfo = {
            userId: 1,
            name: "John Doe",
            userName: "johndoe"
        }

        it("renders the greeting with a logout button", () => {
            render(<WrapWithCommonContexts authInfo={loggedUserAuthInfo}>
                <UserArea/>
            </WrapWithCommonContexts>)

            expect(screen.getByText(/welcome John Doe/i)).toBeInTheDocument()
            expect(screen.getByLabelText(/logout John Doe/i, {selector: "button"})).toBeInTheDocument()
        })

        it("does the logout action", async () => {
            render(<WrapWithCommonContexts authInfo={loggedUserAuthInfo}>
                <WrapperWithRoutes routeConfiguration={
                    [
                        {path: "/", exact: true, component: () => <UserArea/>},
                        {path: "/logout", exact: true, component: () => <>I'm the logout page</>}
                    ]
                }
                />
            </WrapWithCommonContexts>)

            userEvent.click(screen.getByLabelText(/logout John Doe/i, {selector: "button"}))

            expect(await screen.findByText(/i'm the logout page/i)).toBeInTheDocument()
        })
    })
})