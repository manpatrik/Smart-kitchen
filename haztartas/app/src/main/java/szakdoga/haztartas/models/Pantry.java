package szakdoga.haztartas.models;

import java.io.Serializable;
import java.util.List;

/**
 * Kamrában lévő élelmiszer leírására szolgáló modell
 */
public class Pantry implements Serializable {
    private String id;
    private String name; // általánosított név (pl. rizs, nem coop gazdaságos rizs vagy S-budget rizs (ezek egy csoportba tartoznak))
    private double quantity; // mennyiség
    private String quantityUnit; // mennyiségi egység (g, l, csomag, db)
    private String where; // Hol tároljuk (hűtő, fagyasztó...)
    private List<String> barcodes;

    public Pantry(){

    }

    public Pantry(String name, double quantity, String quantityUnit, String where, List<String> barcodes) {
        this.name = name;
        this.quantity = quantity;
        this.quantityUnit = quantityUnit;
        this.where = where;
        this.barcodes = barcodes;
    }

    public Pantry(String id, String name, double quantity, String quantityUnit, String where, List<String> barcodes) {
        this.id = id;
        this.name = name;
        this.quantity = quantity;
        this.quantityUnit = quantityUnit;
        this.where = where;
        this.barcodes = barcodes;
    }

    public String getWhere() {
        return where;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    /**
     *
     * @return mennyiség
     */
    public double getQuantity() {
        return quantity;
    }

    /**
     *
     * @return mennyiségi egység
     */
    public String getQuantityUnit() {
        return quantityUnit;
    }

    public List<String> getBarcodes() {
        return barcodes;
    }
}
