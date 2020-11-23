import React from "react"
import Drawer from "./drawer"
import {WrapperWithRouter, WrapperWithRoutes} from "../../../tests/render-helpers"
import {render, screen} from "@testing-library/react"
import userEvent from "@testing-library/user-event"

describe("Drawer", () => {
    it("renders the basic drawer", () => {
        render(
            <WrapperWithRouter>
                <Drawer isOpen={true} items={[]} onClose={jest.fn()}/>
            </WrapperWithRouter>)

        expect(screen.getByLabelText(/Close the drawer/i)).toBeInTheDocument()
    })

    it("renders the provided items", () => {
        const drawerItems = [
            {name: "Item1", icon: null, route: "/route/1"},
            {name: "Item2", icon: null, route: "/route/2"}
        ]
        render(
            <WrapperWithRouter>
                <Drawer isOpen={true} items={drawerItems} onClose={jest.fn()}/>
            </WrapperWithRouter>)

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
        const onClose = jest.fn()
        const drawerItems = [
            {name: "Item1", icon: null, route: "/route/1"},
            {name: "Item2", icon: null, route: "/route/2"}
        ]
        render(<WrapperWithRoutes routeConfiguration={[
            {path: "/", exact: true, component: () => <Drawer isOpen={true} items={drawerItems} onClose={onClose}/>},
            {path: expectedRoute, exact: true, component: () => <div>I'm the {itemLabel} page</div>}
        ]}/>)

        userEvent.click(screen.getByLabelText(itemLabel))

        expect(screen.getByText(`I'm the ${itemLabel} page`)).toBeInTheDocument()
        expect(onClose).toHaveBeenCalled()
    })

    it("call the onClose function", () => {
        const onCloseMock = jest.fn()
        render(
            <WrapperWithRouter>
                <Drawer isOpen={true} items={[]} onClose={onCloseMock}/>
            </WrapperWithRouter>)

        userEvent.click(screen.getByLabelText(/close the drawer/i))

        expect(onCloseMock).toHaveBeenCalled()
    })
})