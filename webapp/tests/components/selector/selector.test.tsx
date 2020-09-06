import React from "react"
import {render, screen, waitFor, fireEvent, within} from "@testing-library/react"
import Selector from "../../../src/components/selector/selector"

describe("Selector component", () => {
    const onLoadMock = jest.fn().mockImplementation(() => Promise.resolve([]))

    beforeEach(() => {
        onLoadMock.mockClear()
    })

    it("renders the basic layout", () => {
        const expectedOptions = [{id: 1, name: "Only one option"}]
        onLoadMock.mockResolvedValueOnce(expectedOptions)
        render(<Selector label="Recipe type"
                         value={""}
                         ariaLabel="Selector for recipe type"
                         onLoad={onLoadMock}/>)

        expect(screen.getAllByLabelText(/recipe type/i)).toBeInTheDocument()
        expect(screen.getByLabelText(/selector for recipe type/i)).toBeInTheDocument()
    })

    it("renders the provided when open", async () => {
        const expectedOptions = [
            {id: 1, name: "ABC"},
            {id: 2, name: "DEF"}
        ]
        onLoadMock.mockResolvedValueOnce(expectedOptions)

        render(<Selector label="Recipe type"
                         ariaLabel="Selector for recipe type"
                         onLoad={onLoadMock}/>)

        expect(onLoadMock).toHaveBeenCalled()
        // Hackish solution to material-UI's implementation of selects
        fireEvent.mouseDown(screen.getByRole("button"))
        await waitFor(() => {
            expectedOptions.forEach(opts => {
                expect(screen.getByText(opts.name)).toBeInTheDocument()
            })
        })
    })
})