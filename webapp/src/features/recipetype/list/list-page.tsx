import React, {useRef} from "react"
import RecipeTypeList from "./list"
import {deleteRecipeType, getAllRecipeTypes} from "../../../services/recipe-type-service"
import {RecipeType} from "../../../dto"
import {useAsync} from "react-async"
import Button from "react-bootstrap/Button"
import {useHistory} from "react-router-dom"

const RecipeTypeListPage: React.FC<unknown> = () => {
    const history = useHistory()
    const getPromiseRef = useRef(() => getAllRecipeTypes())
    const onDelete = (id: number) => deleteRecipeType(id)
    const {isPending, data: recipeTypes, error} = useAsync<Array<RecipeType>>({
        promiseFn: getPromiseRef.current
    })

    if (isPending) return <span>Loading...</span>
    if (error) return <span>Error: {error.message}</span>

    const navigateToCreateRecipeType = ()=> history.push("/recipetype/new")

    return (recipeTypes &&
      <>
        <Button aria-label="Create new recipe type" onClick={navigateToCreateRecipeType}>Create</Button>
        <RecipeTypeList recipeTypes={recipeTypes} onDelete={(id => onDelete(id))}/>
      </>
    ) ?? null
}

export default RecipeTypeListPage
