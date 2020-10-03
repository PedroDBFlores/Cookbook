import {screen} from "@testing-library/react"
import React from "react"
import EditRecipeType from "../../../../src/features/recipetype/edit/edit"
import {generateRecipeType} from "../../../helpers/generators/dto-generators"
import {renderWithRoutes, renderWrappedInCommonContexts} from "../../../render"
import createRecipeTypeService from "../../../../src/services/recipe-type-service"
import userEvent from "@testing-library/user-event"

jest.mock("../../../../src/services/recipe-type-service")
const createRecipeTypeServiceMock = createRecipeTypeService as jest.MockedFunction<typeof createRecipeTypeService>

describe("Edit recipe type", () => {
    const findRecipeTypeMock = jest.fn()
    const updateRecipeTypeMock = jest.fn()
    createRecipeTypeServiceMock.mockImplementation(() => {
        return {
            getAll: jest.fn(),
            update: updateRecipeTypeMock,
            find: findRecipeTypeMock,
            delete: jest.fn(),
            create: jest.fn()
        }
    })

    beforeEach(() => jest.clearAllMocks())

    it("renders the initial form", async () => {
        const expectedRecipeType = generateRecipeType()
        const apiHandlerMock = jest.fn().mockReturnValue("My api handler")
        findRecipeTypeMock.mockResolvedValueOnce(expectedRecipeType)

        renderWrappedInCommonContexts(<EditRecipeType id={expectedRecipeType.id}/>,
            apiHandlerMock)

        expect(screen.getByText(/loading.../i)).toBeInTheDocument()
        expect(createRecipeTypeServiceMock).toHaveBeenCalledWith(apiHandlerMock())
        expect(findRecipeTypeMock).toHaveBeenCalledWith(expectedRecipeType.id)

        expect(await screen.findByText(/edit a recipe type/i)).toBeInTheDocument()
        expect(await screen.findByText(/^name$/i)).toBeInTheDocument()
        expect(await screen.findByLabelText(/^name$/i)).toHaveAttribute("value", expectedRecipeType.name)
        expect(screen.getByLabelText(/edit recipe type/i)).toHaveAttribute("type", "submit")
        expect(screen.getByLabelText(/reset form/i)).toHaveAttribute("type", "reset")
    })

    it("renders an error if the recipe type cannot be obtained", async () => {
        findRecipeTypeMock.mockRejectedValueOnce({message: "Failure"})

        renderWrappedInCommonContexts(<EditRecipeType id={99}/>)

        expect(screen.getByText(/loading.../i)).toBeInTheDocument()
        expect(await screen.findByText(/failure/i)).toBeInTheDocument()
        expect(findRecipeTypeMock).toHaveBeenCalled()
    })

    describe("Form validation", () => {
        it("displays an error when the name is empty on submitting", async () => {
            const expectedRecipeType = generateRecipeType()
            findRecipeTypeMock.mockResolvedValueOnce(expectedRecipeType)
            renderWrappedInCommonContexts(<EditRecipeType id={expectedRecipeType.id}/>)

            userEvent.clear(await screen.findByLabelText(/^name$/i))
            userEvent.click(screen.getByLabelText(/edit recipe type/i))

            expect(await screen.findByText(/name is required/i)).toBeInTheDocument()
        })

        it("displays an error when the name exceeds 64 characters", async () => {
            const expectedRecipeType = generateRecipeType()
            findRecipeTypeMock.mockResolvedValueOnce(expectedRecipeType)
            renderWrappedInCommonContexts(<EditRecipeType id={expectedRecipeType.id}/>)

            userEvent.paste(await screen.findByLabelText(/^name$/i), "a".repeat(65))
            userEvent.click(screen.getByLabelText(/edit recipe type/i))

            expect(await screen.findByText(/name exceeds the character limit/i)).toBeInTheDocument()
        })
    })

    it("updates the recipe type in the Cookbook API and navigates to the details", async () => {
        const expectedRecipeType = generateRecipeType()
        findRecipeTypeMock.mockResolvedValueOnce(expectedRecipeType)
        updateRecipeTypeMock.mockResolvedValueOnce({})
        renderWithRoutes({
            [`/recipetype/${expectedRecipeType.id}/edit`]: () => <EditRecipeType id={expectedRecipeType.id}/>,
            [`/recipetype/${expectedRecipeType.id}`]: () => <div>I'm the recipe type details page</div>
        }, `/recipetype/${expectedRecipeType.id}/edit`)

        userEvent.clear(await screen.findByLabelText(/^name$/i))
        await userEvent.type(screen.getByLabelText(/^name$/i), "Japanese")
        userEvent.click(screen.getByLabelText(/edit recipe type/i))

        expect(await screen.findByText("I'm the recipe type details page")).toBeInTheDocument()
        expect(updateRecipeTypeMock).toHaveBeenCalledWith({...expectedRecipeType, name: "Japanese"})
    })

    it("shows an error message if the update API call fails", async () => {
        const expectedRecipeType = generateRecipeType()
        findRecipeTypeMock.mockResolvedValueOnce(expectedRecipeType)
        updateRecipeTypeMock.mockRejectedValueOnce({message: "Duplicate recipe type"})
        renderWrappedInCommonContexts(<EditRecipeType id={expectedRecipeType.id}/>)
        expect(await screen.findByText(/edit a recipe type/i)).toBeInTheDocument()

        userEvent.clear(screen.getByLabelText(/^name$/i))
        await userEvent.type(screen.getByLabelText(/^name$/i), "Japanese")
        userEvent.click(screen.getByLabelText(/edit recipe type/i))

        expect(await screen.findByText(/an error occurred while updating the recipe type: duplicate recipe type/i)).toBeInTheDocument()
        expect(updateRecipeTypeMock).toHaveBeenCalledWith({...expectedRecipeType, name: "Japanese"})
    })
})
