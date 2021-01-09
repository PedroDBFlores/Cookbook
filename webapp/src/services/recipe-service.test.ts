import axios from "axios"
import MockAdapter from "axios-mock-adapter"
import * as errorHandler from "utils/error-handling"
import createRecipeService, {Recipe, RecipeDetails, SearchRecipeParameters} from "./recipe-service"
import ApiHandler from "./api-handler"
import {SearchResult} from "model"

const mockedAxios = new MockAdapter(axios)
const handleErrorsSpy = jest.spyOn(errorHandler, "default")
const service = createRecipeService(ApiHandler("http://localhost"))

describe("Recipe service", () => {
    const baseRecipe: Recipe = {
        id: 123,
        recipeTypeId: 456,
        name: "Roasted sweet potato",
        description: "Roasted sweet potato",
        ingredients: "Potato",
        preparingSteps: "Take potato to oven"
    }
    const baseRecipeTypeDetails: RecipeDetails = {
        ...baseRecipe,
        recipeTypeName: "Dessert"
    }

    beforeEach(() => {
        mockedAxios.reset()
        handleErrorsSpy.mockClear()
    })

    describe("Find recipe", () => {
        it("finds a recipe by it's id", async () => {
            mockedAxios.onGet(`/recipe/${baseRecipe.id}`)
                .replyOnce(200, baseRecipe)

            const recipe = await service.find(baseRecipe.id)

            expect(mockedAxios.history.get.length).toBe(1)
            expect(mockedAxios.history.get[0].url).toBe(`/recipe/${baseRecipe.id}`)
            expect(recipe).toStrictEqual(baseRecipe)
        })

        it("calls the error handler", async () => {
            mockedAxios.onGet("/recipe/1")
                .replyOnce(404, {
                    code: "NOT_FOUND",
                    message: "Entity not found"
                })

            await service.find(1).catch(() => {
                expect(handleErrorsSpy).toHaveBeenCalled()
            })
        })
    })

    describe("Search recipe", () => {
        it("searches recipes with the sent parameters", async () => {
            const searchParameters = {
                name: "Potato",
                pageNumber: 1,
                itemsPerPage: 10
            } as SearchRecipeParameters
            const expectedResponse = {
                count: 1,
                numberOfPages: 1,
                results: [baseRecipeTypeDetails]
            } as SearchResult<RecipeDetails>
            mockedAxios.onPost("/recipe/search", searchParameters)
                .replyOnce(200, expectedResponse)

            const result = await service.search(searchParameters)

            expect(mockedAxios.history.post.length).toBe(1)
            expect(mockedAxios.history.post[0].url).toBe("/recipe/search")
            expect(result).toStrictEqual(expectedResponse)
        })

        it("calls the error handler", async () => {
            mockedAxios.onPost("/recipe/search")
                .replyOnce(500, {
                    code: "INTERNAL_SERVER_ERROR",
                    message: "Database Error"
                })

            await service.search({
                name: "Potato",
                pageNumber: 1,
                itemsPerPage: 10
            }).catch(() => {
                expect(handleErrorsSpy).toHaveBeenCalled()
            })
        })

    })

    describe("Get all recipes", () => {
        it("gets all the recipes", async () => {
            const expectedRecipes = [baseRecipeTypeDetails, {
                ...baseRecipeTypeDetails,
                name: "Boiled Sweet Potato",
                description: "Boiled Sweet Potato"
            }]
            mockedAxios.onGet("/recipe")
                .replyOnce(200, expectedRecipes)

            const response = await service.getAll()

            expect(mockedAxios.history.get.length).toBe(1)
            expect(mockedAxios.history.get[0].url).toBe("/recipe")
            expect(response).toStrictEqual(expectedRecipes)
        })

        it("calls the error handler", async () => {
            mockedAxios.onGet("/recipe")
                .replyOnce(500, {
                    code: "INTERNAL_SERVER_ERROR",
                    message: "Database Error"
                })

            await service.getAll().catch(() => {
                expect(handleErrorsSpy).toHaveBeenCalled()
            })
        })
    })

    describe("Create", () => {
        it("creates a recipe", async () => {
            mockedAxios.onPost("/recipe", baseRecipe)
                .replyOnce(201, {id: 59})

            const result = await service.create(baseRecipe)

            expect(mockedAxios.history.post.length).toBe(1)
            expect(mockedAxios.history.post[0].url).toBe("/recipe")
            expect(result).toStrictEqual({id: 59})
        })

        it("calls the error handler", async () => {
            mockedAxios.onPost("/recipe")
                .replyOnce(500, {
                    code: "INTERNAL_SERVER_ERROR",
                    message: "Database Error"
                })

            await service.create(baseRecipe).catch(() => {
                expect(handleErrorsSpy).toHaveBeenCalled()
            })
        })
    })

    describe("Update", () => {
        it("updates a recipe", async () => {
            mockedAxios.onPut("/recipe", baseRecipe)
                .replyOnce(200)

            await service.update(baseRecipe)

            expect(mockedAxios.history.put.length).toBe(1)
            expect(mockedAxios.history.put[0].url).toBe("/recipe")

        })

        it("calls the error handler", async () => {
            mockedAxios.onPut("/recipe")
                .replyOnce(500, {
                    code: "INTERNAL_SERVER_ERROR",
                    message: "Database Error"
                })

            await service.update(baseRecipe).catch(() => {
                expect(handleErrorsSpy).toHaveBeenCalled()
            })
        })
    })

    describe("Delete", () => {
        it("deletes a recipe", async () => {
            mockedAxios.onDelete("/recipe/549")
                .replyOnce(204)

            await service.delete(549)

            expect(mockedAxios.history.delete.length).toBe(1)
            expect(mockedAxios.history.delete[0].url).toBe("/recipe/549")

        })

        it("calls the error handler", async () => {
            mockedAxios.onDelete("/recipe/1")
                .replyOnce(500, {
                    code: "INTERNAL_SERVER_ERROR",
                    message: "Database Error"
                })

            await service.delete(1).catch(() => {
                expect(handleErrorsSpy).toHaveBeenCalled()
            })
        })
    })

})