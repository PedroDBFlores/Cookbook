/* eslint-disable react/jsx-key */
import React from "react"
import 'react-bootstrap-table-next/dist/react-bootstrap-table2.min.css'
import BootstrapTable from 'react-bootstrap-table-next'
import { RecipeType } from "../../../dto"
import PropTypes from "prop-types"

export interface RecipeTypeListProps {
  recipeTypes : Array<RecipeType>
}

const RecipeTypeList: React.FC<RecipeTypeListProps> = ({recipeTypes}) => {
  const columns = [{
    dataField: 'id',
    text: 'Id'
  }, {
    dataField: 'name',
    text: 'Name'
  }]
  
  return <BootstrapTable keyField="id" data={recipeTypes} columns={columns}>

  </BootstrapTable>
}
export default RecipeTypeList

RecipeTypeList.propTypes = {
  recipeTypes: PropTypes.array.isRequired
}