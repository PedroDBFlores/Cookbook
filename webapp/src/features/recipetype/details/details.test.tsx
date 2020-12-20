import React, {useEffect} from "react"
import {render, screen} from "@testing-library/react"
import RecipeTypeDetails from "./details"
import {generateRecipeType} from "../../../../tests/helpers/generators/dto-generators"
import Modal from "../../../components/modal/modal"
import createRecipeTypeService from "../../../services/recipe-type-service"
import userEvent from "@testing-library/user-event"
import {WrapWithCommonContexts, WrapperWithRoutes} from "../../../../tests/render-helpers"

jest.mock("../../../../src/services/recipe-type-service")
const createRecipeTypeServiceMock = createRecipeTypeService as jest.MockedFunction<typeof createRecipeTypeService>

jest.mock("../../../../src/components/modal/modal", () => {
    return {
        __esModule: true,
        default: jest.fn().mockImplementation(() => <div>Delete RecipeType Modal</div>)
    }
})
const basicModalDialogMock = Modal as jest.MockedFunction<typeof Modal>

describe("Recipe type details", () => {
    const findRecipeTypeMock = jest.fn()
    const deleteRecipeTypeMock = jest.fn()

    createRecipeTypeServiceMock.mockImplementation(() => {
        return {
            getAll: jest.fn(),
            update: jest.fn(),
            find: findRecipeTypeMock,
            delete: deleteRecipeTypeMock,
            create: jest.fn()
        }
    })

    beforeEach(() => jest.clearAllMocks())

    it("renders the recipe type details", async () => {
        const expectedRecipeType = {...generateRecipeType(), id: 99}
        const apiHandlerMock = jest.fn().mockReturnValue("My api handler")
        findRecipeTypeMock.mockResolvedValueOnce(expectedRecipeType)

        render(<WrapWithCommonContexts apiHandler={apiHandlerMock}>
            <RecipeTypeDetails id={99}/>
        </WrapWithCommonContexts>)

        expect(screen.getByText(/loading.../i)).toBeInTheDocument()
        expect(createRecipeTypeServiceMock).toHaveBeenCalledWith(apiHandlerMock())
        expect(findRecipeTypeMock).toHaveBeenCalledWith(99)
        expect(await screen.findByText(/^recipe type details$/i)).toBeInTheDocument()
        expect(await screen.findByText(/^Id:$/i)).toBeInTheDocument()
        expect(await screen.findByText(/^Name:$/i)).toBeInTheDocument()
        expect(await screen.findByText(expectedRecipeType.id.toString())).toBeInTheDocument()
        expect(await screen.findByText(expectedRecipeType.name)).toBeInTheDocument()
    })

    it("renders an error if the recipe type cannot be obtained", async () => {
        findRecipeTypeMock.mockRejectedValueOnce({message: "Failure"})

        render(<WrapWithCommonContexts>
            <RecipeTypeDetails id={99}/>
        </WrapWithCommonContexts>)

        expect(await screen.findByText(/failure/i)).toBeInTheDocument()
        expect(findRecipeTypeMock).toHaveBeenCalled()
    })

    describe("Actions", () => {

        it("takes the user to the edit recipe type page", async () => {
            const expectedRecipeType = generateRecipeType()
            findRecipeTypeMock.mockResolvedValueOnce(expectedRecipeType)
            render(<WrapWithCommonContexts>
                <WrapperWithRoutes initialPath={`/recipetype/${expectedRecipeType.id}/details`} routeConfiguration={[
                    {
                        path: `/recipetype/${expectedRecipeType.id}/details`,
                        exact: true,
                        component: () => <RecipeTypeDetails id={expectedRecipeType.id}/>
                    },
                    {
                        path: `/recipetype/${expectedRecipeType.id}/edit`,
                        exact: true,
                        component: () => <div>I'm the recipe type edit page</div>
                    }
                ]}/>
            </WrapWithCommonContexts>)


            userEvent.click(await screen.findByLabelText(`Edit recipe type with id ${expectedRecipeType.id}`))

            expect(screen.getByText(/I'm the recipe type edit page/i)).toBeInTheDocument()
        })

        it("deletes the recipe type", async () => {
            const expectedRecipeType = generateRecipeType()
            findRecipeTypeMock.mockResolvedValueOnce(expectedRecipeType)
            deleteRecipeTypeMock.mockResolvedValueOnce({})
            basicModalDialogMock.mockImplementationOnce(({onAction}) => {
                useEffect(() => onAction(), [])
                return <div>Are you sure you want to delete this recipe type?</div>
            })

            render(<WrapWithCommonContexts>
                <WrapperWithRoutes initialPath={`/recipetype/${expectedRecipeType.id}/details`} routeConfiguration={[
                    {
                        path: `/recipetype/${expectedRecipeType.id}/details`,
                        exact: true,
                        component: () => <RecipeTypeDetails id={expectedRecipeType.id}/>
                    },
                    {
                        path: "/recipetype",
                        exact: true,
                        component: () => <div>I'm the recipe type list page</div>
                    }
                ]}/>
            </WrapWithCommonContexts>)

            userEvent.click(await screen.findByLabelText(`Delete recipe type with id ${expectedRecipeType.id}`))

            expect(screen.getByText(/are you sure you want to delete this recipe type?/i)).toBeInTheDocument()

            expect(deleteRecipeTypeMock).toHaveBeenCalledWith(expectedRecipeType.id)
            expect(await screen.findByText(`Recipe type ${expectedRecipeType.id} was deleted`)).toBeInTheDocument()
            expect(await screen.findByText(/I'm the recipe type list page/i)).toBeInTheDocument()
        })

        it("shows an error if deleting the recipe type fails", async () => {
            const expectedRecipeType = generateRecipeType()
            findRecipeTypeMock.mockResolvedValueOnce(expectedRecipeType)
            deleteRecipeTypeMock.mockRejectedValueOnce({message: "In use"})
            basicModalDialogMock.mockImplementationOnce(({onAction}) => {
                useEffect(() => onAction(), [])
                return <div>Are you sure you want to delete this recipe type?</div>
            })
            render(<WrapWithCommonContexts>
                <RecipeTypeDetails id={99}/>
            </WrapWithCommonContexts>)

            userEvent.click(await screen.findByLabelText(`Delete recipe type with id ${expectedRecipeType.id}`))

            expect(await screen.findByText(`An error occurred while trying to delete this recipe type: In use`)).toBeInTheDocument()
        })
    })
})
