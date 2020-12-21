import React, {useContext, useEffect, useRef, useState} from "react"
import {Form, Formik} from "formik"
import * as yup from "yup"
import createRecipeTypeService, {RecipeType} from "../../../services/recipe-type-service"
import createRecipeService from "../../../services/recipe-service"
import {useHistory} from "react-router-dom"
import {ApiHandlerContext} from "../../../services/api-handler"
import {ButtonGroup, Grid, GridItem, Heading, useToast} from "@chakra-ui/react"
import {InputControl, ResetButton, SelectControl, SubmitButton} from "formik-chakra-ui"
import {IfFulfilled, IfPending, useAsync} from "react-async"
import Loader from "../../../components/loader/loader"

interface CreateRecipeFormData {
    name: string
    description: string
    recipeTypeId: number
    ingredients: string
    preparingSteps: string
}

const schema = yup.object({
    name: yup.string()
        .required("Name is required")
        .min(1, "Name is required")
        .max(128, "Name exceeds the character limit"),
    description: yup.string()
        .required("Description is required")
        .min(1, "Description is required")
        .max(256, "Description exceeds the character limit"),
    recipeTypeId: yup.number()
        .required("Recipe type is required")
        .min(1, "Recipe type is required"),
    ingredients: yup.string()
        .required("Ingredients is required")
        .min(1, "Ingredients is required")
        .max(2048, "Ingredients exceeds the character limit"),
    preparingSteps: yup.string()
        .required("Preparing steps is required")
        .min(1, "Preparing steps is required")
        .max(4096, "Preparing steps exceeds the character limit"),
})

const CreateRecipe: React.FC = () => {
    const [recipeTypes, setRecipeTypes] = useState<Array<RecipeType>>()
    const history = useHistory()
    const toast = useToast()

    const {create} = createRecipeService(useContext(ApiHandlerContext))
    const {getAll: getAllRecipeTypes} = createRecipeTypeService(useContext(ApiHandlerContext))
    const getAllRecipeTypesFn = useRef(() => getAllRecipeTypes())
    const state = useAsync<RecipeType[]>({
        promiseFn: getAllRecipeTypesFn.current
    })

    useEffect(() => {
        getAllRecipeTypes().then(setRecipeTypes)
    }, [])

    const handleOnSubmit = async (data: CreateRecipeFormData) => {
        try {
            const {id} = await create({
                name: data.name,
                description: data.description,
                recipeTypeId: Number(data.recipeTypeId),
                ingredients: data.ingredients,
                preparingSteps: data.preparingSteps
            })
            toast({title: `Recipe '${data.name}' created successfully!`, status: "success"})
            history.push(`/recipe/${id}/details`)
        } catch ({message}) {
            toast({title: `An error occurred while creating the recipe: ${message}`, status: "error"})
        }
    }

    return <Grid width={"100%"} templateColumns="repeat(12, 1fr)" gap={6}>
        <GridItem colSpan={12}>
            <Heading>Create a new recipe</Heading>
        </GridItem>
        <GridItem colSpan={12}>
            <IfPending state={state}>
                <Loader/>
            </IfPending>
            <IfFulfilled state={state}>
                <Formik
                    initialValues={{
                        name: "",
                        description: "",
                        recipeTypeId: 0,
                        ingredients: "",
                        preparingSteps: ""
                    }}
                    validateOnBlur={true}
                    onSubmit={handleOnSubmit}
                    validationSchema={schema}>
                    <Form>
                        <Grid templateColumns="repeat(12, 1fr)" gap={6}>
                            <GridItem colSpan={6}>
                                <InputControl name={"name"} label={"Name"}/>
                            </GridItem>
                            <GridItem colSpan={6}>
                                <InputControl name={"description"} label={"Description"}/>
                            </GridItem>
                            <GridItem colSpan={6}>
                                <SelectControl aria-label="Recipe type parameter"
                                               name={"recipeTypeId"}
                                               label={"Recipe type"}
                                               selectProps={{placeholder: " "}}>
                                    {
                                        recipeTypes?.map(({id, name}) => (
                                            <option key={`recipeType-${id}`} value={id}>
                                                {name}
                                            </option>)
                                        )
                                    }
                                </SelectControl>
                            </GridItem>
                            <GridItem colSpan={6}>
                                <InputControl name={"ingredients"} label={"Ingredients"}/>
                            </GridItem>
                            <GridItem colSpan={6}>
                                <InputControl name={"preparingSteps"} label={"Preparing steps"}/>
                            </GridItem>
                            <GridItem colSpan={12}>
                                <ButtonGroup>
                                    <SubmitButton aria-label="Create recipe">Create</SubmitButton>
                                    <ResetButton aria-label="Reset form">Reset</ResetButton>
                                </ButtonGroup>
                            </GridItem>
                        </Grid>
                    </Form>
                </Formik>
            </IfFulfilled>
        </GridItem>
    </Grid>
}

export default CreateRecipe
