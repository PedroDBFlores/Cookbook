import React, {FormEvent} from "React"
import {useHistory} from "react-router-dom"
import Form from "react-bootstrap/Form"
import Button from "react-bootstrap/Button"
import {Formik} from "formik"
import * as yup from "yup"
import {createRecipeType} from "../../../services/recipe-type-service"

interface CreateRecipeFormData {
    name: string
}

const schema = yup.object({
    name: yup.string().required("Name is required")
})

const CreateRecipeType: React.FC<unknown> = () => {
    const history = useHistory()

    const onSubmit = (data: CreateRecipeFormData) => {
        createRecipeType({name: data.name})
            .then(recipeType => history.push(`/recipetype/${recipeType.id}`))

    }

    return <Formik
        initialValues={{name: ""}}
        validateOnBlur={true}
        onSubmit={onSubmit}
        validationSchema={schema}>
        {
            ({
                 values,
                 errors,
                 handleChange,
                 handleSubmit
             }) => (
                <Form onSubmit={event => handleSubmit(event as FormEvent<HTMLFormElement>)}>
                    <Form.Group controlId="name">
                        <Form.Label>Name</Form.Label>
                        <Form.Control name="name" type="text"
                                      value={values.name}
                                      onChange={handleChange}
                                      isInvalid={!!errors.name}
                        />
                        <Form.Control.Feedback type="invalid">
                            {errors.name}
                        </Form.Control.Feedback>
                    </Form.Group>
                    <Button aria-label="Create recipe type" type="submit">Create</Button>
                </Form>
            )
        }
    </Formik>
}

export default CreateRecipeType
