import React from "react"
import {screen, render, fireEvent, waitFor} from "@testing-library/react"
import UserArea from "../../../src/components/user-area/user-area"
import {localStorageMock} from "../../utils/mocks"
import {renderWithRoutes} from "../../render"

Object.defineProperty(window, "localStorage", {value: localStorageMock})

describe("User area component", () => {
    describe("when the user has no token", () => {
        it("renders the login button", () => {
            render(<UserArea/>)

            expect(screen.getByLabelText(/login/i, {selector: "button"})).toBeInTheDocument()
            expect(screen.getByText(/login/i)).toBeInTheDocument()
        })

        it("does the login action", async () => {
            renderWithRoutes({
                "/login": () => <>I'm the login page</>,
                "/": () => <UserArea/>
            }, "/")
            fireEvent.click(screen.getByLabelText(/login/i, {selector: "button"}))

            await waitFor(() => {
                expect(screen.getByText(/i'm the login page/i)).toBeInTheDocument()
            })
        })
    })

    describe("when the user has token", () => {
        beforeEach(() => {
            localStorage.setItem("token", "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c")
        })

        it("renders the greeting with a logout button", () => {
            render(<UserArea/>)

            expect(screen.getByText(/welcome john doe/i)).toBeInTheDocument()
            expect(screen.getByLabelText(/logout john doe/i, {selector: "button"})).toBeInTheDocument()
        })

        it("does the logout action", async () => {
            renderWithRoutes({
                "/logout": () => <>I'm the logout page</>,
                "/": () => <UserArea/>
            }, "/")
            fireEvent.click(screen.getByLabelText(/logout john doe/i, {selector: "button"}))

            await waitFor(() => {
                expect(screen.getByText(/i'm the logout page/i)).toBeInTheDocument()
            })
        })
    })
})