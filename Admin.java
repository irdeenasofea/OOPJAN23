package com.example.jommasak;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Admin {

    private Connection conn;

    public Admin() {
        try {
            conn = DriverManager.getConnection("jdbc:mysql://localhost/irdeena_sofea", "irdeena_sofea", "Irdeena@26");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Method to add a recipe
    public void addRecipe(String recipename, String ingredientsList, String[] steps) {
        try {
            // Insert the recipe
            String query = "INSERT INTO recipes (recipe_name) VALUES (?)";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, recipename);

            stmt.executeUpdate();

            // Get the ID of the newly inserted recipe
            int recipeId = getLatestRecipeId();

            // Split the ingredients string into separate ingredients and insert them
            String[] ingredients = ingredientsList.split(",");
            String ingredientQuery = "INSERT INTO ingredients (ingredients_name) VALUES (?)";
            PreparedStatement ingredientStmt = conn.prepareStatement(ingredientQuery);
            for (String ingredient : ingredients) {
                ingredientStmt.setString(1, ingredient.trim());
                ingredientStmt.executeUpdate();

                // Get the ID of the newly inserted ingredient
                ResultSet rs = ingredientStmt.getGeneratedKeys();
                int ingredientId = 0;
                if (rs.next()) {
                    ingredientId = rs.getInt(1);
                }

                // Insert the recipe-ingredient mapping
                String recipeIngredientQuery = "INSERT INTO recipe_ingredients (recipe_ID, ingredients_ID) VALUES (?, ?)";
                PreparedStatement recipeIngredientStmt = conn.prepareStatement(recipeIngredientQuery);
                recipeIngredientStmt.setInt(1, recipeId);
                recipeIngredientStmt.setInt(2, ingredientId);
                recipeIngredientStmt.executeUpdate();
            }

            // Insert the steps
            for (String step : steps) {
                String stepQuery = "INSERT INTO instructions (steps) VALUES (?)";
                PreparedStatement stepStmt = conn.prepareStatement(stepQuery, PreparedStatement.RETURN_GENERATED_KEYS);
                stepStmt.setString(1, step);
                stepStmt.executeUpdate();

                // Get the ID of the newly inserted step
                ResultSet rs = stepStmt.getGeneratedKeys();
                int stepId = 0;
                if (rs.next()) {
                    stepId = rs.getInt(1);
                }

                // Insert the recipe-step mapping
                String recipeStepQuery = "INSERT INTO recipe_instructions (recipe_ID, instructions_ID) VALUES (?, ?)";
                PreparedStatement recipeStepStmt = conn.prepareStatement(recipeStepQuery);
                recipeStepStmt.setInt(1, recipeId);
                recipeStepStmt.setInt(2, stepId);
                recipeStepStmt.executeUpdate();
            }

            System.out.println("Recipe added successfully!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private int getLatestRecipeId() {
                int latestId = -1;

                try {
                    String query = "SELECT MAX(recipe_ID) AS max_id FROM recipes";
                    PreparedStatement stmt = conn.prepareStatement(query);
                    ResultSet rs = stmt.executeQuery();
                    if (rs.next()) {
                        latestId = rs.getInt("max_id");
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }

                return latestId;
            }


    // Method to delete a recipe
                public void deleteRecipe(int recipeId) {
                    try {
                        // Delete the recipe-ingredient mappings
                        String recipeIngredientQuery = "DELETE FROM recipe_ingredients WHERE recipe_ID = ?";
                        PreparedStatement recipeIngredientStmt = conn.prepareStatement(recipeIngredientQuery);
                        recipeIngredientStmt.setInt(1, recipeId);
                        recipeIngredientStmt.executeUpdate();

                        // Delete the recipe-instruction mappings
                        String recipeStepQuery = "DELETE FROM recipe_instructions WHERE recipe_ID = ?";
                        PreparedStatement recipeStepStmt = conn.prepareStatement(recipeStepQuery);
                        recipeStepStmt.setInt(1, recipeId);
                        recipeStepStmt.executeUpdate();

                        // Delete the recipe
                        String recipeQuery = "DELETE FROM recipes WHERE recipe_ID = ?";
                        PreparedStatement recipeStmt = conn.prepareStatement(recipeQuery);
                        recipeStmt.setInt(1, recipeId);
                        recipeStmt.executeUpdate();

                        System.out.println("Recipe deleted successfully!");
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }

                public static void main (String args[]){
                    Admin admin = new Admin();
                    admin.addRecipe("Butter Enoki", "mushroom", new String[]{"1. cook evenly", "2. stir well"});


                }
}

