package szakdoga.haztartas.models;

/**
 * Kamrában lévő élelmiszer leírására szolgáló modell
 */
public class Pantry {
    private int id;
    private String name; // általánosított név (pl. rizs, nem coop gazdaságos rizs vagy S-budget rizs (ezek egy csoportba tartoznak))
    private int quantity; // mennyiség
    private String quantityUnit; // mennyiségi egység (g, l, csomag, db)
    private Food[] foods; // élelmiszerek, amellyek ebbe a csoportba tartoznak

    public Pantry(int id, String name, int quantity, String quantityUnit, Food[] foods) {
        this.id = id;
        this.name = name;
        this.quantity = quantity;
        this.quantityUnit = quantityUnit;
        this.foods = foods;
    }

    public Pantry(int id, String name, int quantity, String quantityUnit) {
        this.id = id;
        this.name = name;
        this.quantity = quantity;
        this.quantityUnit = quantityUnit;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    /**
     *
     * @return élelmiszerek, amellyek ebbe a csoportba tartoznak
     */
    public Food[] getFoods() {
        return foods;
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
}
