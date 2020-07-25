import React from "react"
import {act, render, screen, waitFor, fireEvent} from "@testing-library/react"
import RecipeTypeDetails from "../../../../src/features/recipetype/details/details"
import {findRecipeType} from "../../../../src/services/recipe-type-service"
import {generateRecipeType} from "../../../helpers/generators/dto-generators"

jest.mock("../../../../src/services/recipe-type-service")
const findRecipeTypeMock = findRecipeType as jest.MockedFunction<typeof findRecipeType>

describe("Recipe type details", () => {
    it("renders the recipe type details", async () => {
        const expectedRecipeType = generateRecipeType()
        findRecipeTypeMock.mockResolvedValue(expectedRecipeType)
        act(() => {
            render(<RecipeTypeDetails id={99} onDelete={jest.fn()} />)
        })

        expect(screen.getByText(/loading.../i)).toBeInTheDocument()

        await waitFor( () => {
            expect(findRecipeTypeMock).toHaveBeenCalled()
            expect(screen.getByText(/Id:/i)).toBeInTheDocument()
            expect(screen.getByText(/Name:/i)).toBeInTheDocument()
            expect(screen.getByText(expectedRecipeType.id.toString())).toBeInTheDocument()
            expect(screen.getByText(expectedRecipeType.name)).toBeInTheDocument()
        })
    })

    it("renders an error if the recipe type cannot be obtained", async () => {
        findRecipeTypeMock.mockRejectedValueOnce({message: "Failure"})
        render(<RecipeTypeDetails id={99} onDelete={jest.fn()}/>)

        expect(screen.getByText(/loading.../i)).toBeInTheDocument()
        await waitFor(() => {
            expect(findRecipeTypeMock).toHaveBeenCalled()
            expect(screen.getByText(/failure/i)).toBeInTheDocument()
        })
    })

    it("deletes the user", async() => {
        const onDeleteMock = jest.fn()
        const expectedRecipeType = generateRecipeType()
        findRecipeTypeMock.mockResolvedValue(expectedRecipeType)
        render(<RecipeTypeDetails id={expectedRecipeType.id} onDelete={onDeleteMock}/>)

        await waitFor( () => {
            expect(findRecipeTypeMock).toHaveBeenCalled()
            expect(screen.getByText(/Id:/i)).toBeInTheDocument()
            expect(screen.getByText(/Name:/i)).toBeInTheDocument()
        })

        const deleteButton = screen.getByLabelText(`Delete recipe type with id ${expectedRecipeType.id}`)
        fireEvent.click(deleteButton)

        expect(onDeleteMock).toHaveBeenCalledWith(expectedRecipeType.id)
    })
})
