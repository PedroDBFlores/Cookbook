import React, {useEffect} from "react"
import {fireEvent, render, screen, waitFor} from "@testing-library/react"
import RecipeSearchPage from "../../../../src/features/recipe/search/search-page"
import RecipeSearchList from "../../../../src/features/recipe/search/search-list"
import RecipeSearchForm from "../../../../src/features/recipe/search/search-form"
import {generateRecipeDetails} from "../../../helpers/generators/dto-generators"
import Button from "@material-ui/core/Button"
import {SearchResult} from "../../../../src/model"
import {RecipeDetails} from "../../../../src/services/recipe-service"
import {RecipeType} from "../../../../src/services/recipe-type-service"

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

describe("Search recipe page component", () => {
    beforeEach(() => {
        jest.clearAllMocks()
    })

    it("renders the initial component", async () => {
        render(<RecipeSearchPage
            getAllRecipeTypesFn={getAllRecipeTypesMock}
            searchFn={searchRecipesMock}/>)

        expect(screen.getByText(/search recipes/i)).toBeInTheDocument()
        expect(screen.getByText(/no matching recipes/i)).toBeInTheDocument()
        await waitFor(() => {
            expect(getAllRecipeTypesMock).toHaveBeenCalled()
            expect(screen.getByText("Die erste")).toBeInTheDocument()
        })
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

            render(<RecipeSearchPage
                getAllRecipeTypesFn={getAllRecipeTypesMock}
                searchFn={searchRecipesMock}/>)

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

        render(<RecipeSearchPage
            getAllRecipeTypesFn={getAllRecipeTypesMock}
            searchFn={searchRecipesMock}/>)

        await waitFor(() => {
            expect(searchRecipesMock).toHaveBeenNthCalledWith(1, {
                name: "One",
                description: "Two",
                recipeTypeId: 1,
                pageNumber: 0,
                itemsPerPage: 10
            })
        })

        fireEvent.click(screen.getByText(/change page/i))

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
})