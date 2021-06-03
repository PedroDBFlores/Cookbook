import {render, screen} from "@testing-library/react"
import React from "react"
import Loader from "./loader"

describe("Loader", () => {
    it("has the required layout", () => {
        render(<Loader/>)

        expect(screen.getByRole("progressbar")).toBeInTheDocument()
        expect(screen.getByText(/translated common.loading/i)).toBeInTheDocument()
    })
})
