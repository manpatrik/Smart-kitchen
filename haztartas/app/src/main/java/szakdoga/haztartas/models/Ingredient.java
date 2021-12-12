package szakdoga.haztartas.models;

/**
 * Hozzávalók leírására szolgáló modell
 */
public class Ingredient {
    private int kamraId;
    private int quantity; // mennyiség
    private String quantityUnit; // mennyiségi egység (g, l, csomag, db)

    public Ingredient(int kamraId, int quantity, String quantityUnit) {
        this.kamraId = kamraId;
        this.quantity = quantity;
        this.quantityUnit = quantityUnit;
    }

    public int getKamraId() {
        return kamraId;
    }

    public int getQuantity() {
        return quantity;
    }

    public String getQuantityUnit() {
        return quantityUnit;
    }
}
