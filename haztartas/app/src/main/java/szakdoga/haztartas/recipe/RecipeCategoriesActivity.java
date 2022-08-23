package szakdoga.haztartas.recipe;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import szakdoga.haztartas.R;
import szakdoga.haztartas.firebaseAuthentication.FirebaseAuthHelper;

public class RecipeCategoriesActivity extends AppCompatActivity {
    private String userId;
    private String homeId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_categories);

        userId = getIntent().getStringExtra("userId");
        homeId = getIntent().getStringExtra("homeId");

        this.setTitle("Recept kategóriák");
    }

    public void clickSoupButton(View view) {
        Intent recipesListIntent = new Intent(this, RecipesListActivity.class);
        recipesListIntent.putExtra("category", "leves");
        recipesListIntent.putExtra("userId", userId);
        recipesListIntent.putExtra("homeId", homeId);
        startActivity(recipesListIntent);
    }

    public void clickMainFoodButton(View view) {
        Intent recipesListIntent = new Intent(this, RecipesListActivity.class);
        recipesListIntent.putExtra("category", "főétel");
        recipesListIntent.putExtra("userId", userId);
        recipesListIntent.putExtra("homeId", homeId);
        startActivity(recipesListIntent);
    }

    public void clickDessertButton(View view) {
        Intent recipesListIntent = new Intent(this, RecipesListActivity.class);
        recipesListIntent.putExtra("category", "desszert");
        recipesListIntent.putExtra("userId", userId);
        recipesListIntent.putExtra("homeId", homeId);
        startActivity(recipesListIntent);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.new_recipes_menu, menu);

        //vissza gomb
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.new_recipe:
                Intent newRecipe = new Intent(this, NewRecipeActivity.class);
                newRecipe.putExtra("userId", userId);
                newRecipe.putExtra("homeId", homeId);
                startActivity(newRecipe);
                return true;

            case R.id.logOut:
                FirebaseAuthHelper firebaseAuthHelper = new FirebaseAuthHelper();
                firebaseAuthHelper.logOut(this);
                return true;

            case android.R.id.home:
                onBackPressed();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}