import axios from "axios"
import {generateRecipe, generateRecipeDetails} from "../../tests/helpers/generators/dto-generators"
import MockAdapter from "axios-mock-adapter"
import * as errorHandler from "../utils/error-handling"
import createRecipeService, {RecipeDetails, SearchRecipeParameters} from "./recipe-service"
import ApiHandler from "./api-handler"
import {random} from "faker"
import {SearchResult} from "../model"

const mockedAxios = new MockAdapter(axios)
const handleErrorsSpy = jest.spyOn(errorHandler, "default")
const service = createRecipeService(ApiHandler("http://localhost"))

describe("Recipe service", () => {
    beforeAll(() => {
        localStorage.setItem("token", "A_TOKEN")
    })
    beforeEach(() => {
        mockedAxios.reset()
        handleErrorsSpy.mockClear()
    })

    describe("Find recipe", () => {
        it("finds a recipe by it's id", async () => {
            const expectedRecipe = generateRecipeDetails()
            mockedAxios.onGet(`/recipe/${expectedRecipe.id}`)
                .replyOnce(200, expectedRecipe)

            const recipe = await service.find(expectedRecipe.id)

            expect(mockedAxios.history.get.length).toBe(1)
            expect(mockedAxios.history.get[0].url).toBe(`/recipe/${expectedRecipe.id}`)
            expect(recipe).toStrictEqual(expectedRecipe)
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
                name: "Tikka Masala",
                pageNumber: 1,
                itemsPerPage: 10
            } as SearchRecipeParameters
            const expectedResponse = {
                count: 1,
                numberOfPages: 1,
                results: [generateRecipeDetails({name: "Tikka Masala"})]
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
                name: "Tikka Masala",
                pageNumber: 1,
                itemsPerPage: 10
            }).catch(() => {
                expect(handleErrorsSpy).toHaveBeenCalled()
            })
        })

    })

    describe("Get all recipes", () => {
        it("gets all the recipes", async () => {
            const expectedRecipes = [generateRecipeDetails(), generateRecipeDetails()]
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
            const recipeTypeToCreate = generateRecipe()
            const expectedResponse = {id: random.number()}
            mockedAxios.onPost("/recipe", recipeTypeToCreate)
                .replyOnce(201, expectedResponse)

            const result = await service.create(recipeTypeToCreate)

            expect(mockedAxios.history.post.length).toBe(1)
            expect(mockedAxios.history.post[0].url).toBe("/recipe")
            expect(result).toStrictEqual(expectedResponse)
        })

        it("calls the error handler", async () => {
            mockedAxios.onPost("/recipe")
                .replyOnce(500, {
                    code: "INTERNAL_SERVER_ERROR",
                    message: "Database Error"
                })

            await service.create(generateRecipe()).catch(() => {
                expect(handleErrorsSpy).toHaveBeenCalled()
            })
        })
    })

    describe("Update", () => {
        it("updates a recipe", async () => {
            const recipeToUpdate = generateRecipe()
            mockedAxios.onPut("/recipe", recipeToUpdate)
                .replyOnce(200)

            await service.update(recipeToUpdate)

            expect(mockedAxios.history.put.length).toBe(1)
            expect(mockedAxios.history.put[0].url).toBe("/recipe")

        })

        it("calls the error handler", async () => {
            mockedAxios.onPut("/recipe")
                .replyOnce(500, {
                    code: "INTERNAL_SERVER_ERROR",
                    message: "Database Error"
                })

            await service.update(generateRecipe()).catch(() => {
                expect(handleErrorsSpy).toHaveBeenCalled()
            })
        })
    })

    describe("Delete", () => {
        it("deletes a recipe", async () => {
            const idToDelete = random.number()
            mockedAxios.onDelete(`/recipe/${idToDelete}`)
                .replyOnce(204)

            await service.delete(idToDelete)

            expect(mockedAxios.history.delete.length).toBe(1)
            expect(mockedAxios.history.delete[0].url).toBe(`/recipe/${idToDelete}`)

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