import React from "react"
import ApplicationToolbar from "../../../src/components/layout/application-toolbar"
import {renderWithRouter, renderWithRoutes} from "../../render"
import {screen, fireEvent, waitFor} from "@testing-library/react"
import {random} from "faker"

describe("Application Toolbar", () => {
    describe("Layout", () => {
        it("has the application title", () => {
            const expectedTitle = random.word()
            renderWithRouter(<ApplicationToolbar title={expectedTitle}/>)

            expect(screen.getByText(expectedTitle)).toBeInTheDocument()
        })

        describe("Menus", () => {
            it("has an Administration menu", () => {
                renderWithRouter(<ApplicationToolbar title={"title"}/>)
                expect(screen.getByLabelText(/administration menu dropdown/i)).toBeInTheDocument()
            })

            it(`navigates to the recipe types page`, async () => {
                renderWithRoutes({
                    "/": () => <ApplicationToolbar title={"title"}/>,
                    "/recipetype": () => <div>I'm the recipe types page</div>
                })

                const adminMenu = screen.getByLabelText(/administration menu dropdown/i)
                fireEvent.click(adminMenu)
                screen.debug()
                const userLink = screen.getByLabelText(/recipe types option/i)
                fireEvent.click(userLink)

                await waitFor(() => {
                    expect(screen.getByText(/i'm the recipe types page/i)).toBeInTheDocument()
                })
            })
        })
    })
})
