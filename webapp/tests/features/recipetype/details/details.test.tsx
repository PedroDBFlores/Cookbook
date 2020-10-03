import React, {useEffect} from "react"
import {screen} from "@testing-library/react"
import RecipeTypeDetails from "../../../../src/features/recipetype/details/details"
import {generateRecipeType} from "../../../helpers/generators/dto-generators"
import BasicModalDialog from "../../../../src/components/modal/basic-modal-dialog"
import {renderWithRoutes, renderWrappedInCommonContexts} from "../../../render"
import createRecipeTypeService from "../../../../src/services/recipe-type-service"
import userEvent from "@testing-library/user-event"

jest.mock("../../../../src/services/recipe-type-service")
const createRecipeTypeServiceMock = createRecipeTypeService as jest.MockedFunction<typeof createRecipeTypeService>

jest.mock("../../../../src/components/modal/basic-modal-dialog", () => {
    return {
        __esModule: true,
        default: jest.fn().mockImplementation(() => <div>Delete RecipeType Modal</div>)
    }
})
const basicModalDialogMock = BasicModalDialog as jest.MockedFunction<typeof BasicModalDialog>

describe("Recipe type details", () => {
    const findRecipeTypeMock = jest.fn()
    const deleteRecipeTypeMock = jest.fn()

    createRecipeTypeServiceMock.mockImplementation(() => {
        return {
            getAll: jest.fn(),
            update: jest.fn(),
            find: findRecipeTypeMock,
            delete: deleteRecipeTypeMock,
            create: jest.fn()
        }
    })

    beforeEach(() => jest.clearAllMocks())

    it("renders the recipe type details", async () => {
        const expectedRecipeType = generateRecipeType()
        const apiHandlerMock = jest.fn().mockReturnValue("My api handler")
        findRecipeTypeMock.mockResolvedValueOnce(expectedRecipeType)

        renderWrappedInCommonContexts(<RecipeTypeDetails id={99}/>,
            apiHandlerMock)

        expect(screen.getByText(/loading.../i)).toBeInTheDocument()
        expect(createRecipeTypeServiceMock).toHaveBeenCalledWith(apiHandlerMock())
        expect(findRecipeTypeMock).toHaveBeenCalled()
        expect(await screen.findByText(/^recipe type details$/i)).toBeInTheDocument()
        expect(await screen.findByText(/^Id:$/i)).toBeInTheDocument()
        expect(await screen.findByText(/^Name:$/i)).toBeInTheDocument()
        expect(await screen.findByText(expectedRecipeType.id.toString())).toBeInTheDocument()
        expect(await screen.findByText(expectedRecipeType.name)).toBeInTheDocument()
    })

    it("renders an error if the recipe type cannot be obtained", async () => {
        findRecipeTypeMock.mockRejectedValueOnce({message: "Failure"})

        renderWrappedInCommonContexts(<RecipeTypeDetails id={99}/>)

        expect(screen.getByText(/loading.../i)).toBeInTheDocument()
        expect(await screen.findByText(/failure/i)).toBeInTheDocument()
        expect(findRecipeTypeMock).toHaveBeenCalled()
    })

    it("takes the user to the edit recipe type page", async () => {
        const expectedRecipeType = generateRecipeType()
        findRecipeTypeMock.mockResolvedValueOnce(expectedRecipeType)
        renderWithRoutes({
            [`/recipetype/${expectedRecipeType.id}/details`]: () => <RecipeTypeDetails id={expectedRecipeType.id}/>,
            [`/recipetype/${expectedRecipeType.id}/edit`]: () => <div>I'm the recipe type edit page</div>
        }, `/recipetype/${expectedRecipeType.id}/details`)

        expect(await screen.findByText(expectedRecipeType.name)).toBeInTheDocument()

        userEvent.click(screen.getByLabelText(`Edit recipe type with id ${expectedRecipeType.id}`))

        expect(screen.getByText(/I'm the recipe type edit page/i)).toBeInTheDocument()
    })

    it("deletes the recipe type", async () => {
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
            [`/recipetype/${expectedRecipeType.id}/details`]: () => <RecipeTypeDetails id={expectedRecipeType.id}/>,
            "/recipetype": () => <div>I'm the recipe type list page</div>
        }, `/recipetype/${expectedRecipeType.id}/details`)

        expect(await screen.findByText(/^Id:$/i)).toBeInTheDocument()
        expect(await screen.findByText(/^Name:$/i)).toBeInTheDocument()

        userEvent.click(screen.getByLabelText(`Delete recipe type with id ${expectedRecipeType.id}`))

        expect(basicModalDialogMock).toHaveBeenCalled()
        expect(screen.getByText(/are you sure you want to delete this recipe type?/i)).toBeInTheDocument()
        expect(deleteRecipeTypeMock).toHaveBeenCalledWith(expectedRecipeType.id)
        expect(await screen.findByText(`Recipe type ${expectedRecipeType.id} was deleted`)).toBeInTheDocument()
        expect(await screen.findByText(/I'm the recipe type list page/i)).toBeInTheDocument()
    })
})
