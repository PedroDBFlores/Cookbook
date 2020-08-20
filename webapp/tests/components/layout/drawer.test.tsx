import React from "react"
import Drawer from "../../../src/components/layout/drawer"
import {renderWithRouter, renderWithRoutes} from "../../render"
import {screen, fireEvent} from "@testing-library/react"

describe("Drawer", () => {
    it("renders the basic drawer", () => {
        renderWithRouter(<Drawer isOpen={true} items={[]} onClose={jest.fn()}/>)

        expect(screen.getByLabelText(/Close the drawer/i)).toBeInTheDocument()
    })

    it("renders the provided items", () => {
        const drawerItems = [
            {name: "Item1", icon: null, route: "/route/1"},
            {name: "Item2", icon: null, route: "/route/2"}
        ]
        renderWithRouter(<Drawer isOpen={true} items={drawerItems} onClose={jest.fn()}/>)

        drawerItems.forEach(item => {
            expect(screen.getByLabelText(`${item.name} menu`)).toBeInTheDocument()
            expect(screen.getByLabelText(`${item.name} menu icon`)).toBeInTheDocument()
            expect(screen.getByText(item.name)).toBeInTheDocument()
        })
    })

    test.each([
        ["Item1 menu", "/route/1"],
        ["Item2 menu", "/route/2"]
    ])("navigates to the %s", (itemLabel, expectedRoute) => {
        const drawerItems = [
            {name: "Item1", icon: null, route: "/route/1"},
            {name: "Item2", icon: null, route: "/route/2"}
        ]
        renderWithRoutes({
            "/": () => <Drawer isOpen={true} items={drawerItems} onClose={jest.fn()}/>,
            [expectedRoute]: () => <div>I'm the {itemLabel} page</div>
        })

        const listItem = screen.getByLabelText(itemLabel)
        fireEvent.click(listItem)

        expect(screen.getByText(`I'm the ${itemLabel} page`)).toBeInTheDocument()
    })

    it("call the onClose function", () => {
        const onCloseMock = jest.fn()
        renderWithRouter(<Drawer isOpen={true} items={[]} onClose={onCloseMock}/>)

        const closeButton = screen.getByLabelText(/close the drawer/i)
        fireEvent.click(closeButton)

        expect(onCloseMock).toHaveBeenCalled()
    })
})