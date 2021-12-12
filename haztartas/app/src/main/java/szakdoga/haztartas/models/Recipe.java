package szakdoga.haztartas.models;

/**
 * Receptek leírására szolgáló modell
 */
public class Recipe {
    private int id;
    private String name;
    private String category;
    private String description;
    private Ingredient[] ingredients;
    private int preparationTime;
    private int difficulty;
    private int quantity; // mennyiség
    private String quantityUnit; // fő, db, tepsi (mennyiségi egység)

    public Recipe(int id, String name, int preparationTime) {
        this.id = id;
        this.name = name;
        this.preparationTime = preparationTime;
    }

    public Recipe(int id, String name, String category, String description, Ingredient[] ingredients, int preparationTime, int difficulty, int quantity, String quantityUnit) {
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

    public int getId() {
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
    public int getPreparationTime() {
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

    public Ingredient[] getIngredients() {
        return ingredients;
    }
}
