import React from "react"
import Layout from "./layout"
import {WrapperWithRouter} from "../../../tests/render-helpers"
import {render, screen} from "@testing-library/react"

describe("Application layout", () => {
    it("renders the layout with content", () => {
        render(<WrapperWithRouter>
            <Layout>
                <div>I'm blinded by the light</div>
            </Layout>
        </WrapperWithRouter>)

        expect(screen.getByText("Cookbook")).toBeInTheDocument()
        expect(screen.getByText("I'm blinded by the light")).toBeInTheDocument()
    })
})