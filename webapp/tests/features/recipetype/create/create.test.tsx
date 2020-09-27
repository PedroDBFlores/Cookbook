import React, {ReactNode} from "react"
import {render, screen, fireEvent, waitFor} from "@testing-library/react"
import CreateRecipeType from "../../../../src/features/recipetype/create/create"
import {renderWithRoutes} from "../../../render"
import {SnackbarProvider} from "notistack"

describe("Create recipe type", () => {
    const createRecipeTypeMock = jest.fn()
    beforeEach(() => createRecipeTypeMock.mockReset())

    const WrappedCreateComponent = () =>
        <SnackbarProvider maxSnack={2}>
            <CreateRecipeType onCreate={createRecipeTypeMock}/>
        </SnackbarProvider>

    it("renders the initial form", () => {
        render(<WrappedCreateComponent/>)

        expect(screen.getByText(/create a new recipe type/i)).toBeInTheDocument()
        expect(screen.getByLabelText(/name/i)).toBeInTheDocument()
        const submitButton = screen.getByLabelText(/create recipe type/i)
        expect(submitButton).toHaveAttribute("type", "submit")
        const resetButton = screen.getByLabelText(/reset form/i)
        expect(resetButton).toHaveAttribute("type", "reset")
    })

    describe("Form validation", () => {
        it("displays an error when the name is empty on submitting", async () => {
            render(<WrappedCreateComponent/>)

            const submitButton = screen.getByLabelText(/create recipe type/i)
            fireEvent.submit(submitButton)

            await waitFor(() =>
                expect(screen.getByText(/name is required/i)).toBeInTheDocument()
            )
        })

        it("displays an error when the name exceeds 64 characters", async () => {
            render(<WrappedCreateComponent/>)

            const nameInput = screen.getByLabelText(/name/i)
            fireEvent.change(nameInput, {target: {value: "a".repeat(65)}})
            const submitButton = screen.getByLabelText(/create recipe type/i)
            fireEvent.submit(submitButton)

            await waitFor(() =>
                expect(screen.getByText(/name exceeds the character limit/i)).toBeInTheDocument()
            )
        })
    })

    it("create the recipe type in the Cookbook API and navigates to the details", async () => {
        createRecipeTypeMock.mockResolvedValueOnce({id: 1})
        renderWithRoutes({
            "/recipetype/new": () => <WrappedCreateComponent/>,
            "/recipetype/1": () => <div>I'm the recipe type details page for id 1</div>
        }, "/recipetype/new")

        const nameInput = screen.getByLabelText(/name/i)
        fireEvent.change(nameInput, {target: {value: "Fish"}})
        const submitButton = screen.getByLabelText(/create recipe type/i)
        fireEvent.submit(submitButton)

        await waitFor(() => {
            expect(createRecipeTypeMock).toHaveBeenCalledWith({name: "Fish"})
            expect(screen.getByText(/i'm the recipe type details page for id 1/i)).toBeInTheDocument()
        })
    })

    it("shows an error message if the create API call fails", async () => {
        createRecipeTypeMock.mockRejectedValueOnce({message: "Duplicate recipe type"})
        render(<WrappedCreateComponent/>)

        const nameInput = screen.getByLabelText(/name/i)
        fireEvent.change(nameInput, {target: {value: "Fish"}})
        const submitButton = screen.getByLabelText(/create recipe type/i)
        fireEvent.submit(submitButton)

        await waitFor(() => {
            expect(createRecipeTypeMock).toHaveBeenCalledWith({name: "Fish"})
            expect(screen.getByText(/an error occurred while creating the recipe type: duplicate recipe type/i)).toBeInTheDocument()
        })
    })
})
