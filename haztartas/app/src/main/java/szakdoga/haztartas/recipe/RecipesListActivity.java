package szakdoga.haztartas.recipe;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import szakdoga.haztartas.R;
import szakdoga.haztartas.firebaseAuthentication.FirebaseAuthHelper;
import szakdoga.haztartas.firestore.DbHelper;
import szakdoga.haztartas.models.Ingredient;
import szakdoga.haztartas.models.Recipe;

public class RecipesListActivity extends AppCompatActivity {
    private RecyclerView recipesRecylerView;
    private RecipeItemAdapter recipeItemAdapter;
    private List<Recipe> recipes = new ArrayList<>();
    private String category;

    private FirebaseAuthHelper firebaseAuthHelper;
    private DbHelper dbHelper;

    private String userId;
    private String homeId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipes_list);

        category = this.getIntent().getStringExtra("category");
        getSupportActionBar().setTitle(category+"ek");

        userId = getIntent().getStringExtra("userId");
        homeId = getIntent().getStringExtra("homeId");

        firebaseAuthHelper = new FirebaseAuthHelper();
        dbHelper = new DbHelper();

        recipesRecylerView = findViewById(R.id.recipesRecylerView);
        recipesRecylerView.setLayoutManager(new GridLayoutManager(this, 1));

        recipeItemAdapter = new RecipeItemAdapter(this, recipes, userId, homeId);
        recipesRecylerView.setAdapter(recipeItemAdapter);
    }

    protected void onResume() {
        super.onResume();
        firebaseAuthHelper.isAuthUser(userId, this);
        getRecipes();
    }

    private void getRecipes(){
        recipes.clear();
        dbHelper.getHomeCollection().document(homeId).collection("Recipes").whereEqualTo("category", category).get().addOnSuccessListener(queryDocumentSnapshots -> {
            for (DocumentSnapshot data: queryDocumentSnapshots){
                recipes.add(data.toObject(Recipe.class));
            }
            recipeItemAdapter.notifyDataSetChanged();
        });
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
                newRecipe.putExtra("category", category);
                startActivity(newRecipe);
                return true;

            case android.R.id.home:
                onBackPressed();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}