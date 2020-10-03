import {render, screen} from "@testing-library/react"
import React from "react"
import TablePaginationActions from "../../../src/components/table-pagination-actions/table-pagination-actions"
import "jest-chain"
import userEvent from "@testing-library/user-event"

describe("Table pagination actions", () => {
    it("renders the basic elements", () => {
        render(<TablePaginationActions
            count={100}
            page={4}
            rowsPerPage={10}
            onChangePage={jest.fn()}/>)

        expect(screen.getByLabelText(/first page/i)).toBeInTheDocument().toBeEnabled()
        expect(screen.getByLabelText(/previous page/i)).toBeInTheDocument().toBeEnabled()
        expect(screen.getByLabelText(/next page/i)).toBeInTheDocument().toBeEnabled()
        expect(screen.getByLabelText(/last page/i)).toBeInTheDocument().toBeEnabled()
    })

    test.each([
        ["first page button is clicked", {label: "first page", expectedPage: 0}],
        ["previous page button is clicked", {label: "previous page", expectedPage: 4}],
        ["next page button is clicked", {label: "next page", expectedPage: 6}],
        ["last page button is clicked", {label: "last page", expectedPage: 9}]
    ])("it calls the 'onChangePage' function when the %s", (_, {label, expectedPage}) => {
        const onChangePageMock = jest.fn()
        render(<TablePaginationActions
            count={100}
            page={5}
            rowsPerPage={10}
            onChangePage={onChangePageMock}/>)

        userEvent.click(screen.getByLabelText(label))

        expect(onChangePageMock).toHaveBeenCalledWith(expect.anything(), expectedPage)
    })

    it("has the first page and previous page button disabled when on the first page", () => {
        render(<TablePaginationActions
            count={100}
            page={0}
            rowsPerPage={10}
            onChangePage={jest.fn()}/>)

        expect(screen.getByLabelText(/first page/i)).toBeDisabled()
        expect(screen.getByLabelText(/previous page/i)).toBeDisabled()
        expect(screen.getByLabelText(/next page/i)).toBeEnabled()
        expect(screen.getByLabelText(/last page/i)).toBeEnabled()
    })

    it("has the last page and next page button disabled when on the last page", () => {
        render(<TablePaginationActions
            count={100}
            page={9}
            rowsPerPage={10}
            onChangePage={jest.fn()}/>)

        expect(screen.getByLabelText(/first page/i)).toBeEnabled()
        expect(screen.getByLabelText(/previous page/i)).toBeEnabled()
        expect(screen.getByLabelText(/next page/i)).toBeDisabled()
        expect(screen.getByLabelText(/last page/i)).toBeDisabled()
    })
})