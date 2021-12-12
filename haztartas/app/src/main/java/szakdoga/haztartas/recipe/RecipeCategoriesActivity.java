package szakdoga.haztartas.recipe;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import szakdoga.haztartas.R;

public class RecipeCategoriesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_categories);
    }

    public void clickSoupButton(View view) {
        Intent recipesListIntent = new Intent(this, RecipesListActivity.class);
        recipesListIntent.putExtra("category", "Leves");
        startActivity(recipesListIntent);
    }

    public void clickMainFoodButton(View view) {
        Intent recipesListIntent = new Intent(this, RecipesListActivity.class);
        recipesListIntent.putExtra("category", "Főétel");
        startActivity(recipesListIntent);
    }

    public void clickDessertButton(View view) {
        Intent recipesListIntent = new Intent(this, RecipesListActivity.class);
        recipesListIntent.putExtra("category", "Desszert");
        startActivity(recipesListIntent);
    }
}