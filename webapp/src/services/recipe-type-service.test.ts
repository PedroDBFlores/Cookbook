import axios from "axios"
import {generateRecipeType} from "../../tests/helpers/generators/dto-generators"
import MockAdapter from "axios-mock-adapter"
import * as errorHandler from "../utils/error-handling"
import {name, random} from "faker"
import createRecipeTypeService from "./recipe-type-service"
import ApiHandler from "./api-handler"

const mockedAxios = new MockAdapter(axios)
const handleErrorsSpy = jest.spyOn(errorHandler, "default")
const service = createRecipeTypeService(ApiHandler("http://localhost"))

describe("Recipe type service", () => {
    beforeEach(() => {
        localStorage.setItem("token", "A_TOKEN")
        mockedAxios.reset()
        handleErrorsSpy.mockClear()
    })

    describe("Find recipe type", () => {
        it("finds a recipe type by id", async () => {
            const expectedRecipeType = generateRecipeType()
            mockedAxios.onGet(`/recipetype/${expectedRecipeType.id}`)
                .replyOnce(200, expectedRecipeType)

            const response = await service.find(expectedRecipeType.id)

            expect(mockedAxios.history.get.length).toBe(1)
            expect(mockedAxios.history.get[0].url).toBe(`/recipetype/${expectedRecipeType.id}`)
            expect(response).toStrictEqual(expectedRecipeType)
        })

        it("calls the error handler", async () => {
            mockedAxios.onGet("/recipetype/1")
                .replyOnce(404, {
                    code: "NOT_FOUND",
                    message: "Entity not found"
                })

            await service.find(1).catch(() => {
                expect(handleErrorsSpy).toHaveBeenCalled()
            })
        })
    })

    describe("Get all", () => {
        it("gets all the recipe types", async () => {
            const expectedRecipeTypes = [generateRecipeType(), generateRecipeType()]
            mockedAxios.onGet("/recipetype")
                .replyOnce(200, expectedRecipeTypes)

            const response = await service.getAll()

            expect(mockedAxios.history.get.length).toBe(1)
            expect(mockedAxios.history.get[0].url).toBe("/recipetype")
            expect(response).toStrictEqual(expectedRecipeTypes)
        })

        it("calls the error handler", async () => {
            mockedAxios.onGet("/recipetype")
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
            const recipeTypeToCreate = {name: name.lastName()}
            const expectedResponse = {id: random.number()}
            mockedAxios.onPost("/recipetype", recipeTypeToCreate)
                .replyOnce(201, expectedResponse)

            const result = await service.create(recipeTypeToCreate)

            expect(mockedAxios.history.post.length).toBe(1)
            expect(mockedAxios.history.post[0].url).toBe("/recipetype")
            expect(result).toStrictEqual(expectedResponse)
        })

        it("calls the error handler", async () => {
            mockedAxios.onPost("/recipetype")
                .replyOnce(500, {
                    code: "INTERNAL_SERVER_ERROR",
                    message: "Database Error"
                })

            await service.create({name: ""}).catch(() => {
                expect(handleErrorsSpy).toHaveBeenCalled()
            })
        })
    })

    describe("Update", () => {
        it("updates a recipe type", async () => {
            const recipeTypeToUpdate = generateRecipeType()
            mockedAxios.onPut("/recipetype", recipeTypeToUpdate)
                .replyOnce(200)

            await service.update(recipeTypeToUpdate)

            expect(mockedAxios.history.put.length).toBe(1)
            expect(mockedAxios.history.put[0].url).toBe("/recipetype")

        })

        it("calls the error handler", async () => {
            mockedAxios.onPut("/recipetype")
                .replyOnce(500, {
                    code: "INTERNAL_SERVER_ERROR",
                    message: "Database Error"
                })

            await service.update({id: 123, name: ""}).catch(() => {
                expect(handleErrorsSpy).toHaveBeenCalled()
            })
        })
    })

    describe("Delete", () => {
        it("deletes a recipe type", async () => {
            const idToDelete = random.number()
            mockedAxios.onDelete(`/recipetype/${idToDelete}`)
                .replyOnce(204)

            await service.delete(idToDelete)

            expect(mockedAxios.history.delete.length).toBe(1)
            expect(mockedAxios.history.delete[0].url).toBe(`/recipetype/${idToDelete}`)

        })

        it("calls the error handler", async () => {
            mockedAxios.onDelete("/recipetype/1")
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
