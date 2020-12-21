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
        getAllRecipeTypesMock.mockRejectedValueOnce(new Error("Database error"))

        render(<WrapWithCommonContexts>
            <RecipeTypeListPage/>
        </WrapWithCommonContexts>)

        expect(screen.getByText(/loading.../i)).toBeInTheDocument()
        await screen.findByText(/error: database error/i)
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
            const expectedRecipeType = generateRecipeType()
            getAllRecipeTypesMock.mockResolvedValueOnce([expectedRecipeType])
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
            expect(await screen.findByText(`Recipe type '${expectedRecipeType.name}' was deleted`)).toBeInTheDocument()
            expect(deleteRecipeTypeMock).toHaveBeenCalledWith(expectedRecipeType.id)
            expect(getAllRecipeTypesMock).toHaveBeenCalled()
            expect(screen.queryByText(expectedRecipeType.name)).not.toBeInTheDocument()
        })

        it("shows an error message if deleting the recipe type fails", async () => {
            const expectedRecipeType = generateRecipeType()
            getAllRecipeTypesMock.mockResolvedValueOnce([expectedRecipeType])
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

            expect(await screen.findByText(`An error occurred while trying to delete recipe type '${expectedRecipeType.name}': In use`)).toBeInTheDocument()
        })
    })
})
