import React, {useContext, useRef} from "react"
import PropTypes from "prop-types"
import {IfFulfilled, IfPending, IfRejected, useAsync} from "react-async"
import {Form, Formik, FormikValues} from "formik"
import {useHistory} from "react-router-dom"
import createRecipeTypeService, {RecipeType} from "../../../services/recipe-type-service"
import {ApiHandlerContext} from "../../../services/api-handler"
import {ButtonGroup, Grid, GridItem, Heading, Text, useToast} from "@chakra-ui/react"
import {InputControl, ResetButton, SubmitButton} from "formik-chakra-ui"
import Loader from "../../../components/loader/loader"
import RecipeTypeFormSchema from "../common/form-schema"

const EditRecipeType: React.FC<{ id: number }> = ({id}) => {
    const toast = useToast()
    const history = useHistory()

    const {find, update} = createRecipeTypeService(useContext(ApiHandlerContext))
    const findPromiseRef = useRef(() => find(id))
    const state = useAsync<RecipeType>({
        promiseFn: findPromiseRef.current,
        onReject: ({message}) => toast({
            title: "An error occurred while fetching the recipe type",
            description: message,
            status: "error",
            duration: null
        })
    })

    const handleOnSubmit = async (values: FormikValues) => {
        try {
            await update({...values} as RecipeType)
            toast({title: `Recipe type '${values.name}' updated successfully!`, status: "success"})
            history.push(`/recipetype/${id}`)
        } catch ({message}) {
            toast({
                title: "An error occurred while updating the recipe type",
                description: message,
                status: "error",
                duration: null
            })
        }
    }

    return <>
        <IfPending state={state}>
            <Loader/>
        </IfPending>
        <IfRejected state={state}>
            <Text>Failed to fetch the recipe type</Text>
        </IfRejected>
        <IfFulfilled state={state}>
            {data => <>
                <Heading>Edit a recipe type</Heading>
                <Formik
                    initialValues={data}
                    validateOnBlur={true}
                    onSubmit={handleOnSubmit}
                    validationSchema={RecipeTypeFormSchema}>
                    <Form>
                        <Grid templateColumns="repeat(12, 1fr)" gap={6}>
                            <GridItem colSpan={12}>
                                <InputControl name={"name"} label={"Name"}/>
                            </GridItem>
                            <GridItem colSpan={12}>
                                <ButtonGroup>
                                    <SubmitButton aria-label="Edit recipe type">Edit</SubmitButton>
                                    <ResetButton aria-label="Reset form">Reset</ResetButton>
                                </ButtonGroup>
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
