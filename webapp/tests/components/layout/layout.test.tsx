import React from "react"
import Layout from "../../../src/components/layout/layout"
import {renderWithRouter, renderWithRoutes} from "../../render"
import {screen, fireEvent, waitFor} from "@testing-library/react"

describe("Application layout", () => {
    it("renders the navbar", () => {
        renderWithRouter(<Layout/>)

        expect(screen.getByText("Cookbook")).toBeInTheDocument()
    })

    it("renders the provided children", () => {
        renderWithRouter(<Layout>
            <div>I'm blinded by the light</div>
        </Layout>)

        expect(screen.queryByText("I'm blinded by the light")).toBeInTheDocument()
    })
})