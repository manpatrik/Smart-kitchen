package szakdoga.haztartas.recipe;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.InputType;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.firestore.DocumentSnapshot;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import szakdoga.haztartas.R;
import szakdoga.haztartas.firebaseAuthentication.FirebaseAuthHelper;
import szakdoga.haztartas.firestore.DbHelper;
import szakdoga.haztartas.models.Ingredient;
import szakdoga.haztartas.models.Pantry;
import szakdoga.haztartas.models.Recipe;

public class RecipeDetailsActivity extends AppCompatActivity {

    private LinearLayout ingredientslayout;
    private TextView recipeName;
    private TextView recipeQuantityTextView;
    private TextView recipeQuantityUnit;
    private TextView recipeDescription;

    private Recipe recipe;
    private String userId;
    private String homeId;

    private FirebaseAuthHelper firebaseAuthHelper;
    DbHelper dbHelper;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_details);

        recipe = (Recipe) this.getIntent().getSerializableExtra("recipe");
        userId = getIntent().getStringExtra("userId");
        homeId = getIntent().getStringExtra("homeId");

        dbHelper = new DbHelper();
        firebaseAuthHelper = new FirebaseAuthHelper();

        ingredientslayout = findViewById(R.id.ingredientsLayout);
        recipeName = findViewById(R.id.recipeName);
        recipeQuantityTextView = findViewById(R.id.recipeQuantity);
        recipeQuantityUnit = findViewById(R.id.recipeQuantityUnit);
        recipeDescription = findViewById(R.id.recipeDescription);
    }

    @Override
    protected void onResume() {
        super.onResume();
        recipeUpdate();
        firebaseAuthHelper.isAuthUser(userId, this);
    }

    @SuppressLint("ResourceAsColor")
    private void recipeUpdate() {
        ingredientslayout.removeAllViews();
        dbHelper.getHomeCollection().document(homeId).collection("Recipes").document(recipe.getId()).get().addOnSuccessListener(data -> {
            recipe = data.toObject(Recipe.class);

            recipeName.setText(recipe.getName());
            recipeQuantityTextView.setText(recipe.getQuantity()+"");
            recipeQuantityUnit.setText(recipe.getQuantityUnit());
            recipeDescription.setText(recipe.getDescription());
            if(recipe.getIngredients() != null) {
                for (Ingredient ingredient : recipe.getIngredients()) {
                    CheckBox name = new CheckBox(this);
                    name.setText(ingredient.getQuantity() + " " + ingredient.getQuantityUnit() + " " + ingredient.getName());
                    ingredientslayout.addView(name);
                }
            }
        });
    }

    @SuppressLint("SetTextI18n")
    public void clickRemovePersonButton(View view) {
        int num = Integer.parseInt((String) recipeQuantityTextView.getText());
        if (num > 1){
            num--;

            recipeQuantityTextView.setText(num + "");
            for (int i = 0; i < ingredientslayout.getChildCount(); i++){
                String tempText = ((CheckBox) ingredientslayout.getChildAt(i)).getText().toString();
                String quantity = tempText.split(" ")[0];
                String textWithoutQuantity = tempText.replaceAll(quantity, "");
                try {
                    float newQuantity = (Float.parseFloat(quantity) / (num + 1) * num);
                    String newQuantityString = Float.toString(newQuantity);

                    if (newQuantityString.charAt(newQuantityString.length()-1) == '0'){
                        ((CheckBox) ingredientslayout.getChildAt(i)).setText(Math.round(newQuantity) + textWithoutQuantity);
                    } else{
                        ((CheckBox) ingredientslayout.getChildAt(i)).setText(newQuantity + textWithoutQuantity);
                    }
                } catch (Exception ignored){}

            }
        }
    }

    @SuppressLint("SetTextI18n")
    public void clickAddPersonButton(View view) {
        LinearLayout ingredientsTable = findViewById(R.id.ingredientsLayout);
        int num = Integer.parseInt((String) recipeQuantityTextView.getText());
        num++;
        recipeQuantityTextView.setText(num + "");

        for (int i = 0; i < ingredientslayout.getChildCount(); i++){
            String tempText = ((CheckBox) ingredientslayout.getChildAt(i)).getText().toString();
            String quantity = tempText.split(" ")[0];
            String textWithiutQuantity = tempText.replaceAll(quantity, "");
            try {
                float newQuantity = (Float.parseFloat(quantity) / (num - 1) * num);
                String newQuantityString = Float.toString(newQuantity);

                if (newQuantityString.charAt(newQuantityString.length()-1) == '0'){
                    ((CheckBox) ingredientslayout.getChildAt(i)).setText(Math.round(newQuantity) + textWithiutQuantity);
                } else{
                    ((CheckBox) ingredientslayout.getChildAt(i)).setText(newQuantity + textWithiutQuantity);
                }
            } catch (Exception ignored){}
        }
    }


    private void deleteRecipe() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Biztosan törölni szeretné ezt a receptet: "+ recipe.getName() +"?");
        builder.setCancelable(true);

        builder.setPositiveButton(
                "Törlés",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // törlés
                        dbHelper.getHomeCollection().document(homeId).collection("Recipes").document(recipe.getId()).delete();
                        Toast.makeText(builder.getContext(), "Sikeres törlés!", Toast.LENGTH_LONG).show();
                        finish();
                    }
                });

        builder.setNegativeButton(
                "Mégsem",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alert = builder.create();
        alert.show();
    }

    public void cooking(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        int recipeQuantity =  Integer.parseInt(recipeQuantityTextView.getText().toString());

        builder.setTitle("Főzés "+recipeQuantity+ " főre");
        builder.setMessage("Az alábbi hozzávalók lesznek levonva a recept szerint "+ recipeQuantity + " főre:");

        // hozzávalók
        dbHelper.getHomeCollection().document(homeId).collection("Pantry").whereIn("name", recipe.getIngredientsNames()).get().addOnSuccessListener(queryDocumentSnapshots -> {
            int padding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5, getResources().getDisplayMetrics());

            TableLayout tableLayout = new TableLayout(RecipeDetailsActivity.this);
            tableLayout.setPadding(padding, padding, padding, padding);

            TableRow tableHead = new TableRow(RecipeDetailsActivity.this);
            tableHead.setGravity(Gravity.CENTER_VERTICAL);

            TextView nameHeadTextView = new TextView(RecipeDetailsActivity.this);
            nameHeadTextView.setText("hozzávaló");
            nameHeadTextView.setPadding(padding, padding, padding, padding);
            tableHead.addView(nameHeadTextView);

            TextView oldHeadTextView = new TextView(RecipeDetailsActivity.this);
            oldHeadTextView.setText("régi mennyiség");
            oldHeadTextView.setPadding(padding, padding, padding, padding);
            tableHead.addView(oldHeadTextView);

            TextView newHeadEditText = new TextView(RecipeDetailsActivity.this);
            newHeadEditText.setText("új mennyiség");
            newHeadEditText.setPadding(padding, padding, padding, padding);
            tableHead.addView(newHeadEditText);

            tableLayout.addView(tableHead);

            List<Pantry> pantries = new ArrayList<>();
            for (DocumentSnapshot data : queryDocumentSnapshots){
                Pantry pantry = data.toObject(Pantry.class);
                pantries.add(pantry);

                TableRow tableRow = new TableRow(RecipeDetailsActivity.this);
                tableRow.setGravity(Gravity.CENTER_VERTICAL);

                TextView nameTextView = new TextView(RecipeDetailsActivity.this);
                nameTextView.setText(pantry.getName());
                nameTextView.setPadding(padding, padding, padding, padding);
                tableRow.addView(nameTextView);

                TextView oldTextView = new TextView(RecipeDetailsActivity.this);
                oldTextView.setText(pantry.getQuantity() + pantry.getQuantityUnit());
                oldTextView.setPadding(padding, padding, padding, padding);
                tableRow.addView(oldTextView);


                LinearLayout newQuantityLayout = new LinearLayout(RecipeDetailsActivity.this);
                newQuantityLayout.setOrientation(LinearLayout.HORIZONTAL);
                newQuantityLayout.setGravity(Gravity.CENTER_VERTICAL);
                newQuantityLayout.setPadding(padding, padding, padding, padding);

                EditText newEditText = new EditText(RecipeDetailsActivity.this);
                for (Ingredient ingredient : recipe.getIngredients()){
                    if (ingredient.getName().equals(pantry.getName())){
                        newEditText.setText(Double.toString(getNewQuantity(pantry.getQuantity(), pantry.getQuantityUnit(), Double.parseDouble(ingredient.getQuantity()), ingredient.getQuantityUnit(), recipe.getQuantity() , recipeQuantity)));
                        break;
                    }
                }
                newEditText.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL);
                newQuantityLayout.addView(newEditText);

                TextView quantytyTextView = new TextView(RecipeDetailsActivity.this);
                quantytyTextView.setText(pantry.getQuantityUnit());
                newQuantityLayout.addView(quantytyTextView);

                tableRow.addView(newQuantityLayout);

                tableLayout.addView(tableRow);
            }

            builder.setView(tableLayout);

            builder.setNegativeButton("Mégsem", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.cancel();
                }
            });

            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int j) {
                    for (int i = 1; i < tableLayout.getChildCount(); i++){
                        TableRow tableRow = (TableRow) tableLayout.getChildAt(i);
                        String name = pantries.get(i-1).getName();
                        System.out.println(tableRow.getChildAt(2));
                        Double quantity = Double.parseDouble(((EditText)((LinearLayout) tableRow.getChildAt(2)).getChildAt(0)).getText().toString());
                        dbHelper.getHomeCollection().document(homeId).collection("Pantry").document(pantries.get(i-1).getId()).update("quantity", quantity);
                    }
                    dialogInterface.cancel();
                    Toast.makeText(RecipeDetailsActivity.this, "Sikeres főzés! Hozzávalók kivonva!", Toast.LENGTH_SHORT).show();
                }
            });

            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        });
    }

    private double getNewQuantity(double pantryIngQuantity, String pantryIngQuantityUnit, double recipeIngQuantity, String recipeIngQuantityUnit, double oldQuantity, double newQuantity) {
        Double newQuantityMultiplier;
        Double returnQuantity = pantryIngQuantity;

        System.out.println("units: "+ recipeIngQuantityUnit+pantryIngQuantityUnit);
        if (pantryIngQuantityUnit.equals(recipeIngQuantityUnit)){
            newQuantityMultiplier = (double)newQuantity / (double)oldQuantity;
            returnQuantity =  pantryIngQuantity - (recipeIngQuantity * newQuantityMultiplier);
        } else if ((pantryIngQuantityUnit.equals("liter") && recipeIngQuantityUnit.equals("ml"))  || (pantryIngQuantityUnit.equals("kg") && recipeIngQuantityUnit.equals("g"))){
            newQuantityMultiplier = (double)newQuantity / (double)oldQuantity / 1000.0;
            returnQuantity =  pantryIngQuantity - (recipeIngQuantity * newQuantityMultiplier);
        } else if (pantryIngQuantityUnit.equals("ml") && recipeIngQuantityUnit.equals("liter") || (pantryIngQuantityUnit.equals("g") && recipeIngQuantityUnit.equals("kg"))){
            newQuantityMultiplier = (double)newQuantity / (double)oldQuantity * 1000.0;
            returnQuantity =  pantryIngQuantity - (recipeIngQuantity * newQuantityMultiplier);
        }
        if (returnQuantity < 0){
            return 0;
        }
        return returnQuantity;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.recipe_details_menu, menu);

        //vissza gomb
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.logOut:
                firebaseAuthHelper.logOut(this);
                return true;

            case R.id.recipeModify:
                Intent recipeModifyIntent = new Intent(this, RecipeModifyActivity.class);
                recipeModifyIntent.putExtra("recipe", (Serializable) recipe);
                recipeModifyIntent.putExtra("userId", userId);
                recipeModifyIntent.putExtra("homeId", homeId);
                this.startActivity(recipeModifyIntent);
                return true;

            case R.id.recipeDelete:
                deleteRecipe();
                return true;

            case android.R.id.home:
                onBackPressed();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void shoppingList(View view) {
        boolean nothing = true;
        for (int i = 0; i < ingredientslayout.getChildCount(); i++) {
            if (((CheckBox) ingredientslayout.getChildAt(i)).isChecked()){
                nothing = false;
                break;
            }
        }
        if(nothing){
            Toast.makeText(this, "Jelölje be mit szeretne a bevásárló listához adni!", Toast.LENGTH_SHORT).show();
        } else {
            AlertDialog.Builder newOrExistShoppingListBuider = new AlertDialog.Builder(this);
            newOrExistShoppingListBuider.setTitle("Új vagy meglévő?");
            newOrExistShoppingListBuider.setMessage("Új vagy már meglévő bevásárló listához szeretné adni a hozzávalókat?");

            newOrExistShoppingListBuider.setPositiveButton("Új", ((dialogInterface, i) -> {

            }));

            newOrExistShoppingListBuider.setNegativeButton("Meglévő", ((dialogInterface, i) -> {

            }));

            newOrExistShoppingListBuider.setNeutralButton("Mégsem", ((dialogInterface, i) -> {
                dialogInterface.cancel();
            }));

            newOrExistShoppingListBuider.create().show();
        }
    }
}