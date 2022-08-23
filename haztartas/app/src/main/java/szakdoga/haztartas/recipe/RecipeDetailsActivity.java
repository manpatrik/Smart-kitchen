package szakdoga.haztartas.recipe;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.Serializable;

import szakdoga.haztartas.R;
import szakdoga.haztartas.firebaseAuthentication.FirebaseAuthHelper;
import szakdoga.haztartas.firestore.DbHelper;
import szakdoga.haztartas.models.Ingredient;
import szakdoga.haztartas.models.Recipe;
import szakdoga.haztartas.pantry.PantryListActivity;

public class RecipeDetailsActivity extends AppCompatActivity {

    private LinearLayout ingredientslayout;
    private TextView recipeName;
    private TextView recipeQuantity;
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
        recipeQuantity = findViewById(R.id.recipeQuantity);
        recipeQuantityUnit = findViewById(R.id.recipeQuantityUnit);
        recipeDescription = findViewById(R.id.recipeDescription);
    }

    @Override
    protected void onResume() {
        super.onResume();
        recipeUpdate();
        firebaseAuthHelper.isAuthUser(userId, this);
    }

    private void recipeUpdate() {
        ingredientslayout.removeAllViews();
        dbHelper.getHomeCollection().document(homeId).collection("Recipes").document(recipe.getId()).get().addOnSuccessListener(data -> {
            recipe = new Recipe(
                    data.getId(),
                    data.get("name").toString(),
                    data.get("category").toString(),
                    data.get("description").toString(),
                    data.get("ingredients").toString(),
                    data.get("preparationTime").toString(),
                    Integer.parseInt(data.get("difficulty").toString()),
                    Integer.parseInt(data.get("quantity").toString()),
                    data.get("quantityUnit").toString());

            recipeName.setText(recipe.getName());
            recipeQuantity.setText(recipe.getQuantity()+"");
            recipeQuantityUnit.setText(recipe.getQuantityUnit());
            recipeDescription.setText(recipe.getDescription());
            if(recipe.getIngredients() != null) {
                for (Ingredient ingredient : recipe.getIngredients()) {
                    TextView name = new TextView(this);
                    name.setText(ingredient.getQuantity() + " " + ingredient.getQuantityUnit() + " " + ingredient.getName());
                    ingredientslayout.addView(name);
                }
            }
        });
    }

    @SuppressLint("SetTextI18n")
    public void clickRemovePersonButton(View view) {
        int num = Integer.parseInt((String) recipeQuantity.getText());
        if (num > 1){
            num--;

            recipeQuantity.setText(num + "");
            for (int i = 0; i < ingredientslayout.getChildCount(); i++){
                String tempText = ((TextView) ingredientslayout.getChildAt(i)).getText().toString();
                String quantity = tempText.split(" ")[0];
                String textWithoutQuantity = tempText.replaceAll(quantity, "");
                try {
                    float newQuantity = (Float.parseFloat(quantity) / (num + 1) * num);
                    String newQuantityString = Float.toString(newQuantity);

                    if (newQuantityString.charAt(newQuantityString.length()-1) == '0'){
                        ((TextView) ingredientslayout.getChildAt(i)).setText(Math.round(newQuantity) + textWithoutQuantity);
                    } else{
                        ((TextView) ingredientslayout.getChildAt(i)).setText(newQuantity + textWithoutQuantity);
                    }
                } catch (Exception ignored){}

            }
        }
    }

    @SuppressLint("SetTextI18n")
    public void clickAddPersonButton(View view) {
        LinearLayout ingredientsTable = findViewById(R.id.ingredientsLayout);
        int num = Integer.parseInt((String) recipeQuantity.getText());
        num++;
        recipeQuantity.setText(num + "");

        for (int i = 0; i < ingredientslayout.getChildCount(); i++){
            String tempText = ((TextView) ingredientslayout.getChildAt(i)).getText().toString();
            String quantity = tempText.split(" ")[0];
            String textWithiutQuantity = tempText.replaceAll(quantity, "");
            try {
                float newQuantity = (Float.parseFloat(quantity) / (num - 1) * num);
                String newQuantityString = Float.toString(newQuantity);

                if (newQuantityString.charAt(newQuantityString.length()-1) == '0'){
                    ((TextView) ingredientslayout.getChildAt(i)).setText(Math.round(newQuantity) + textWithiutQuantity);
                } else{
                    ((TextView) ingredientslayout.getChildAt(i)).setText(newQuantity + textWithiutQuantity);
                }
            } catch (Exception ignored){}
        }
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
}