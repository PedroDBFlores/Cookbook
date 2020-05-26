import React from "react"
import { render } from "@testing-library/react"
import UserListPage from "../../../../src/features/user/list/list-page"

describe("User list page", () => {
    describe("Render", () => {
        it("has the required content", () => {
            const {queryByText} = render(<UserListPage />)

            expect(queryByText("User List")).toBeInTheDocument()
        })
    })
})