import React, {useEffect} from "react"
import {render, screen} from "@testing-library/react"
import RecipeTypeListPage from "./list-page"
import RecipeTypeList from "./list"
import {generateRecipeType} from "../../../../tests/helpers/generators/dto-generators"
import {WrapperWithRoutes, WrapWithCommonContexts} from "../../../../tests/render-helpers"
import createRecipeTypeService from "../../../services/recipe-type-service"
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

        render(<WrapWithCommonContexts apiHandler={apiHandlerMock}>
            <RecipeTypeListPage/>
        </WrapWithCommonContexts>)

        expect(screen.getByText(/recipe types/i)).toBeInTheDocument()
        expect(screen.getByText(/loading.../i)).toBeInTheDocument()
        expect(createRecipeTypeServiceMock).toHaveBeenCalledWith(apiHandlerMock())
        expect(getAllRecipeTypesMock).toHaveBeenCalled()
        expect(await screen.findByLabelText("Create new recipe type")).toBeInTheDocument()
        expect(await screen.findByText(/^mock recipe type list$/i)).toBeInTheDocument()
    })

    it("shows the error if fetching the recipe types fails", async () => {
        getAllRecipeTypesMock.mockRejectedValueOnce({code: "YELLOW", message: "Database error"})

        render(<WrapWithCommonContexts>
            <RecipeTypeListPage/>
        </WrapWithCommonContexts>)

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

        render(<WrapWithCommonContexts>
            <RecipeTypeListPage/>
        </WrapWithCommonContexts>)

        expect(getAllRecipeTypesMock).toHaveBeenCalled()
        expect(await screen.findByText(`Recipe type ${expectedRecipeType.id} was deleted`)).toBeInTheDocument()
        expect(deleteRecipeTypeMock).toHaveBeenCalledWith(expectedRecipeType.id)
    })

    it("navigates to the recipe type create page on click", async () => {
        getAllRecipeTypesMock.mockResolvedValueOnce([])
        render(<WrapWithCommonContexts>
            <WrapperWithRoutes initialPath="/recipetype" routeConfiguration={[
                {path: "/recipetype", exact: true, component: () => <RecipeTypeListPage/>},
                {path: "/recipetype/new", exact: true, component: () => <>I'm the recipe type create page</>}
            ]}/>
        </WrapWithCommonContexts>)

        userEvent.click(await screen.findByLabelText(/^create new recipe type$/i))

        expect(await screen.findByText(/I'm the recipe type create page/i)).toBeInTheDocument()
    })
})
