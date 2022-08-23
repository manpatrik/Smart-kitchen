package szakdoga.haztartas.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Receptek leírására szolgáló modell
 */
public class Recipe implements Serializable {
    private String id;
    private String name;
    private String category;
    private String description;
    private String ingredients;
    private String preparationTime;
    private int difficulty;
    private int quantity; // mennyiség
    private String quantityUnit; // fő, db, tepsi (mennyiségi egység)

    public Recipe(String id, String name, String preparationTime) {
        this.id = id;
        this.name = name;
        this.preparationTime = preparationTime;
    }

    public Recipe(String id, String name, String category, String description, String ingredients, String preparationTime, int difficulty, int quantity, String quantityUnit) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.description = description;
        this.ingredients = ingredients;
        this.preparationTime = preparationTime;
        this.difficulty = difficulty;
        this.quantity = quantity;
        this.quantityUnit = quantityUnit;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    /**
     *
     * @return elkészítési idő
     */
    public String getPreparationTime() {
        return preparationTime;
    }

    public int getDifficulty() {
        return difficulty;
    }

    /**
     *
     * @return mennyiség
     */
    public int getQuantity() {
        return quantity;
    }

    /**
     *
     * @return mennyiségi egység
     */
    public String getQuantityUnit() {
        return quantityUnit;
    }

    public String getCategory() {
        return category;
    }

    public List<Ingredient> getIngredients(){
        List<Ingredient> ingredients = new ArrayList<>();

        String [] ingredientsString = this.ingredients.split("#");
        if (ingredientsString.length == 0){
            return null;
        }
        for (String ingredientString : ingredientsString){

            String [] temp = ingredientString.split(";");
            if ( temp.length == 0){
                return null;
            }
            Ingredient ingredient = new Ingredient(temp[0], temp[1], temp[2]);
            ingredients.add(ingredient);
        }
        return ingredients;
    }
}
