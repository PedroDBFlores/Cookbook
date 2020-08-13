import React, {useEffect} from "react"
import {render, screen, waitFor, fireEvent} from "@testing-library/react"
import RecipeTypeDetails from "../../../../src/features/recipetype/details/details"
import {deleteRecipeType, findRecipeType} from "../../../../src/services/recipe-type-service"
import {generateRecipeType} from "../../../helpers/generators/dto-generators"
import BasicModalDialog from "../../../../src/components/modal/basic-modal-dialog"
import {renderWithRoutes} from "../../../render"

jest.mock("../../../../src/services/recipe-type-service")
const findRecipeTypeMock = findRecipeType as jest.MockedFunction<typeof findRecipeType>
const deleteRecipeTypeMock = deleteRecipeType as jest.MockedFunction<typeof deleteRecipeType>

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
        render(<RecipeTypeDetails id={99}/>)

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
        render(<RecipeTypeDetails id={99}/>)

        expect(screen.getByText(/loading.../i)).toBeInTheDocument()
        await waitFor(() => {
            expect(findRecipeTypeMock).toHaveBeenCalled()
            expect(screen.getByText(/failure/i)).toBeInTheDocument()
        })
    })

    it("takes the user to the edit recipe type page", async () => {
        const expectedRecipeType = generateRecipeType()
        findRecipeTypeMock.mockResolvedValueOnce(expectedRecipeType)
        renderWithRoutes({
            [`/recipetype/${expectedRecipeType.id}`]: () => <RecipeTypeDetails id={expectedRecipeType.id}/>,
            [`/recipetype/${expectedRecipeType.id}/edit`]: () => <div>I'm the recipe type edit page</div>
        }, `/recipetype/${expectedRecipeType.id}`)

        await waitFor(() => {
            expect(screen.getByText(expectedRecipeType.name)).toBeInTheDocument()
        })

        const editButton = screen.getByLabelText(`Edit recipe type with id ${expectedRecipeType.id}`)
        fireEvent.click(editButton)

        expect(screen.getByText(/I'm the recipe type edit page/i)).toBeInTheDocument()
    })

    it("deletes the user", async () => {
        const expectedRecipeType = generateRecipeType()
        findRecipeTypeMock.mockResolvedValueOnce(expectedRecipeType)
        deleteRecipeTypeMock.mockResolvedValueOnce()
        basicModalDialogMock.mockImplementationOnce(({dismiss}) => {
            useEffect(() => {dismiss.onDismiss()}, [])
            return <div>Are you sure you want to delete this recipe type?</div>
        })

        renderWithRoutes({
            [`/recipetype/${expectedRecipeType.id}`]: () => <RecipeTypeDetails id={expectedRecipeType.id}/>,
            "/recipetype": () => <div>I'm the recipe type list page</div>
        }, `/recipetype/${expectedRecipeType.id}`)

        await waitFor(() => {
            expect(findRecipeTypeMock).toHaveBeenCalled()
            expect(screen.getByText(/Id:/i)).toBeInTheDocument()
            expect(screen.getByText(/Name:/i)).toBeInTheDocument()
        })

        const deleteButton = screen.getByLabelText(`Delete recipe type with id ${expectedRecipeType.id}`)
        fireEvent.click(deleteButton)

        expect(basicModalDialogMock).toHaveBeenCalled()
        expect(screen.getByText(/are you sure you want to delete this recipe type?/i)).toBeInTheDocument()
        expect(deleteRecipeTypeMock).toHaveBeenCalledWith(expectedRecipeType.id)

        await waitFor(() => {
            expect(screen.getByText(/I'm the recipe type list page/i)).toBeInTheDocument()
        })
    })
})
