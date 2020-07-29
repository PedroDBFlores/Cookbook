import React, {useState} from "react"
import PropTypes from "prop-types"
import {AppBar, Toolbar, IconButton, Typography, Button} from "@material-ui/core"
import MenuIcon from "@material-ui/icons/Menu"
import {makeStyles} from "@material-ui/core/styles"

export interface ApplicationToolbarProps {
    title: string
}

const styles = makeStyles({})

const ApplicationToolbar: React.FC<ApplicationToolbarProps> = ({title}) => {
    return (
        <AppBar position="static">
            <Toolbar>
                <IconButton edge="start" color="inherit" aria-label="menu">
                    <MenuIcon/>
                </IconButton>
                <Typography variant="h6">
                    {title}
                </Typography>
                <Button color="inherit">Login</Button>
            </Toolbar>
        </AppBar>)
}

// const ApplicationToolbar: React.FC<ApplicationToolbarProps> = ({title}) => {
//     return <Navbar bg="light" expand="lg" role="navbar">
//         <Navbar.Brand href="/">{title}</Navbar.Brand>
//         <Navbar.Toggle aria-controls="basic-navbar-nav"/>
//         <Navbar.Collapse id="basic-navbar-nav">
//             <Nav className="mr-auto" role="navbar-menu">
//                 <Nav.Link href="#home">Home</Nav.Link>
//                 <NavDropdown aria-label="Administration menu dropdown"
//                              title="Administration" id="basic-nav-dropdown">
//                     <LinkContainer to="/recipetype">
//                         <NavDropdown.Item aria-label="Recipe types option">Recipe types</NavDropdown.Item>
//                     </LinkContainer>
//                 </NavDropdown>
//             </Nav>
//         </Navbar.Collapse>
//     </Navbar>
// }

ApplicationToolbar.propTypes = {
    title: PropTypes.string.isRequired
}

export default ApplicationToolbar