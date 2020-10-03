import React, {useEffect} from "react"
import {screen} from "@testing-library/react"
import RecipeTypeListPage from "../../../../src/features/recipetype/list/list-page"
import RecipeTypeList from "../../../../src/features/recipetype/list/list"
import {generateRecipeType} from "../../../helpers/generators/dto-generators"
import {renderWithRoutes, renderWrappedInCommonContexts} from "../../../render"
import createRecipeTypeService from "../../../../src/services/recipe-type-service"
import userEvent from "@testing-library/user-event"

jest.mock("../../../../src/services/recipe-type-service")
const createRecipeTypeServiceMock = createRecipeTypeService as jest.MockedFunction<typeof createRecipeTypeService>

jest.mock("../../../../src/features/recipetype/list/list", () => {
    return {
        __esModule: true,
        default: jest.fn(() => <>Mock Recipe Type list</>)
    }
})
const recipeTypeListMock = RecipeTypeList as jest.MockedFunction<typeof RecipeTypeList>

describe("Recipe type list page", () => {
    const getAllRecipeTypesMock = jest.fn()
    const deleteRecipeTypeMock = jest.fn()
    createRecipeTypeServiceMock.mockImplementation(() => {
        return {
            getAll: getAllRecipeTypesMock,
            update: jest.fn(),
            find: jest.fn(),
            delete: deleteRecipeTypeMock,
            create: jest.fn()
        }
    })

    beforeEach(() => jest.clearAllMocks())

    it("has the required content and gets the recipe types", async () => {
        const apiHandlerMock = jest.fn().mockReturnValue("My api handler")
        getAllRecipeTypesMock.mockResolvedValueOnce([])

        renderWrappedInCommonContexts(<RecipeTypeListPage/>, apiHandlerMock)

        expect(screen.getByText(/recipe types/i)).toBeInTheDocument()
        expect(screen.getByText(/loading.../i)).toBeInTheDocument()
        expect(createRecipeTypeServiceMock).toHaveBeenCalledWith(apiHandlerMock())
        expect(getAllRecipeTypesMock).toHaveBeenCalled()
        expect(await screen.findByLabelText("Create new recipe type")).toBeInTheDocument()
        expect(await screen.findByText(/^mock recipe type list$/i)).toBeInTheDocument()
    })

    it("shows the error", async () => {
        getAllRecipeTypesMock.mockRejectedValueOnce({code: "YELLOW", message: "Database error"})
        renderWrappedInCommonContexts(<RecipeTypeListPage/>)

        expect(screen.getByText(/loading.../i)).toBeInTheDocument()
        await screen.findByText(/error: database error/i)
        expect(getAllRecipeTypesMock).toHaveBeenCalled()
    })

    it("passed the onDelete function to the 'RecipeTypeList' component", async () => {
        const expectedRecipeType = generateRecipeType()
        getAllRecipeTypesMock.mockResolvedValueOnce([expectedRecipeType])
        deleteRecipeTypeMock.mockResolvedValueOnce(void (0))
        recipeTypeListMock.mockImplementationOnce(({recipeTypes, onDelete}) => {
            useEffect(() => {
                onDelete(recipeTypes[0].id)
            }, [])
            return <></>
        })

        renderWrappedInCommonContexts(<RecipeTypeListPage/>)

        expect(screen.getByText(/loading.../i)).toBeInTheDocument()
        expect(getAllRecipeTypesMock).toHaveBeenCalled()
        expect(await screen.findByText(`Recipe type ${expectedRecipeType.id} was deleted`)).toBeInTheDocument()
        expect(deleteRecipeTypeMock).toHaveBeenCalledWith(expectedRecipeType.id)
    })

    it("navigates to the recipe type create page on click", async () => {
        getAllRecipeTypesMock.mockResolvedValueOnce([])
        renderWithRoutes({
            "/recipetype": () => <RecipeTypeListPage/>,
            "/recipetype/new": () => <>I'm the recipe type create page</>
        }, "/recipetype")

        userEvent.click(await screen.findByLabelText(/create new recipe type/i))

        expect(await screen.findByText(/I'm the recipe type create page/i)).toBeInTheDocument()
    })
})
