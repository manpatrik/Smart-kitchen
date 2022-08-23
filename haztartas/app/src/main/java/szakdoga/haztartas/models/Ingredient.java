package szakdoga.haztartas.models;

/**
 * Hozzávalók leírására szolgáló modell
 */
public class Ingredient {
    private String quantity; // mennyiség
    private String quantityUnit; // mennyiségi egység (g, l, csomag, db)
    private String name;

    public Ingredient(String quantity, String quantityUnit, String name) {
        this.quantity = quantity;
        this.quantityUnit = quantityUnit;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String getQuantity() {
        return quantity;
    }

    public String getQuantityUnit() {
        return quantityUnit;
    }
}
