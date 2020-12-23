import React, {useContext, useEffect, useRef, useState} from "react"
import {Form, Formik} from "formik"
import createRecipeTypeService, {RecipeType} from "../../../services/recipe-type-service"
import createRecipeService, {Recipe} from "../../../services/recipe-service"
import {useHistory} from "react-router-dom"
import {ApiHandlerContext} from "../../../services/api-handler"
import {ButtonGroup, Grid, GridItem, Heading, useToast} from "@chakra-ui/react"
import {InputControl, ResetButton, SelectControl, SubmitButton, TextareaControl} from "formik-chakra-ui"
import {IfFulfilled, IfPending, useAsync} from "react-async"
import Loader from "../../../components/loader/loader"
import RecipeFormSchema from "../common/form-schema"

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

    const handleOnSubmit = async (formData: Omit<Recipe, "id">) => {
        try {
            const {id} = await create({...formData, recipeTypeId: Number(formData.recipeTypeId)})
            toast({title: `Recipe '${formData.name}' created successfully!`, status: "success"})
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
                    validationSchema={RecipeFormSchema}>
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
                                <TextareaControl variant={"textarea"} name={"ingredients"} label={"Ingredients"}/>
                            </GridItem>
                            <GridItem colSpan={6}>
                                <TextareaControl name={"preparingSteps"} label={"Preparing steps"}/>
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
