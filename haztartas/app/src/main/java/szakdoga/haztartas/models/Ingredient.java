package szakdoga.haztartas.models;

import android.media.MediaParser;

import java.io.Serializable;
import java.util.Map;

/**
 * Hozzávalók leírására szolgáló modell
 */
public class Ingredient implements Serializable {
    private String quantity; // mennyiség
    private String quantityUnit; // mennyiségi egység (g, l, csomag, db)
    private String name;

    public Ingredient(){

    }

    public Ingredient(Map<String, String> ingredient){
        this.quantity = ingredient.get("quantity");
        this.quantityUnit = ingredient.get("quantityUnit");
        this.name = ingredient.get("name");
    }

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

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public void setQuantityUnit(String quantityUnit) {
        this.quantityUnit = quantityUnit;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString(){
        return quantity+";"+quantityUnit+";"+name;
    }
}
