import React from "react"
import Layout from "./layout"
import {render, screen} from "@testing-library/react"

jest.mock("components/layout/application-toolbar", () => ({
    __esModule: true,
    default: ({title}: { title: string }) => <>{title}</>
}))

describe("Application layout", () => {
    it("renders the layout with content", () => {
        render(
            <Layout>
                <div>I'm blinded by the light</div>
            </Layout>
        )

        expect(screen.getByText(/translated app-name/i)).toBeInTheDocument()
        expect(screen.getByText(/i'm blinded by the light/i)).toBeInTheDocument()
    })
})
