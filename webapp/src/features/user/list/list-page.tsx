import React from "react"
import UserList from "./list"
//import Typography from "@material-ui/core/Typography/Typography"

const UserListPage: React.FC<{}> = () => {
    return <>
        <span>User List</span>
        <UserList />
        <hr />
    </>
}

export default UserListPage