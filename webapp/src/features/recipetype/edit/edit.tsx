import React, {useContext, useRef} from "react"
import PropTypes from "prop-types"
import * as yup from "yup"
import {useAsync} from "react-async"
import {Form, Formik, FormikValues} from "formik"
import {useHistory} from "react-router-dom"
import createRecipeTypeService, {RecipeType} from "../../../services/recipe-type-service"
import {ApiHandlerContext} from "../../../services/api-handler"
import {Choose, When} from "../../../components/flow-control/choose"
import {Grid, GridItem, Heading, useToast} from "@chakra-ui/react"
import {InputControl, ResetButton, SubmitButton} from "formik-chakra-ui"

const schema = yup.object({
    name: yup.string().required("Name is required")
        .min(1, "Name is required")
        .max(64, "Name exceeds the character limit")
})

const EditRecipeType: React.FC<{ id: number }> = ({id}) => {
    const toast = useToast()
    const history = useHistory()

    const {find, update} = createRecipeTypeService(useContext(ApiHandlerContext))
    const findPromiseRef = useRef(() => find(id))
    const state = useAsync<RecipeType>({
        promiseFn: findPromiseRef.current
    })

    const handleOnSubmit = async (values: FormikValues) => {
        try {
            await update({...values} as RecipeType)
            history.push(`/recipetype/${id}`)
        } catch ({message}) {
            toast({title: `An error occurred while updating the recipe type: ${message}`, status: "error"})
        }
    }

    return <>
        <Choose>
            <When condition={state.isPending}>
                <span>Loading...</span>
            </When>
            <When condition={state.isRejected}>
                <span>Error: {state.error?.message}</span>
            </When>
            <When condition={state.isFulfilled}>
                <>
                    <Heading as="h4">Edit a recipe type</Heading>
                    <Formik
                        initialValues={{...state.data}}
                        validateOnBlur={true}
                        onSubmit={handleOnSubmit}
                        validationSchema={schema}>
                        <Form>
                            <Grid templateColumns="repeat(12, 1fr)" gap={6}>
                                <GridItem colSpan={12}>
                                    <InputControl name={"name"} label={"Name"}/>
                                </GridItem>
                                <GridItem colSpan={12}>
                                    <SubmitButton aria-label="Edit recipe type"/>
                                    <ResetButton aria-label="Reset form"/>
                                </GridItem>
                            </Grid>
                        </Form>
                    </Formik>
                </>
            </When>
        </Choose>
    </>
}
EditRecipeType.propTypes = {
    id: PropTypes.number.isRequired
}

export default EditRecipeType
