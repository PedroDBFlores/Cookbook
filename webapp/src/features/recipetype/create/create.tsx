import React, {useContext} from "react"
import {useHistory} from "react-router-dom"
import {Form, Formik} from "formik"
import * as yup from "yup"
import createRecipeTypeService from "../../../services/recipe-type-service"
import {ApiHandlerContext} from "../../../services/api-handler"
import {Grid, GridItem, Heading, useToast} from "@chakra-ui/react"
import {InputControl, SubmitButton, ResetButton} from "formik-chakra-ui"

interface CreateRecipeTypeFormData {
    name: string
}

const schema = yup.object({
    name: yup.string()
        .required("Name is required")
        .min(1, "Name is required")
        .max(64, "Name exceeds the character limit")
})

const CreateRecipeType: React.FC = () => {
    const {create} = createRecipeTypeService(useContext(ApiHandlerContext))
    const toast = useToast()
    const history = useHistory()

    const handleOnSubmit = async ({name}: CreateRecipeTypeFormData) => {
        try {
            const {id} = await create({name})
            toast({title: `Recipe type '${name}' created successfully!`, status: "success"})
            history.push(`/recipetype/${id}/details`)
        } catch ({message}) {
            toast({title: `An error occurred while creating the recipe type: ${message}`, status: "error"})
        }
    }

    return <>
        <Heading>Create a new recipe type</Heading>
        <Formik
            initialValues={{name: ""}}
            validateOnBlur={true}
            onSubmit={handleOnSubmit}
            validationSchema={schema}>
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
