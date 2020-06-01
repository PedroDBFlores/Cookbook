import React from "react"
import PropTypes from "prop-types"
import Navbar from "react-bootstrap/Navbar"
import Nav from "react-bootstrap/Nav"
import NavDropdown from "react-bootstrap/NavDropdown"
import { LinkContainer } from "react-router-bootstrap"

export interface ApplicationToolbarProps {
    title: string
}

const ApplicationToolbar: React.FC<ApplicationToolbarProps> = ({ title }) => {
    return <Navbar bg="light" expand="lg" role="navbar">
        <Navbar.Brand href="/">{title}</Navbar.Brand>
        <Navbar.Toggle aria-controls="basic-navbar-nav" />
        <Navbar.Collapse id="basic-navbar-nav">
            <Nav className="mr-auto" role="navbar-menu">
                <Nav.Link href="#home">Home</Nav.Link>
                <Nav.Link href="#link">Link</Nav.Link>
                <NavDropdown title="Administration" id="basic-nav-dropdown">
                    <LinkContainer to="/users">
                        <NavDropdown.Item>Users</NavDropdown.Item>
                    </LinkContainer>
                </NavDropdown>
            </Nav>
        </Navbar.Collapse>
    </Navbar>
}

ApplicationToolbar.propTypes = {
    title: PropTypes.string.isRequired
}

export default ApplicationToolbar