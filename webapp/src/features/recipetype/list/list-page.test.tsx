import React, {useEffect} from "react"
import {render, screen} from "@testing-library/react"
import RecipeTypeListPage from "./list-page"
import RecipeTypeList from "./list"
import {WrapperWithRoutes, WrapWithCommonContexts} from "../../../../tests/render-helpers"
import createRecipeTypeService from "services/recipe-type-service"
import userEvent from "@testing-library/user-event"

jest.mock("services/recipe-type-service")
const createRecipeTypeServiceMock = createRecipeTypeService as jest.MockedFunction<typeof createRecipeTypeService>

jest.mock("features/recipetype/list/list", () => ({
    __esModule: true,
    default: jest.fn(() => <>Mock Recipe Type list</>)
}))
const recipeTypeListMock = RecipeTypeList as jest.MockedFunction<typeof RecipeTypeList>

describe("Recipe type list page", () => {
    const baseRecipeType = {id: 123, name: "Meat"}

    const getAllRecipeTypesMock = jest.fn()
    const deleteRecipeTypeMock = jest.fn()

    createRecipeTypeServiceMock.mockImplementation(() => ({
        getAll: getAllRecipeTypesMock,
        update: jest.fn(),
        find: jest.fn(),
        delete: deleteRecipeTypeMock,
        create: jest.fn()
    }))

    beforeEach(jest.clearAllMocks)

    it("has the required content and gets the recipe types", async () => {
        getAllRecipeTypesMock.mockResolvedValueOnce([])

        render(<WrapWithCommonContexts>
            <RecipeTypeListPage/>
        </WrapWithCommonContexts>)

        expect(screen.getByText(/recipe types/i)).toBeInTheDocument()
        expect(screen.getByText(/loading.../i)).toBeInTheDocument()
        expect(createRecipeTypeServiceMock).toHaveBeenCalledWith()
        expect(getAllRecipeTypesMock).toHaveBeenCalled()
        expect(await screen.findByLabelText("Create new recipe type")).toBeInTheDocument()
        expect(await screen.findByText(/^mock recipe type list$/i)).toBeInTheDocument()
    })

    it("shows the error if fetching the recipe types fails", async () => {
        getAllRecipeTypesMock.mockRejectedValueOnce(new Error("Database error"))

        render(<WrapWithCommonContexts>
            <RecipeTypeListPage/>
        </WrapWithCommonContexts>)

        expect(await screen.findByText(/^an error occurred while fetching the recipe types$/i)).toBeInTheDocument()
        expect(await screen.findByText(/^failed to fetch the recipe types$/i)).toBeInTheDocument()
        expect(getAllRecipeTypesMock).toHaveBeenCalled()
    })

    describe("Actions", () => {
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

        it("deletes a recipe type successfully", async () => {
            getAllRecipeTypesMock.mockResolvedValueOnce([baseRecipeType])
                .mockResolvedValueOnce([])
            deleteRecipeTypeMock.mockResolvedValueOnce(void (0))
            recipeTypeListMock.mockImplementationOnce(({recipeTypes, onDelete}) => {
                useEffect(() => {
                    onDelete(recipeTypes[0].id, recipeTypes[0].name)
                }, [])
                return <>
                    {
                        recipeTypes.map(r => <span key={r.id}>{r.name}</span>)
                    }
                </>
            })

            render(<WrapWithCommonContexts>
                <RecipeTypeListPage/>
            </WrapWithCommonContexts>)

            expect(getAllRecipeTypesMock).toHaveBeenCalled()
            expect(await screen.findByText(`Recipe type '${baseRecipeType.name}' was deleted`)).toBeInTheDocument()
            expect(deleteRecipeTypeMock).toHaveBeenCalledWith(baseRecipeType.id)
            expect(getAllRecipeTypesMock).toHaveBeenCalled()
            expect(screen.queryByText(baseRecipeType.name)).not.toBeInTheDocument()
        })

        it("shows an error message if deleting the recipe type fails", async () => {
            getAllRecipeTypesMock.mockResolvedValueOnce([baseRecipeType])
            deleteRecipeTypeMock.mockRejectedValueOnce({message: "In use"})
            recipeTypeListMock.mockImplementationOnce(({recipeTypes, onDelete}) => {
                useEffect(() => {
                    onDelete(recipeTypes[0].id, recipeTypes[0].name)
                }, [])
                return <></>
            })

            render(<WrapWithCommonContexts>
                <RecipeTypeListPage/>
            </WrapWithCommonContexts>)

            expect(await screen.findByText(`An error occurred while trying to delete recipe type '${baseRecipeType.name}': In use`)).toBeInTheDocument()
        })
    })
})
