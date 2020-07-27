import React from "react"
import ApplicationToolbar from "../../../src/components/layout/application-toolbar"
import { \renderWithRouter, renderWithRoutes } from "../../render"
import { screen, fireEvent, waitFor } from "@testing-library/react"
import { random } from "faker"

describe("Application Toolbar", () => {
    describe("Layout", () => {
        it("has the application title", () => {
            const expectedTitle = random.word()
            renderWithRouter(<ApplicationToolbar title={expectedTitle} />)

            expect(screen.getByText(expectedTitle)).toBeInTheDocument()
        })

        describe("Menus", () => {
            it("has an Administration menu", () => {
                renderWithRouter(<ApplicationToolbar title={"title"} />)
                expect(screen.getByText("Administration")).toBeInTheDocument()
            })

            it(`navigates to the Users page`, async () => {
                renderWithRoutes({
                    "/": () => <ApplicationToolbar title={"title"} />,
                    "/users": () => <div>I'm the user page</div>
                })

                const adminMenu = await screen.findByText("Administration")
                fireEvent.click(adminMenu)

                const userLink = await screen.findByText("Users")
                fireEvent.click(userLink)
                await waitFor(() => {
                    expect(screen.queryByText("I'm the user page")).toBeInTheDocument()
                })
            })
        })
    })
})
