import React, {useEffect} from "react"
import {render, screen, waitFor, fireEvent} from "@testing-library/react"
import RecipeTypeDetails from "../../../../src/features/recipetype/details/details"
import {findRecipeType} from "../../../../src/services/recipe-type-service"
import {generateRecipeType} from "../../../helpers/generators/dto-generators"
import BasicModalDialog from "../../../../src/components/modal/basic-modal-dialog"

jest.mock("../../../../src/services/recipe-type-service")
const findRecipeTypeMock = findRecipeType as jest.MockedFunction<typeof findRecipeType>

jest.mock("../../../../src/components/modal/basic-modal-dialog", () => {
    return {
        __esModule: true,
        default: jest.fn().mockImplementation(() => <div>Delete RecipeType Modal</div>)
    }
})
const basicModalDialogMock = BasicModalDialog as jest.MockedFunction<typeof BasicModalDialog>

describe("Recipe type details", () => {
    it("renders the recipe type details", async () => {
        const expectedRecipeType = generateRecipeType()
        findRecipeTypeMock.mockResolvedValueOnce(expectedRecipeType)
        render(<RecipeTypeDetails id={99} onDelete={jest.fn()}/>)

        expect(screen.getByText(/loading.../i)).toBeInTheDocument()

        await waitFor(() => {
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

    it("deletes the user", async () => {
        const onDeleteMock = jest.fn()
        const expectedRecipeType = generateRecipeType()
        findRecipeTypeMock.mockResolvedValueOnce(expectedRecipeType)
        basicModalDialogMock.mockImplementationOnce(({dismiss}) => {
            useEffect(() => dismiss.onDismiss(), [])
            return <div>Are you sure you want to delete this recipe type?</div>
        })
        render(<RecipeTypeDetails id={expectedRecipeType.id} onDelete={onDeleteMock}/>)

        await waitFor(() => {
            expect(findRecipeTypeMock).toHaveBeenCalled()
            expect(screen.getByText(/Id:/i)).toBeInTheDocument()
            expect(screen.getByText(/Name:/i)).toBeInTheDocument()
        })

        const deleteButton = screen.getByLabelText(`Delete recipe type with id ${expectedRecipeType.id}`)
        fireEvent.click(deleteButton)

        expect(basicModalDialogMock).toHaveBeenCalled()
        expect(screen.getByText(/are you sure you want to delete this recipe type?/i)).toBeInTheDocument()
        expect(onDeleteMock).toHaveBeenCalledWith(expectedRecipeType.id)
    })
})
