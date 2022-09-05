package szakdoga.haztartas.recipe;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.InputType;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import szakdoga.haztartas.R;
import szakdoga.haztartas.firestore.DbHelper;
import szakdoga.haztartas.models.Ingredient;
import szakdoga.haztartas.models.Recipe;

public class NewRecipeActivity extends AppCompatActivity {

    private String userId;
    private String homeId;

    DbHelper dbHelper;

    LinearLayout ingredientsLayout;
    Spinner categorySpinner;
    EditText nameEditText;
    EditText quantityEditText;
    Spinner quantityUnitSpinner;
    EditText preparationTimeEditText;
    EditText descriptionEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_recipe);

        this.setTitle("Új recept");

        userId = getIntent().getStringExtra("userId");
        homeId = getIntent().getStringExtra("homeId");

        dbHelper = new DbHelper();

        ingredientsLayout = findViewById(R.id.newRecipeIngredientsLayout);
        categorySpinner = findViewById(R.id.newRecipeCategory);
        nameEditText = findViewById(R.id.newRecipeName);
        quantityEditText = findViewById(R.id.newRecipeQuantity);
        quantityUnitSpinner = findViewById(R.id.newRecipeQuantityUnit);
        preparationTimeEditText = findViewById(R.id.newRecipePreparationTime);
        descriptionEditText = findViewById(R.id.newRecipeDesripion);

        String cat = getIntent().getStringExtra("category");
        if (cat != null) {
            switch (cat) {
                case "leves":
                    break;
                case "főétel":
                    categorySpinner.setSelection(1);
                    break;
                case "desszert":
                    categorySpinner.setSelection(2);
                    break;
                default:
                    break;

            }
        }
    }

    @SuppressLint("ResourceType")
    public void clickAddIngredient(View view) {
        LinearLayout newRow = new LinearLayout(this);
        newRow.setOrientation(LinearLayout.HORIZONTAL);

        EditText quantity = new EditText(this);
        quantity.setInputType(InputType.TYPE_CLASS_NUMBER);
        quantity.setHint("500");
        quantity.setEms(10);
        quantity.setWidth((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 70, getResources().getDisplayMetrics()));
        newRow.addView(quantity);

        Spinner quantityUnitSpinner =  new Spinner(this);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.ingredient_quantity_units, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        quantityUnitSpinner.setAdapter(adapter);
        newRow.addView(quantityUnitSpinner);

        EditText name = new EditText(this);
        name.setInputType(InputType.TYPE_CLASS_TEXT);
        name.setHint("víz");
        name.setEms(10);
        name.setWidth((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 150, getResources().getDisplayMetrics()));
        newRow.addView(name);

        ImageButton removeIngredient = new ImageButton(this);
        removeIngredient.setImageResource(R.drawable.icon_remove);
        removeIngredient.setScaleType(ImageView.ScaleType.FIT_XY);
        removeIngredient.setBackgroundResource(R.color.transparent);
        removeIngredient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removeIngredient(view);
            }
        });
        newRow.addView(removeIngredient);

        ingredientsLayout.addView(newRow);
    }

    public void saveNewRecipe(View view) {
        String error = null;

        List<Ingredient> ingredients = new ArrayList<>();
        for (int i = 0; i < ingredientsLayout.getChildCount(); i++){
            LinearLayout row = (LinearLayout) ingredientsLayout.getChildAt(i);

            Ingredient ingredient = new Ingredient();
            ingredient.setQuantity(((EditText) row.getChildAt(0)).getText().toString());
            ingredient.setQuantityUnit(((Spinner) row.getChildAt(1)).getSelectedItem().toString());
            ingredient.setName(((EditText) row.getChildAt(2)).getText().toString());

            if (ingredient.getName().length() == 0){
                error = "Nem adta meg minden hozzávaló nevét!";
                break;
            }
            ingredients.add(ingredient);

        }
        if (ingredientsLayout.getChildCount() == 0){
            error = "Nem adott meg hozzávalót!";
        } else if (nameEditText.getText().toString().length() == 0){
            error = "Nem adott a receptnek nevet!";
        } else if (preparationTimeEditText.getText().toString().length() == 0){
            error = "Nem adta meg az elkészítési időt!";
        } else if (Integer.parseInt(quantityEditText.getText().toString()) < 1){
            error = "A mennyiségnek minimum egynek kell lennie!";
        }

        if (error == null) {
            Recipe recipe = new Recipe();
            recipe.setName(nameEditText.getText().toString());
            recipe.setCategory(categorySpinner.getSelectedItem().toString());
            recipe.setDescription(descriptionEditText.getText().toString());
            recipe.setPreparationTime(preparationTimeEditText.getText().toString());
            recipe.setQuantity(Integer.parseInt(quantityEditText.getText().toString()));
            recipe.setQuantityUnit(quantityUnitSpinner.getSelectedItem().toString());
            recipe.setIngredients(ingredients);


            dbHelper.getHomeCollection().document(homeId).collection("Recipes").add(recipe).addOnSuccessListener(result -> {
                dbHelper.getHomeCollection().document(homeId).collection("Recipes").document(result.getId()).update("id", result.getId());
            });
            Toast.makeText(this, "Sikeres mentés!", Toast.LENGTH_SHORT).show();
            this.finish();
        } else {
            Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
        }
    }

    public void removeIngredient(View view) {
        ingredientsLayout.removeView((View) view.getParent());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        //vissza gomb
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}