import React from "react"
import {render, screen} from "@testing-library/react"
import DataDisplay from "./data-display"

describe("Data display component", () => {
    it("renders data with title and content", () => {
        render(<DataDisplay title="The title" content="The content"/>)

        expect(screen.getByRole("heading")).toHaveTextContent(/^the title$/i)
        expect(screen.getByText(/^the content$/i)).toBeInTheDocument()
    })
})