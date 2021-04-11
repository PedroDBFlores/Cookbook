CREATE TABLE IF NOT EXISTS RecipeTypes (Id SERIAL PRIMARY KEY, name VARCHAR(64) NOT NULL);
ALTER TABLE RecipeTypes ADD CONSTRAINT RecipeTypes_name_UNIQUE UNIQUE (name);

CREATE TABLE IF NOT EXISTS Recipes (Id SERIAL PRIMARY KEY, recipeTypeId INT NOT NULL, name VARCHAR(128) NOT NULL,
description VARCHAR(256) NOT NULL, ingredients VARCHAR(2048) NOT NULL, preparingSteps VARCHAR(4096) NOT NULL,
CONSTRAINT FK_Recipes_recipeTypeId_Id FOREIGN KEY (recipeTypeId) REFERENCES RecipeTypes(Id) ON DELETE RESTRICT ON UPDATE RESTRICT);

CREATE TABLE IF NOT EXISTS Recipes (Id SERIAL PRIMARY KEY, recipeId INT NOT NULL, name VARCHAR(64) NOT NULL,
data BYTEA NOT NULL, CONSTRAINT FK_RecipePhotos_RecipeId_Id FOREIGN KEY (recipeId) REFERENCES Recipes(Id) ON DELETE RESTRICT ON UPDATE RESTRICT);