import { render, screen } from "@solidjs/testing-library"

import DataDisplay from "."

describe("Data Display Atom", () => {
    it("renders data with title and content", () => {
        render(() => <DataDisplay title="The title" content="The content" />)

        expect(screen.getByRole("heading")).toHaveTextContent(/^the title$/i)
        expect(screen.getByText(/^the content$/i)).toBeInTheDocument()
    })

    it("should replace newlines by linebreaks in the content", () => {
        render(() => <DataDisplay title="The title" content="The content\nof\nthe world" />)

        expect(screen.queryByText(/The content\nof\nthe world/i)).not.toBeInTheDocument()
        expect(screen.getByText(/the content/i)).toBeInTheDocument()
        expect(screen.getByText(/of/i)).toBeInTheDocument()
        expect(screen.getByText(/the world/i)).toBeInTheDocument()
    })
})