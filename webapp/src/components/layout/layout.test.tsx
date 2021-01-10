import React from "react"
import Layout from "./layout"
import { WrapperWithRouter } from "../../../tests/render-helpers"
import { render, screen } from "@testing-library/react"

jest.mock("components/layout/application-toolbar", () => ({
    __esModule: true,
    default: () => <>I'm the application toolbar</>
}))

describe("Application layout", () => {
    it("renders the layout with content", () => {
        render(<WrapperWithRouter>
            <Layout>
                <div>I'm blinded by the light</div>
            </Layout>
        </WrapperWithRouter>)

        expect(screen.getByText(/i'm the application toolbar/i)).toBeInTheDocument()
        expect(screen.getByText(/i'm blinded by the light/i)).toBeInTheDocument()
    })
})
