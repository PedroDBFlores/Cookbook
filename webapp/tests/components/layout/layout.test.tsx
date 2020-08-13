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

    describe("Drawer menus", () => {
        it("opens the drawer", () => {
            renderWithRouter(<Layout/>)
            const menuButton = screen.getByLabelText("menu")

            fireEvent.click(menuButton)

            expect(screen.getByLabelText(/recipe types menu/i)).toBeInTheDocument()
            expect(screen.getByText(/recipe types/i)).toBeVisible()
        })

        it(`navigates to the recipe types page`, async () => {
            renderWithRoutes({
                "/": () => <Layout/>,
                "/recipetype": () => <Layout>
                    <div>I'm the recipe types page</div>
                </Layout>
            })

            const recipeTypeMenu = screen.getByLabelText(/recipe types menu/i)
            fireEvent.click(recipeTypeMenu)

            await waitFor(() => {
                expect(screen.getByText(/i'm the recipe types page/i)).toBeInTheDocument()
            })
        })
    })
})