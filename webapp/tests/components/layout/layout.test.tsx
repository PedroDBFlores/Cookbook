import React from "react"
import Layout from "../../../src/components/layout/layout"
import {renderWithRouter} from "../../render"
import {screen} from "@testing-library/react"

describe("Application layout", () => {
    it("renders the layout with content", () => {
        renderWithRouter(<Layout>
            <div>I'm blinded by the light</div>
        </Layout>)

        expect(screen.getByText("Cookbook")).toBeInTheDocument()
        expect(screen.getByText("I'm blinded by the light")).toBeInTheDocument()
    })
})