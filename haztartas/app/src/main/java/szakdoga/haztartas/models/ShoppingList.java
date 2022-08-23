package szakdoga.haztartas.models;

/**
 * Bevásárló lista leírására szolgáló modell
 */
public class ShoppingList {
    private int id;
    private String name;
    private String description;
    private ShoppingListItem[] shoppingListItems;

    public ShoppingList(int id, String name, String description, ShoppingListItem[] shoppingListItems) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.shoppingListItems = shoppingListItems;
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

    public ShoppingListItem[] getShoppingListItems() {
        return shoppingListItems;
    }
}
