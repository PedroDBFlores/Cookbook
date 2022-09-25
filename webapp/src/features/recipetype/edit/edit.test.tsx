import { render, screen } from "@testing-library/react"
import React from "react"
import EditRecipeType from "./edit"
import { WrapperWithRoutes, WrapWithCommonContexts } from "../../../../tests/render-helpers"
import createRecipeTypeService from "services/recipe-type-service"
import userEvent from "@testing-library/user-event"

jest.mock("services/recipe-type-service")
const createRecipeTypeServiceMock = createRecipeTypeService as jest.MockedFunction<typeof createRecipeTypeService>

jest.mock("components/recipe-type-form/recipe-type-form", () => ({
    __esModule: true,
    default: jest.fn().mockImplementation(({ onSubmit }) => <>
        <p>Edit recipe type form</p>
        <button aria-label="Edit recipe type"
            onClick={() => onSubmit({ id: 1, name: "Cake" })}>
            Edit
        </button>
    </>)
}))

describe("Edit recipe type", () => {
    const baseRecipeType = { id: 1, name: "Meat" }
    const findRecipeTypeMock = jest.fn()
    const updateRecipeTypeMock = jest.fn()

    createRecipeTypeServiceMock.mockImplementation(() => ({
        getAll: jest.fn(),
        update: updateRecipeTypeMock,
        find: findRecipeTypeMock,
        delete: jest.fn(),
        create: jest.fn()
    }))

    beforeEach(jest.clearAllMocks)

    it("renders the initial form", async () => {
        findRecipeTypeMock.mockResolvedValueOnce(baseRecipeType)

        render(<WrapWithCommonContexts>
            <WrapperWithRoutes initialPath="/recipetype/1/edit" routeConfiguration={[
                {
                    path: "/recipetype/:id/edit",
                    element: <EditRecipeType />
                },
            ]} />
        </WrapWithCommonContexts>)

        expect(await screen.findByText(/^translated recipe-type-feature.edit.title$/i)).toBeInTheDocument()
        expect(screen.getByText(/translated common.loading/i)).toBeInTheDocument()
        expect(findRecipeTypeMock).toHaveBeenCalledWith(1)
        expect(await screen.findByText(/edit recipe type form/i)).toBeInTheDocument()
    })

    it("renders an error if the recipe type cannot be obtained", async () => {
        findRecipeTypeMock.mockRejectedValueOnce(new Error("failure"))

        render(<WrapWithCommonContexts>
            <WrapperWithRoutes initialPath="/recipetype/1/edit" routeConfiguration={[
                {
                    path: "/recipetype/:id/edit",
                    element: <EditRecipeType />
                },
            ]} />
        </WrapWithCommonContexts>)

        expect(await screen.findByText(/^translated recipe-type-feature.errors.occurred-fetching$/i)).toBeInTheDocument()
        expect(await screen.findByText(/^failure$/i)).toBeInTheDocument()
        expect(await screen.findByText(/^translated recipe-type-feature.errors.cannot-load$/i)).toBeInTheDocument()
        expect(findRecipeTypeMock).toHaveBeenCalled()
    })

    it("updates the recipe type in the Cookbook API and navigates to the details", async () => {
        findRecipeTypeMock.mockResolvedValueOnce(baseRecipeType)
        updateRecipeTypeMock.mockResolvedValueOnce({})
        render(<WrapWithCommonContexts>
            <WrapperWithRoutes initialPath="/recipetype/1/edit" routeConfiguration={[
                {
                    path: "/recipetype/:id/edit",
                    element: <EditRecipeType />
                },
                {
                    path: "/recipetype/1",
                    element: <>I'm the recipe type details page</>
                }
            ]} />
        </WrapWithCommonContexts>)
        expect(await screen.findByText(/^translated recipe-type-feature.edit.title$/i)).toBeInTheDocument()

        await userEvent.click(await screen.findByLabelText(/^edit recipe type$/i))

        expect(await screen.findByText(`translated recipe-type-feature.update.success #Cake#`)).toBeInTheDocument()
        expect(await screen.findByText(/^I'm the recipe type details page$/i)).toBeInTheDocument()
        expect(updateRecipeTypeMock).toHaveBeenCalledWith({ ...baseRecipeType, name: "Cake" })
    })

    fit("shows an error message if the update API call fails", async () => {
        findRecipeTypeMock.mockResolvedValueOnce(baseRecipeType)
        updateRecipeTypeMock.mockRejectedValueOnce({ message: "Duplicate recipe type" })
        render(<WrapWithCommonContexts>
            <WrapperWithRoutes initialPath="/recipetype/1/edit" routeConfiguration={[
                {
                    path: "/recipetype/:id/edit",
                    element: <EditRecipeType />
                },
            ]} />
        </WrapWithCommonContexts>)

        expect(await screen.findByText(/^translated recipe-type-feature.edit.title$/i)).toBeInTheDocument()

        await userEvent.click(await screen.findByLabelText(/edit recipe type/i))

        expect(updateRecipeTypeMock).toHaveBeenCalledWith({ ...baseRecipeType, name: "Cake" })
        expect(await screen.findByText(`translated recipe-type-feature.update.failure`)).toBeInTheDocument()
        expect(await screen.findByText(/^duplicate recipe type$/i)).toBeInTheDocument()
    })
})
