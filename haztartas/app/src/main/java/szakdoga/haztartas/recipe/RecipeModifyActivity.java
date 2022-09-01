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

import szakdoga.haztartas.R;
import szakdoga.haztartas.firebaseAuthentication.FirebaseAuthHelper;
import szakdoga.haztartas.firestore.DbHelper;
import szakdoga.haztartas.models.Ingredient;
import szakdoga.haztartas.models.Recipe;

public class RecipeModifyActivity extends AppCompatActivity {

    LinearLayout ingredientsLayout;
    Spinner categorySpinner;
    EditText nameEditText;
    EditText quantityEditText;
    Spinner quantityUnitSpinner;
    EditText preparationTimeEditText;
    EditText descriptionEditText;

    String userId;
    Recipe recipe;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        userId = getIntent().getStringExtra("userId");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_modify);

        this.setTitle("Recept módosítása");
        recipe = (Recipe) getIntent().getSerializableExtra("recipe");

        ingredientsLayout = findViewById(R.id.newRecipeIngredientsLayout);
        categorySpinner = findViewById(R.id.newRecipeCategory);
        nameEditText = findViewById(R.id.newRecipeName);
        quantityEditText = findViewById(R.id.newRecipeQuantity);
        quantityUnitSpinner = findViewById(R.id.newRecipeQuantityUnit);
        preparationTimeEditText = findViewById(R.id.newRecipePreparationTime);
        descriptionEditText = findViewById(R.id.newRecipeDesripion);

        // form jelenlegi adatokkal feltöltése
        nameEditText.setText(recipe.getName());
        quantityEditText.setText(Integer.toString(recipe.getQuantity()));
        preparationTimeEditText.setText(recipe.getPreparationTime());
        descriptionEditText.setText(recipe.getDescription());
        switch (recipe.getCategory()) {
            case "leves":
                break;
            case "főétel":
                categorySpinner.setSelection(1);
                break;
            case "desszert":
                categorySpinner.setSelection(2);
                break;
        }

        switch (recipe.getQuantityUnit()){
            case "fő":
                break;
            case "darab":
                quantityUnitSpinner.setSelection(1);
                break;
        }
        for (Ingredient ingredient : recipe.getIngredients()){
            LinearLayout newRow = new LinearLayout(this);
            newRow.setOrientation(LinearLayout.HORIZONTAL);

            EditText quantity = new EditText(this);
            quantity.setInputType(InputType.TYPE_CLASS_NUMBER);
            quantity.setText(ingredient.getQuantity());
            quantity.setHint("500");
            quantity.setEms(10);
            quantity.setWidth((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 70, getResources().getDisplayMetrics()));
            newRow.addView(quantity);

            Spinner quantityUnitSpinner =  new Spinner(this);
            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.ingredient_quantity_units, android.R.layout.simple_spinner_item);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            quantityUnitSpinner.setAdapter(adapter);
            final String[] quantityUnits = getResources().getStringArray(R.array.ingredient_quantity_units);
            for (int i = 0; i < quantityUnits.length; i++){
                System.out.println(quantityUnits[i] +" == "+ ingredient.getQuantityUnit());
                if(quantityUnits[i].equals(ingredient.getQuantityUnit())){
                    quantityUnitSpinner.setSelection(i);
                    break;
                }
            }
            System.out.println("hallo");
            newRow.addView(quantityUnitSpinner);

            EditText name = new EditText(this);
            name.setInputType(InputType.TYPE_CLASS_TEXT);
            name.setText(ingredient.getName());
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
                    ingredientsLayout.removeView((View) view.getParent());
                }
            });
            newRow.addView(removeIngredient);

            ingredientsLayout.addView(newRow);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        FirebaseAuthHelper firebaseAuthHelper = new FirebaseAuthHelper();
        firebaseAuthHelper.isAuthUser(userId, this);
    }

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
                ingredientsLayout.removeView((View) view.getParent());
            }
        });
        newRow.addView(removeIngredient);

        ingredientsLayout.addView(newRow);
    }

    public void saveRecipe(View view) {
        String homeId = getIntent().getStringExtra("homeId");
        String error = null;

        String ingredients = "";
        for (int i = 0; i < ingredientsLayout.getChildCount(); i++){
            LinearLayout row = (LinearLayout) ingredientsLayout.getChildAt(i);
            String quantity = ((EditText) row.getChildAt(0)).getText().toString();
            String quantityUnit = ((Spinner) row.getChildAt(1)).getSelectedItem().toString();
            String name = ((EditText) row.getChildAt(2)).getText().toString();
            ingredients += quantity + ";" + quantityUnit + ";" + name + "#";
            if (name.length() == 0){
                error = "Nem adta meg minden hozzávaló nevét!";
                break;
            }
        }
        if (ingredientsLayout.getChildCount() > 0){
            ingredients = ingredients.substring(0, ingredients.length()-1);
        } else {
            error = "Nem adott meg hozzávalót!";
        }

        if (nameEditText.getText().toString().length() == 0){
            error = "Nem adott a receptnek nevet!";
        } else if (preparationTimeEditText.getText().toString().length() == 0){
            error = "Nem adta meg az elkészítési időt!";
        } else if (Integer.parseInt(quantityEditText.getText().toString()) < 1){
            error = "A mennyiségnek minimum egynek kell lennie!";
        }

        if (error == null) {
            DbHelper dbHelper = new DbHelper();
            dbHelper.getHomeCollection().document(homeId).collection("Recipes").document(recipe.getId()).update(
                    "name", nameEditText.getText().toString(),
                    "category", categorySpinner.getSelectedItem().toString(),
                    "description", descriptionEditText.getText().toString(),
                    "ingredients", ingredients,
                    "preparationTime", preparationTimeEditText.getText().toString(),
                    "difficulty", 1,
                    "quantity", quantityEditText.getText().toString(),
                    "quantityUnit", quantityUnitSpinner.getSelectedItem().toString()
            );

            Toast.makeText(this, "Sikeres módosítás!", Toast.LENGTH_LONG).show();
            this.finish();
        } else {
            Toast.makeText(this, error, Toast.LENGTH_LONG).show();
        }
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