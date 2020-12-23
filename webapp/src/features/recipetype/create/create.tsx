import React, {useContext} from "react"
import {useHistory} from "react-router-dom"
import {Form, Formik} from "formik"
import createRecipeTypeService, {RecipeType} from "../../../services/recipe-type-service"
import {ApiHandlerContext} from "../../../services/api-handler"
import {Grid, GridItem, Heading, useToast} from "@chakra-ui/react"
import {InputControl, ResetButton, SubmitButton} from "formik-chakra-ui"
import RecipeTypeFormSchema from "../common/form-schema"

const CreateRecipeType: React.FC = () => {
    const {create} = createRecipeTypeService(useContext(ApiHandlerContext))
    const toast = useToast()
    const history = useHistory()

    const handleOnSubmit = async ({name}: Omit<RecipeType, "id">) => {
        try {
            const {id} = await create({name})
            toast({title: `Recipe type '${name}' created successfully!`, status: "success"})
            history.push(`/recipetype/${id}/details`)
        } catch ({message}) {
            toast({
                title: "An error occurred while creating the recipe type",
                description: message,
                status: "error",
                duration: null
            })
        }
    }

    return <>
        <Heading>Create a new recipe type</Heading>
        <Formik
            initialValues={{name: ""}}
            validateOnBlur={true}
            onSubmit={handleOnSubmit}
            validationSchema={RecipeTypeFormSchema}>
            <Form>
                <Grid templateColumns="repeat(12, 1fr)" gap={6}>
                    <GridItem colSpan={12}>
                        <InputControl name={"name"} label={"Name"}/>
                    </GridItem>
                    <GridItem colSpan={12}>
                        <SubmitButton aria-label="Create recipe type">Create</SubmitButton>
                        <ResetButton aria-label="Reset form">Reset</ResetButton>
                    </GridItem>
                </Grid>
            </Form>
        </Formik>
    </>
}

export default CreateRecipeType
