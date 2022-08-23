package szakdoga.haztartas.models;

public class ShoppingListItem {
    private int KamraId;
    private int quantity; // mennyiség
    private String quantityUnit; // mennyiségi egység (g, l, csomag, db)

    public ShoppingListItem(int kamraId, int quantity, String quantityUnit) {
        KamraId = kamraId;
        this.quantity = quantity;
        this.quantityUnit = quantityUnit;
    }

    public int getKamraId() {
        return KamraId;
    }

    public int getQuantity() {
        return quantity;
    }

    public String getQuantityUnit() {
        return quantityUnit;
    }
}
