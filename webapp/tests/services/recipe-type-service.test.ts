import axios from "axios"
import { generateRecipeType } from "../helpers/generators/dto-generators"
import * as RecipeTypeService from "../../src/services/recipe-type-service"
import MockAdapter from "axios-mock-adapter"
import * as errorHandler from "../../src/utils/error-handling"
import * as faker from "faker"

const mockedAxios = new MockAdapter(axios)
const handleErrorsSpy = jest.spyOn(errorHandler, "default")

describe("Recipe type service", () => {
    beforeEach(() => {
        mockedAxios.reset()
    })

    describe("Find recipe type", () => {
        it("finds a recipe type by id", async () => {
            const expectedRecipeType = generateRecipeType()
            mockedAxios.onGet(`/api/recipetype/${expectedRecipeType.id}`)
                .replyOnce(200, expectedRecipeType)

            const response = await RecipeTypeService.findRecipeType(expectedRecipeType.id)
            expect(mockedAxios.history.get.length).toBe(1)
            expect(response).toStrictEqual(expectedRecipeType)
        })

        it("calls the error handler", async () => {
            mockedAxios.onGet("/api/recipetype/1")
                .replyOnce(404, {
                    code: "NOT_FOUND",
                    message: "Entity not found"
                })

            const service = RecipeTypeService
            await service.findRecipeType(1).catch(() => {
                expect(handleErrorsSpy).toHaveBeenCalled()
            })
        })
    })

    describe("Get all", () => {
        it("gets all the recipe types", async () => {
            const expectedRecipeTypes = [generateRecipeType(), generateRecipeType()]
            mockedAxios.onGet("/api/recipetype")
                .reply(200, expectedRecipeTypes)

            const response = await RecipeTypeService.getAllRecipeTypes()
            expect(mockedAxios.history.get.length).toBe(1)
            expect(response).toStrictEqual(expectedRecipeTypes)
        })

        it("calls the error handler", async () => {
            mockedAxios.onGet("/api/recipetype")
                .reply(500, {
                    code: "INTERNAL_SERVER_ERROR",
                    message: "Database Error"
                })

            const service = RecipeTypeService
            await service.getAllRecipeTypes().catch(() => {
                expect(handleErrorsSpy).toHaveBeenCalled()
            })
        })
    })

    describe("Create", () => {
        it("creates a recipe", async () => {
            const recipeTypeToCreate = { name: faker.name.lastName() }
            const expectedResponse = { id: faker.random.number() }
            mockedAxios.onPost("/api/recipetype", recipeTypeToCreate)
                .replyOnce(201, expectedResponse)
            const service = RecipeTypeService

            const result = await service.createRecipeType(recipeTypeToCreate)

            expect(result).toStrictEqual(expectedResponse)
            expect(mockedAxios.history.post.length).toBe(1)
        })

        it("calls the error handler", async () => {
            mockedAxios.onPost("/api/recipetype")
                .reply(500, {
                    code: "INTERNAL_SERVER_ERROR",
                    message: "Database Error"
                })

            const service = RecipeTypeService
            await service.createRecipeType({ name: "" }).catch(() => {
                expect(handleErrorsSpy).toHaveBeenCalled()
            })
        })
    })

    describe("Update", () => {
        it("updates a recipe type", async () => {
            const recipeTypeToUpdate = generateRecipeType()
            mockedAxios.onPut("/api/recipetype", recipeTypeToUpdate)
                .replyOnce(200)
            const service = RecipeTypeService

            await service.updateRecipeType(recipeTypeToUpdate)

            expect(mockedAxios.history.put.length).toBe(1)
        })

        it("calls the error handler", async () => {
            mockedAxios.onPut("/api/recipetype")
                .reply(500, {
                    code: "INTERNAL_SERVER_ERROR",
                    message: "Database Error"
                })

            const service = RecipeTypeService
            await service.updateRecipeType({ id: 123, name: "" }).catch(() => {
                expect(handleErrorsSpy).toHaveBeenCalled()
            })
        })
    })

    describe("Delete", () => {
        it("deletes a recipe type", async () => {
            const idToDelete = faker.random.number()
            mockedAxios.onDelete(`/api/recipetype/${idToDelete}`)
                .replyOnce(204)
            const service = RecipeTypeService

            await service.deleteRecipeType(idToDelete)

            expect(mockedAxios.history.delete.length).toBe(1)
        })

        it("calls the error handler", async () => {
            mockedAxios.onDelete("/api/recipetype/1")
                .reply(500, {
                    code: "INTERNAL_SERVER_ERROR",
                    message: "Database Error"
                })

            const service = RecipeTypeService
            await service.deleteRecipeType(1).catch(() => {
                expect(handleErrorsSpy).toHaveBeenCalled()
            })
        })
    })
})
