package szakdoga.haztartas.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Receptek leírására szolgáló modell
 */
public class Recipe implements Serializable {
    private String id;
    private String name;
    private String category;
    private String description;
    private List<Ingredient> ingredients;
    private String preparationTime;
    private int difficulty;
    private int quantity; // mennyiség
    private String quantityUnit; // fő, db, tepsi (mennyiségi egység)

    public Recipe() {
        this.id = null;
        this.difficulty = 1;
    }

    public Recipe(String id, String name, String category, String description, String preparationTime, int difficulty, int quantity, String quantityUnit) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.description = description;
        this.preparationTime = preparationTime;
        this.difficulty = difficulty;
        this.quantity = quantity;
        this.quantityUnit = quantityUnit;
        this.ingredients = new ArrayList<>();
    }

    public Recipe(String id, String name, String category, String description, List<Map<String, String>> ingredients, String preparationTime, int difficulty, int quantity, String quantityUnit) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.description = description;
        this.preparationTime = preparationTime;
        this.difficulty = difficulty;
        this.quantity = quantity;
        this.quantityUnit = quantityUnit;
        this.ingredients = new ArrayList<>();

        for (Map<String, String > ingredient : ingredients){
            this.ingredients.add(new Ingredient(ingredient));
        }
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Ingredient> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<Ingredient> ingredients) {
        this.ingredients = ingredients;
    }

    public String getPreparationTime() {
        return preparationTime;
    }

    public void setPreparationTime(String preparationTime) {
        this.preparationTime = preparationTime;
    }

    public int getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(int difficulty) {
        this.difficulty = difficulty;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getQuantityUnit() {
        return quantityUnit;
    }

    public void setQuantityUnit(String quantityUnit) {
        this.quantityUnit = quantityUnit;
    }

    public void addIngredient(Ingredient ingredient){
        this.ingredients.add(ingredient);
    }

    public List<String> getIngredientsNames(){
        List<String> ingredientsNames = new ArrayList<>();

        for (Ingredient ingredient : this.ingredients){
            ingredientsNames.add(ingredient.getName());
        }

        return ingredientsNames;
    }
}
