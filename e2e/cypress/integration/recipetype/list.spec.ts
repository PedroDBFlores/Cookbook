describe("Recipe type list tests", () => {
    it("visits the the recipe type list", () => {
        cy.visit("http://localhost:8080")

        cy.contains("Recipe type").click()

        cy.contains("h2", "Recipe type")
    })

    it("creates a recipe type", () => {
        cy.visit("http://localhost:8080")

        cy.contains("Recipe type").click()

        cy.contains("h2", "Recipe type")

        cy.contains("button", "Create").click()

        cy.contains("Name").type("E2E Recipe type")

        cy.contains("button", "Create").click()

        cy.contains("Recipe type details")
        cy.contains("E2E Recipe type")

        cy.contains("button[aria-label]='delete recipe type with'").click()
        cy.contains("button", "Delete")
    })
})