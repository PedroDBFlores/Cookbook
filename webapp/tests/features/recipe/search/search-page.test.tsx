import React, {useEffect} from "react"
import {screen, waitFor} from "@testing-library/react"
import RecipeSearchPage from "../../../../src/features/recipe/search/search-page"
import RecipeSearchForm from "../../../../src/features/recipe/search/search-form"
import {generateRecipeDetails} from "../../../helpers/generators/dto-generators"
import Button from "@material-ui/core/Button"
import {SearchResult} from "../../../../src/model"
import createRecipeService, {RecipeDetails} from "../../../../src/services/recipe-service"
import createRecipeTypeService, {RecipeType} from "../../../../src/services/recipe-type-service"
import {renderWithRoutes, renderWrappedInCommonContexts} from "../../../render"
import userEvent from "@testing-library/user-event"

jest.mock("../../../../src/services/recipe-type-service")
jest.mock("../../../../src/services/recipe-service")
const createRecipeTypeServiceMock = createRecipeTypeService as jest.MockedFunction<typeof createRecipeTypeService>
const createRecipeServiceMock = createRecipeService as jest.MockedFunction<typeof createRecipeService>

jest.mock("../../../../src/features/recipe/search/search-form", () => {
    return {
        __esModule: true,
        default: jest.fn().mockImplementation(({recipeTypes}: { recipeTypes: Array<RecipeType> }) =>
            <>
                Search Recipe Form
                {
                    recipeTypes.map(({id, name}) => <span key={id}>{name}</span>)
                }
            </>
        )
    }
})
const recipeSearchFormMock = RecipeSearchForm as jest.MockedFunction<typeof RecipeSearchForm>

jest.mock("../../../../src/features/recipe/search/search-list", () => {
    return {
        __esModule: true,
        default: jest.fn().mockImplementation(({searchResult, onPageChange}: {
                searchResult: SearchResult<RecipeDetails>
                onPageChange: (page: number) => void
            }) =>
                <>
                    Search Recipe List
                    {
                        searchResult.count ?
                            searchResult.results.map(({id, name}) => <span key={id}>{name}</span>) :
                            <span>No matching recipes</span>
                    }
                    <Button onClick={() => onPageChange(1)}>Change page</Button>
                </>
        )
    }
})

describe("Search recipe page component", () => {
    const getAllRecipeTypesMock = jest.fn().mockImplementation(() => Promise.resolve([
        {id: 1, name: "Die erste"},
        {id: 2, name: "Die zweite"}
    ]))
    const basicRecipes = [
        generateRecipeDetails({id: 1, recipeTypeId: 1}),
        generateRecipeDetails({id: 2, recipeTypeId: 1})
    ]
    const searchRecipesMock = jest.fn().mockImplementation(() =>
        Promise.resolve({
            count: 2,
            numberOfPages: 1,
            results: basicRecipes
        } as SearchResult<RecipeDetails>)
    )
    createRecipeTypeServiceMock.mockImplementation(() => ({
        getAll: getAllRecipeTypesMock,
        update: jest.fn(),
        find: jest.fn(),
        delete: jest.fn(),
        create: jest.fn()
    }))
    createRecipeServiceMock.mockImplementation(() => ({
        create: jest.fn(),
        search: searchRecipesMock,
        find: jest.fn(),
        delete: jest.fn(),
        update: jest.fn(),
        getAll: jest.fn()
    }))

    beforeEach(() => {
        jest.clearAllMocks()
    })

    it("renders the initial component", async () => {
        const apiHandlerMock = jest.fn().mockReturnValue("My api handler")

        renderWrappedInCommonContexts(<RecipeSearchPage/>, apiHandlerMock)

        expect(screen.getByText(/search recipes/i)).toBeInTheDocument()
        expect(screen.getByText(/no matching recipes/i)).toBeInTheDocument()
        expect(createRecipeTypeServiceMock).toHaveBeenCalledWith(apiHandlerMock())
        expect(createRecipeServiceMock).toHaveBeenCalledWith(apiHandlerMock())
        expect(getAllRecipeTypesMock).toHaveBeenCalled()
        expect(await screen.findByText("Die erste")).toBeInTheDocument()
    })

    test.each([
        ["with a valid recipeTypeId", 1, 1],
        ["with an unset recipeTypeId", 0, undefined]
    ])("calls the search function with the parameters from the form %s",
        async (_, recipeTypeId, expectedOnSearchRecipeTypeId) => {
            recipeSearchFormMock.mockImplementationOnce(({recipeTypes, onSearch}) => {
                useEffect(() => {
                    if (recipeTypes) {
                        onSearch({name: "One", description: "Two", recipeTypeId})
                    }
                }, [])

                return <></>
            })

            renderWrappedInCommonContexts(<RecipeSearchPage/>)

            await waitFor(() => {
                expect(searchRecipesMock).toHaveBeenCalledWith({
                    name: "One",
                    description: "Two",
                    recipeTypeId: expectedOnSearchRecipeTypeId,
                    pageNumber: 0,
                    itemsPerPage: 10
                })

                basicRecipes.forEach(recipeDetail => {
                    expect(screen.getByText(recipeDetail.name)).toBeInTheDocument()
                })
            })
        })

    it("calls the search function and persists the form info even if the pages change", async () => {
        recipeSearchFormMock.mockImplementationOnce(({recipeTypes, onSearch}) => {
            useEffect(() => {
                if (recipeTypes) {
                    onSearch({name: "One", description: "Two", recipeTypeId: 1})
                }
            }, [])

            return <></>
        })

        renderWrappedInCommonContexts(<RecipeSearchPage/>)

        await waitFor(() => {
            expect(searchRecipesMock).toHaveBeenNthCalledWith(1, {
                name: "One",
                description: "Two",
                recipeTypeId: 1,
                pageNumber: 0,
                itemsPerPage: 10
            })
        })

        userEvent.click(screen.getByText(/change page/i))

        await waitFor(() => {
            expect(searchRecipesMock).toHaveBeenNthCalledWith(2, {
                name: "One",
                description: "Two",
                recipeTypeId: 1,
                pageNumber: 1,
                itemsPerPage: 10
            })
        })
    })

    it("navigates to the recipe create page on click", async () => {
        getAllRecipeTypesMock.mockResolvedValueOnce([])
        renderWithRoutes({
            "/recipe": () => <RecipeSearchPage/>,
            "/recipe/new": () => <>I'm the recipe create page</>
        }, "/recipe")

        userEvent.click(await screen.findByLabelText(/create new recipe/i))

        expect(await screen.findByText(/I'm the recipe create page/i)).toBeInTheDocument()
    })
})