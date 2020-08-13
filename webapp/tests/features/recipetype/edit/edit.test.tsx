import {act, fireEvent, render, screen, waitFor} from "@testing-library/react"
import React from "react"
import EditRecipeType from "../../../../src/features/recipetype/edit/edit"
import {findRecipeType, updateRecipeType} from "../../../../src/services/recipe-type-service"
import {generateRecipeType} from "../../../helpers/generators/dto-generators"

jest.mock("../../../../src/services/recipe-type-service")
const findRecipeTypeMock = findRecipeType as jest.MockedFunction<typeof findRecipeType>
const updateRecipeTypeMock = updateRecipeType as jest.MockedFunction<typeof updateRecipeType>


describe("Edit recipe type", () => {
    it("renders the initial form", async () => {
        const expectedRecipeType = generateRecipeType()
        findRecipeTypeMock.mockResolvedValueOnce(expectedRecipeType)
        render(<EditRecipeType id={expectedRecipeType.id}/>)

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
        render(<EditRecipeType id={99}/>)

        expect(screen.getByText(/loading.../i)).toBeInTheDocument()
        await waitFor(() => {
            expect(findRecipeTypeMock).toHaveBeenCalled()
            expect(screen.getByText(/failure/i)).toBeInTheDocument()
        })
    })

    it("displays an error when the name is empty on submitting", async () => {
        const expectedRecipeType = generateRecipeType()
        findRecipeTypeMock.mockResolvedValueOnce(expectedRecipeType)
        render(<EditRecipeType id={expectedRecipeType.id}/>)

        await waitFor(() => expect(screen.getByText(/edit a recipe type/i)).toBeInTheDocument())

        const nameInput = screen.getByLabelText(/name/i)
        fireEvent.change(nameInput, {target: {value: ""}})

        const submitButton = screen.getByLabelText(/edit recipe type/i)
        fireEvent.submit(submitButton)

        await waitFor(() =>
            expect(screen.getByText(/name is required/i)).toBeInTheDocument()
        )
    })
})