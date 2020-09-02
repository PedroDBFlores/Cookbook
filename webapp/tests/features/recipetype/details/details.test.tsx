import React, {useEffect} from "react"
import {render, screen, waitFor, fireEvent} from "@testing-library/react"
import RecipeTypeDetails from "../../../../src/features/recipetype/details/details"
import {generateRecipeType} from "../../../helpers/generators/dto-generators"
import BasicModalDialog from "../../../../src/components/modal/basic-modal-dialog"
import {renderWithRoutes} from "../../../render"
import {RecipeTypeService} from "../../../../src/services/recipe-type-service"

const findRecipeTypeMock = jest.fn()
const deleteRecipeTypeMock = jest.fn()
const recipeTypeServiceMock = {
    getAll: jest.fn(),
    delete: deleteRecipeTypeMock,
    update: jest.fn(),
    create: jest.fn(),
    find: findRecipeTypeMock
} as RecipeTypeService

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
        render(<RecipeTypeDetails id={99} recipeTypeService={recipeTypeServiceMock}/>)

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
        render(<RecipeTypeDetails id={99} recipeTypeService={recipeTypeServiceMock}/>)

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
            [`/recipetype/${expectedRecipeType.id}`]: () => <RecipeTypeDetails id={expectedRecipeType.id}
                                                                               recipeTypeService={recipeTypeServiceMock}/>,
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
        deleteRecipeTypeMock.mockResolvedValueOnce({})
        basicModalDialogMock.mockImplementationOnce(({dismiss}) => {
            useEffect(() => {
                dismiss.onDismiss()
            }, [])
            return <div>Are you sure you want to delete this recipe type?</div>
        })

        renderWithRoutes({
            [`/recipetype/${expectedRecipeType.id}`]: () => <RecipeTypeDetails id={expectedRecipeType.id}
                                                                               recipeTypeService={recipeTypeServiceMock}/>,
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
