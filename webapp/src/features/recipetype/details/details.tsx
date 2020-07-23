import React from "react"
import {RecipeType} from "../../../dto"
import PropTypes from "prop-types"
import {findRecipeType} from "../../../services/recipe-type-service"
import {Async} from "react-async"
import {ResponseError} from "../../../dto/response-error";

interface RecipeTypeDetailsProps {
    id: number
}

const RecipeTypeDetails: React.FC<RecipeTypeDetailsProps> = ({id}) => {
    return <>
        <Async promiseFn={() => findRecipeType(id)}>
            <Async.Loading>Loading...</Async.Loading>
            <Async.Rejected>
                {(error: ResponseError) =>
                    <span>Error: {error.message}</span>
                }
            </Async.Rejected>
            <Async.Fulfilled>
                {(data: RecipeType) =>
                    <dl>
                        <dt>Id:</dt>
                        <dd>{data?.id}</dd>
                        <dt>Name:</dt>
                        <dd>{data?.name}</dd>
                    </dl>
                }
            </Async.Fulfilled>
        </Async>
    </>
}

RecipeTypeDetails.propTypes = {
    id: PropTypes.number.isRequired
}

export default RecipeTypeDetails
