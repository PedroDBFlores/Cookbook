import React from "react"
import ApplicationToolbar from "./application-toolbar"
import {WrapperWithRouter} from "../../../tests/render-helpers"
import {render, screen} from "@testing-library/react"
import {random} from "faker"
import userEvent from "@testing-library/user-event"

describe("Application Toolbar", () => {
    describe("Layout", () => {
        it("has the application title", () => {
            const expectedTitle = random.word()
            render(<WrapperWithRouter>
                <ApplicationToolbar title={expectedTitle} onMenuClick={jest.fn()} drawerWidth={0}
                                    isDrawerOpen={false}/>
            </WrapperWithRouter>)

            expect(screen.getByText(expectedTitle)).toBeInTheDocument()
        })

        it("executes the provided function when the menu button is clicked", () => {
            const menuClickFn = jest.fn()
            render(<WrapperWithRouter>
                <ApplicationToolbar title="Title" onMenuClick={menuClickFn} drawerWidth={0}
                                    isDrawerOpen={false}/>
            </WrapperWithRouter>)

            userEvent.click(screen.getByLabelText("menu"))

            expect(menuClickFn).toHaveBeenCalled()
        })
    })
})
