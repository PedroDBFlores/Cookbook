import {fireEvent, render, screen} from "@testing-library/react"
import React from "react"
import TablePagination from "./table-pagination"
import userEvent from "@testing-library/user-event"

describe("Table pagination actions", () => {
    it("renders the basic elements", () => {
        render(<TablePagination
            count={100}
            page={4}
            rowsPerPage={10}
            onChangeRowsPerPage={jest.fn()}
            onChangePage={jest.fn()}/>)

        expect(screen.getByLabelText(/rows per page/i)).toHaveValue("10")
        expect(screen.getByText(/page 4 of 10/i)).toBeInTheDocument()
        expect(screen.getByLabelText(/first page/i)).toBeInTheDocument()
        expect(screen.getByLabelText(/previous page/i)).toBeInTheDocument()
        expect(screen.getByLabelText(/next page/i)).toBeInTheDocument()
        expect(screen.getByLabelText(/last page/i)).toBeInTheDocument()
    })

    test.each([
        ["5 of 6", {count: 60, page: 5, rowsPerPage: 10, expectedMaxPages: 6}],
        ["1 of 5", {count: 100, page: 1, rowsPerPage: 20, expectedMaxPages: 5}],
        ["2 of 8", {count: 400, page: 2, rowsPerPage: 50, expectedMaxPages: 8}]
    ])("renders the appropriate number of pages (%s)", (_, {count, page, rowsPerPage, expectedMaxPages}) => {
        render(<TablePagination
            count={count}
            page={page}
            rowsPerPage={rowsPerPage}
            onChangeRowsPerPage={jest.fn()}
            onChangePage={jest.fn()}/>)

        expect(screen.getByText(`Page ${page} of ${expectedMaxPages}`)).toBeInTheDocument()
    })

    describe("Actions", () => {
        test.each([
            [20],
            [50],
        ])("changes to %s rows per page", (value) => {
            const onChangeRowsPerPageMock = jest.fn()
            render(<TablePagination
                count={100}
                page={4}
                rowsPerPage={10}
                onChangeRowsPerPage={onChangeRowsPerPageMock}
                onChangePage={jest.fn()}/>)

            fireEvent.change(screen.getByLabelText(/rows per page/i), {
                target: {value}
            })

            expect(onChangeRowsPerPageMock).toHaveBeenCalledWith(value)
        })

        test.each([
            ["first page button is clicked", {label: "first page", expectedPage: 1}],
            ["previous page button is clicked", {label: "previous page", expectedPage: 4}],
            ["next page button is clicked", {label: "next page", expectedPage: 6}],
            ["last page button is clicked", {label: "last page", expectedPage: 10}]
        ])("it calls the 'onChangePage' function when the %s", (_, {label, expectedPage}) => {
            const onChangePageMock = jest.fn()
            render(<TablePagination
                count={100}
                page={5}
                rowsPerPage={10}
                onChangeRowsPerPage={jest.fn()}
                onChangePage={onChangePageMock}/>)

            userEvent.click(screen.getByLabelText(label))

            expect(onChangePageMock).toHaveBeenCalledWith(expectedPage)
        })

        it("has the first page and previous page button disabled when on the first page", () => {
            render(<TablePagination
                count={100}
                page={1}
                rowsPerPage={10}
                onChangeRowsPerPage={jest.fn()}
                onChangePage={jest.fn()}/>)

            expect(screen.getByLabelText(/first page/i)).toBeDisabled()
            expect(screen.getByLabelText(/previous page/i)).toBeDisabled()
            expect(screen.getByLabelText(/next page/i)).toBeEnabled()
            expect(screen.getByLabelText(/last page/i)).toBeEnabled()
        })

        it("has the last page and next page button disabled when on the last page", () => {
            render(<TablePagination
                count={100}
                page={10}
                rowsPerPage={10}
                onChangeRowsPerPage={jest.fn()}
                onChangePage={jest.fn()}/>)

            expect(screen.getByLabelText(/first page/i)).toBeEnabled()
            expect(screen.getByLabelText(/previous page/i)).toBeEnabled()
            expect(screen.getByLabelText(/next page/i)).toBeDisabled()
            expect(screen.getByLabelText(/last page/i)).toBeDisabled()
        })
    })
})