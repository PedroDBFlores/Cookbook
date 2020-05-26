import React from "react"
import { render, screen } from "@testing-library/react"
import UserList from "../../../../src/features/user/list/list"

describe("User list", () => {
    describe("Render", () => {
        it("shows a table with the required headers", () => {
            render(<UserList />)

            expect(screen.getByText("Name")).toBeInTheDocument()
            expect(screen.getByText("First Name")).toBeInTheDocument()
            expect(screen.getByText("Last Name")).toBeInTheDocument()
            expect(screen.getByText("User Name")).toBeInTheDocument()
            expect(screen.getByText("Email")).toBeInTheDocument()
        })
    })
})