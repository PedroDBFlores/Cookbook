import React from "react"
import ApplicationToolbar from "../../../src/components/layout/application-toolbar"
import {renderWithRouter} from "../../render"
import {screen} from "@testing-library/react"
import {random} from "faker"
import userEvent from "@testing-library/user-event"

describe("Application Toolbar", () => {
    describe("Layout", () => {
        it("has the application title", () => {
            const expectedTitle = random.word()
            renderWithRouter(<ApplicationToolbar title={expectedTitle} onMenuClick={jest.fn()} drawerWidth={0}
                                                 isDrawerOpen={false}/>)

            expect(screen.getByText(expectedTitle)).toBeInTheDocument()
        })

        it("executes the provided function when the menu button is clicked", () => {
            const menuClickFn = jest.fn()
            renderWithRouter(<ApplicationToolbar title="Title" onMenuClick={menuClickFn} drawerWidth={0}
                                                 isDrawerOpen={false}/>)

            userEvent.click(screen.getByLabelText("menu"))

            expect(menuClickFn).toHaveBeenCalled()
        })
    })
})
