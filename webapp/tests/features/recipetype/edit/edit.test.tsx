import {fireEvent, screen, waitFor} from "@testing-library/react"
import React from "react"
import EditRecipeType from "../../../../src/features/recipetype/edit/edit"
import {generateRecipeType} from "../../../helpers/generators/dto-generators"
import {renderWithRoutes, renderWrappedInCommonContexts} from "../../../render"
import createRecipeTypeService from "../../../../src/services/recipe-type-service"

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

        await waitFor(() => {
            expect(createRecipeTypeServiceMock).toHaveBeenCalledWith(apiHandlerMock())
            expect(findRecipeTypeMock).toHaveBeenCalledWith(expectedRecipeType.id)
            expect(screen.getByText(/edit a recipe type/i)).toBeInTheDocument()
            expect(screen.getByText(/name/i)).toBeInTheDocument()
            expect(screen.getByLabelText(/name/i)).toBeInTheDocument()
            expect(screen.getByLabelText(/name/i)).toHaveAttribute("value", expectedRecipeType.name)
            const submitButton = screen.getByLabelText(/edit recipe type/i)
            expect(submitButton).toHaveAttribute("type", "submit")
            const resetButton = screen.getByLabelText(/reset form/i)
            expect(resetButton).toHaveAttribute("type", "reset")
        })
    })

    it("renders an error if the recipe type cannot be obtained", async () => {
        findRecipeTypeMock.mockRejectedValueOnce({message: "Failure"})
        renderWrappedInCommonContexts(<EditRecipeType id={99}/>)

        expect(screen.getByText(/loading.../i)).toBeInTheDocument()

        await waitFor(() => {
            expect(findRecipeTypeMock).toHaveBeenCalled()
            expect(screen.getByText(/failure/i)).toBeInTheDocument()
        })
    })

    describe("Form validation", () => {
        it("displays an error when the name is empty on submitting", async () => {
            const expectedRecipeType = generateRecipeType()
            findRecipeTypeMock.mockResolvedValueOnce(expectedRecipeType)
            renderWrappedInCommonContexts(<EditRecipeType id={expectedRecipeType.id}/>)
            await waitFor(() => expect(screen.getByText(/edit a recipe type/i)).toBeInTheDocument())

            const nameInput = screen.getByLabelText(/name/i)
            fireEvent.change(nameInput, {target: {value: ""}})
            const submitButton = screen.getByLabelText(/edit recipe type/i)
            fireEvent.submit(submitButton)

            await waitFor(() =>
                expect(screen.getByText(/name is required/i)).toBeInTheDocument()
            )
        })

        it("displays an error when the name exceeds 64 characters", async () => {
            const expectedRecipeType = generateRecipeType()
            findRecipeTypeMock.mockResolvedValueOnce(expectedRecipeType)
            renderWrappedInCommonContexts(<EditRecipeType id={expectedRecipeType.id}/>)
            await waitFor(() => expect(screen.getByText(/edit a recipe type/i)).toBeInTheDocument())

            const nameInput = screen.getByLabelText(/name/i)
            fireEvent.change(nameInput, {target: {value: "a".repeat(65)}})
            const submitButton = screen.getByLabelText(/edit recipe type/i)
            fireEvent.submit(submitButton)

            await waitFor(() =>
                expect(screen.getByText(/name exceeds the character limit/i)).toBeInTheDocument()
            )
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
        await waitFor(() => expect(screen.getByText(/edit a recipe type/i)).toBeInTheDocument())

        const nameInput = screen.getByLabelText(/name/i)
        fireEvent.change(nameInput, {target: {value: "Japanese"}})
        const submitButton = screen.getByLabelText(/edit recipe type/i)
        fireEvent.submit(submitButton)

        await waitFor(() => {
            expect(updateRecipeTypeMock).toHaveBeenCalledWith({...expectedRecipeType, name: "Japanese"})
            expect(screen.getByText("I'm the recipe type details page")).toBeInTheDocument()
        })
    })

    it("shows an error message if the update API call fails", async () => {
        const expectedRecipeType = generateRecipeType()
        findRecipeTypeMock.mockResolvedValueOnce(expectedRecipeType)
        updateRecipeTypeMock.mockRejectedValueOnce({message: "Duplicate recipe type"})
        renderWrappedInCommonContexts(<EditRecipeType id={expectedRecipeType.id}/>)
        await waitFor(() => expect(screen.getByText(/edit a recipe type/i)).toBeInTheDocument())

        const nameInput = screen.getByLabelText(/name/i)
        fireEvent.change(nameInput, {target: {value: "Japanese"}})
        const submitButton = screen.getByLabelText(/edit recipe type/i)
        fireEvent.submit(submitButton)

        await waitFor(() => {
            expect(screen.getByText(/an error occurred while updating the recipe type: duplicate recipe type/i)).toBeInTheDocument()
        })
    })
})
