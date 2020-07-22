import React from "react"
import { RecipeType } from "../../../dto"
import PropTypes from "prop-types"
import { findRecipeType } from "../../../services/recipe-type-service"
import { useAsync } from "react-async"

interface RecipeTypeDetailsProps {
    id: number
}

const RecipeTypeDetails: React.FC<RecipeTypeDetailsProps> = ({ id }) => {
    const { data, error, isLoading } = useAsync<RecipeType>({ promise: findRecipeType(id) })

    console.log({ data, error, isLoading })

    if (isLoading) return <div>Loading...</div>
    if (error) return <div>Error: {error.message}</div>

    return <>
        <dl>
            <dt>Id:</dt>
            <dd>{data?.id}</dd>
            <dt>Name:</dt>
            <dd>{data?.name}</dd>
        </dl>
    </>
}

RecipeTypeDetails.propTypes = {
    id: PropTypes.number.isRequired
}

export default RecipeTypeDetails