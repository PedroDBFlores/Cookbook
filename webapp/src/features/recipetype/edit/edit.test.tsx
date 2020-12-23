import {render, screen} from "@testing-library/react"
import React from "react"
import EditRecipeType from "./edit"
import {generateRecipeType} from "../../../../tests/helpers/generators/dto-generators"
import {WrapperWithRoutes, WrapWithCommonContexts} from "../../../../tests/render-helpers"
import createRecipeTypeService from "../../../services/recipe-type-service"
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

        render(<WrapWithCommonContexts apiHandler={apiHandlerMock}>
            <EditRecipeType id={expectedRecipeType.id}/>
        </WrapWithCommonContexts>)

        expect(screen.getByText(/loading.../i)).toBeInTheDocument()
        expect(createRecipeTypeServiceMock).toHaveBeenCalledWith(apiHandlerMock())
        expect(findRecipeTypeMock).toHaveBeenCalledWith(expectedRecipeType.id)
        expect(await screen.findByText(/edit a recipe type/i)).toBeInTheDocument()
        expect(await screen.findByLabelText(/^name$/i)).toHaveValue(expectedRecipeType.name)
        expect(screen.getByLabelText(/edit recipe type/i)).toHaveAttribute("type", "submit")
        expect(screen.getByLabelText(/reset form/i)).toHaveAttribute("type", "button")
    })

    it("renders an error if the recipe type cannot be obtained", async () => {
        findRecipeTypeMock.mockRejectedValueOnce(new Error("failure"))

        render(<WrapWithCommonContexts>
            <EditRecipeType id={99}/>
        </WrapWithCommonContexts>)

        expect(await screen.findByText(/^an error occurred while fetching the recipe type$/i)).toBeInTheDocument()
        expect(await screen.findByText(/^failure$/i)).toBeInTheDocument()
        expect(await screen.findByText(/^failed to fetch the recipe type$/i)).toBeInTheDocument()
        expect(findRecipeTypeMock).toHaveBeenCalled()
    })

    describe("Form validation", () => {
        it("displays an error when the name is empty on submitting", async () => {
            const expectedRecipeType = generateRecipeType()
            findRecipeTypeMock.mockResolvedValueOnce(expectedRecipeType)
            render(<WrapWithCommonContexts>
                <EditRecipeType id={expectedRecipeType.id}/>
            </WrapWithCommonContexts>)

            userEvent.clear(await screen.findByLabelText(/^name$/i))
            userEvent.click(screen.getByLabelText(/^edit recipe type$/i))

            expect(await screen.findByText(/^name is required$/i)).toBeInTheDocument()
        })

        it("displays an error when the name exceeds 64 characters", async () => {
            const expectedRecipeType = generateRecipeType()
            findRecipeTypeMock.mockResolvedValueOnce(expectedRecipeType)
            render(<WrapWithCommonContexts>
                <EditRecipeType id={expectedRecipeType.id}/>
            </WrapWithCommonContexts>)

            userEvent.paste(await screen.findByLabelText(/^name$/i), "a".repeat(65))
            userEvent.click(screen.getByLabelText(/^edit recipe type$/i))

            expect(await screen.findByText(/^name exceeds the character limit$/i)).toBeInTheDocument()
        })
    })

    it("updates the recipe type in the Cookbook API and navigates to the details", async () => {
        const expectedRecipeType = generateRecipeType()
        findRecipeTypeMock.mockResolvedValueOnce(expectedRecipeType)
        updateRecipeTypeMock.mockResolvedValueOnce({})
        render(<WrapWithCommonContexts>
            <WrapperWithRoutes initialPath={`/recipetype/${expectedRecipeType.id}/edit`} routeConfiguration={[
                {
                    path: `/recipetype/${expectedRecipeType.id}/edit`,
                    exact: true,
                    component: () => <EditRecipeType id={expectedRecipeType.id}/>
                },
                {
                    path: `/recipetype/${expectedRecipeType.id}`,
                    exact: true,
                    component: () => <>I'm the recipe type details page</>
                }
            ]}/>
            <EditRecipeType id={expectedRecipeType.id}/>
        </WrapWithCommonContexts>)

        userEvent.clear(await screen.findByLabelText(/^name$/i))
        await userEvent.type(screen.getByLabelText(/^name$/i), "Japanese")
        userEvent.click(screen.getByLabelText(/^edit recipe type$/i))

        expect(await screen.findByText(/^recipe type 'Japanese' updated successfully!$/i)).toBeInTheDocument()
        expect(await screen.findByText(/^I'm the recipe type details page$/i)).toBeInTheDocument()
        expect(updateRecipeTypeMock).toHaveBeenCalledWith({...expectedRecipeType, name: "Japanese"})
    })

    it("shows an error message if the update API call fails", async () => {
        const expectedRecipeType = generateRecipeType()
        findRecipeTypeMock.mockResolvedValueOnce(expectedRecipeType)
        updateRecipeTypeMock.mockRejectedValueOnce({message: "Duplicate recipe type"})
        render(<WrapWithCommonContexts>
            <EditRecipeType id={expectedRecipeType.id}/>
        </WrapWithCommonContexts>)
        expect(await screen.findByText(/^edit a recipe type$/i)).toBeInTheDocument()

        userEvent.clear(screen.getByLabelText(/^name$/i))
        await userEvent.type(screen.getByLabelText(/^name$/i), "Japanese")
        userEvent.click(screen.getByLabelText(/edit recipe type/i))

        expect(await screen.findByText(/^an error occurred while updating the recipe type$/i)).toBeInTheDocument()
        expect(await screen.findByText(/^duplicate recipe type$/i)).toBeInTheDocument()
        expect(updateRecipeTypeMock).toHaveBeenCalledWith({...expectedRecipeType, name: "Japanese"})
    })
})
