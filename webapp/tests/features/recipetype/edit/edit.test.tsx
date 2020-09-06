import {fireEvent, render, screen, waitFor} from "@testing-library/react"
import React from "react"
import EditRecipeType from "../../../../src/features/recipetype/edit/edit"
import {generateRecipeType} from "../../../helpers/generators/dto-generators"
import {renderWithRoutes} from "../../../render"
import {SnackbarProvider} from "notistack"

const findRecipeTypeMock = jest.fn()
const updateRecipeTypeMock = jest.fn()

const wrappedEditComponent = (children: React.ReactNode) =>
    <SnackbarProvider maxSnack={4}>
        {children}
    </SnackbarProvider>

describe("Edit recipe type", () => {
    it("renders the initial form", async () => {
        const expectedRecipeType = generateRecipeType()
        findRecipeTypeMock.mockResolvedValueOnce(expectedRecipeType)
        render(wrappedEditComponent(<EditRecipeType id={expectedRecipeType.id} onFind={findRecipeTypeMock}
                                                    onUpdate={updateRecipeTypeMock}/>))

        expect(screen.getByText(/loading.../i)).toBeInTheDocument()

        await waitFor(() => {
            expect(findRecipeTypeMock).toHaveBeenCalledWith(expectedRecipeType.id)
            expect(screen.getByText(/edit a recipe type/i)).toBeInTheDocument()
            expect(screen.getByText(/name/i)).toBeInTheDocument()
            expect(screen.getByLabelText(/name/i)).toBeInTheDocument()
            expect(screen.getByLabelText(/name/i)).toHaveAttribute("value", expectedRecipeType.name)
            const submitButton = screen.getByLabelText(/edit recipe type/i)
            expect(submitButton).toHaveAttribute("type", "submit")
        })
    })

    it("renders an error if the recipe type cannot be obtained", async () => {
        findRecipeTypeMock.mockRejectedValueOnce({message: "Failure"})
        render(wrappedEditComponent(<EditRecipeType id={99} onFind={findRecipeTypeMock}
                                                    onUpdate={updateRecipeTypeMock}/>))

        expect(screen.getByText(/loading.../i)).toBeInTheDocument()
        await waitFor(() => {
            expect(findRecipeTypeMock).toHaveBeenCalled()
            expect(screen.getByText(/failure/i)).toBeInTheDocument()
        })
    })

    it("displays an error when the name is empty on submitting", async () => {
        const expectedRecipeType = generateRecipeType()
        findRecipeTypeMock.mockResolvedValueOnce(expectedRecipeType)
        render(wrappedEditComponent(<EditRecipeType id={expectedRecipeType.id}
                                                    onFind={findRecipeTypeMock}
                                                    onUpdate={updateRecipeTypeMock}/>))

        await waitFor(() => expect(screen.getByText(/edit a recipe type/i)).toBeInTheDocument())

        const nameInput = screen.getByLabelText(/name/i)
        fireEvent.change(nameInput, {target: {value: ""}})

        const submitButton = screen.getByLabelText(/edit recipe type/i)
        fireEvent.submit(submitButton)

        await waitFor(() =>
            expect(screen.getByText(/name is required/i)).toBeInTheDocument()
        )
    })

    it("updates the recipe type in the Cookbook API and navigates to the details", async () => {
        const expectedRecipeType = generateRecipeType()
        findRecipeTypeMock.mockResolvedValueOnce(expectedRecipeType)
        updateRecipeTypeMock.mockResolvedValueOnce({})

        renderWithRoutes({
            [`/recipetype/${expectedRecipeType.id}/edit`]: () => wrappedEditComponent(<EditRecipeType
                id={expectedRecipeType.id} onFind={findRecipeTypeMock}
                onUpdate={updateRecipeTypeMock}/>),
            [`/recipetype/${expectedRecipeType.id}`]: () => <div>I'm the recipe type details page</div>
        }, `/recipetype/${expectedRecipeType.id}/edit`)

        await waitFor(() => expect(screen.getByText(/edit a recipe type/i)).toBeInTheDocument())

        const nameInput = screen.getByLabelText(/name/i)
        fireEvent.change(nameInput, {target: {value: "Japanese"}})

        const submitButton = screen.getByLabelText(/edit recipe type/i)
        fireEvent.submit(submitButton)

        await waitFor(() => {
            expect(updateRecipeTypeMock).toHaveBeenCalledWith({...expectedRecipeType, name: "Japanese"})
            expect(screen.getByText("I'm the recipe type details page")).toBeInTheDocument()
        })
    })

    it("shows an error message if the update API call fails", async () => {
        const expectedRecipeType = generateRecipeType()
        findRecipeTypeMock.mockResolvedValueOnce(expectedRecipeType)
        updateRecipeTypeMock.mockRejectedValueOnce({})

        render(wrappedEditComponent(<EditRecipeType id={expectedRecipeType.id}
                                                    onFind={findRecipeTypeMock}
                                                    onUpdate={updateRecipeTypeMock}/>))

        await waitFor(() => expect(screen.getByText(/edit a recipe type/i)).toBeInTheDocument())

        const nameInput = screen.getByLabelText(/name/i)
        fireEvent.change(nameInput, {target: {value: "Japanese"}})

        const submitButton = screen.getByLabelText(/edit recipe type/i)
        fireEvent.submit(submitButton)

        await waitFor(() => {
            expect(screen.getByText("An error occurred while updating the recipe type")).toBeInTheDocument()
        })
    })
})
