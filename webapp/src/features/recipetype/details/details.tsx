import React from "react"
import {RecipeType} from "../../../dto"
import PropTypes from "prop-types"
import {findRecipeType} from "../../../services/recipe-type-service"
import {Async} from "react-async"
import {ResponseError} from "../../../dto/response-error"
import Button from "react-bootstrap/Button"
import {faTrash} from "@fortawesome/free-solid-svg-icons"
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome"

interface RecipeTypeDetailsProps {
    id: number
    onDelete: (id: number) => void
}

const RecipeTypeDetails: React.FC<RecipeTypeDetailsProps> = ({id, onDelete}) => {
    return <>
        <Async promiseFn={() => findRecipeType(id)}>
            <Async.Loading>Loading...</Async.Loading>
            <Async.Rejected>
                {(error: ResponseError) =>
                    <span>Error: {error.message}</span>
                }
            </Async.Rejected>
            <Async.Fulfilled>
                {(recipeType: RecipeType) =>
                    <>
                        <dl>
                            <dt>Id:</dt>
                            <dd>{recipeType.id}</dd>
                            <dt>Name:</dt>
                            <dd>{recipeType.name}</dd>
                        </dl>
                        <Button aria-label={`Delete recipe type with id ${recipeType.id}`} onClick={() => onDelete(recipeType.id)}>
                            <FontAwesomeIcon icon={faTrash} />
                        </Button>
                    </>
                }
            </Async.Fulfilled>
        </Async>
    </>
}

RecipeTypeDetails.propTypes = {
    id: PropTypes.number.isRequired,
    onDelete: PropTypes.func.isRequired
}

export default RecipeTypeDetails
