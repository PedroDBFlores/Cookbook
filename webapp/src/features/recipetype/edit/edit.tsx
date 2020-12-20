import React, {useContext, useRef} from "react"
import PropTypes from "prop-types"
import * as yup from "yup"
import {IfFulfilled, IfPending, IfRejected, useAsync} from "react-async"
import {Form, Formik, FormikValues} from "formik"
import {useHistory} from "react-router-dom"
import createRecipeTypeService, {RecipeType} from "../../../services/recipe-type-service"
import {ApiHandlerContext} from "../../../services/api-handler"
import {Grid, GridItem, Heading, useToast} from "@chakra-ui/react"
import {InputControl, ResetButton, SubmitButton} from "formik-chakra-ui"
import Loader from "../../../components/loader/loader"

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
        <IfPending state={state}>
            <Loader />
        </IfPending>
        <IfRejected state={state}>
            {err => <span>Error: {err.message}</span>}
        </IfRejected>
        <IfFulfilled state={state}>
            {data => <>
                <Heading>Edit a recipe type</Heading>
                <Formik
                    initialValues={{...data}}
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
            }
        </IfFulfilled>
    </>
}
EditRecipeType.propTypes = {
    id: PropTypes.number.isRequired
}

export default EditRecipeType
