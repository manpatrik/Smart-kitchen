package szakdoga.haztartas.models;

/**
 * Boltban vett élelmiszerek leírására szolgáló modell
 */
public class Food {
    private int barCode; // vonalkód vagy kód (id)
    private String name;
    private int quantity; // mennyiség
    private int guantityUnit; // mennyiségi egység (g, l, csomag, db)
    private String description;

    public Food(int barCode, String name, int quantity, int guantityUnit, String description) {
        this.barCode = barCode;
        this.name = name;
        this.quantity = quantity;
        this.guantityUnit = guantityUnit;
        this.description = description;
    }

    /**
     *
     * @return vonalkód, kód
     */
    public int getBarCode() {
        return barCode;
    }

    public String getName() {
        return name;
    }

    /**
     *
     * @return mennyiség
     */
    public int getQuantity() {
        return quantity;
    }

    public String getDescription() {
        return description;
    }

    /**
     *
     * @return mennyiségi egység
     */
    public int getGuantityUnit() {
        return guantityUnit;
    }
}
