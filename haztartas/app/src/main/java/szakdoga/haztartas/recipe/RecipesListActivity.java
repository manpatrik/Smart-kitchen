package szakdoga.haztartas.recipe;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

import szakdoga.haztartas.R;
import szakdoga.haztartas.models.Pantry;
import szakdoga.haztartas.models.Recipe;
import szakdoga.haztartas.pantry.PantryItemAdapter;

public class RecipesListActivity extends AppCompatActivity {
    private RecyclerView recipesRecylerView;
    private RecipeItemAdapter recipeItemAdapter;
    private List<Recipe> recipes = new ArrayList<>();
    private String category;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipes_list);

        category = this.getIntent().getStringExtra("category");
        getSupportActionBar().setTitle(category);

        recipesRecylerView = findViewById(R.id.recipesRecylerView);
        recipesRecylerView.setLayoutManager(new GridLayoutManager(this, 1));

        if (category.equals("Leves")){
            recipes.add(new Recipe(1, "Paradicsom leves", 30));
            recipes.add(new Recipe(1, "Hús leves", 90));
            recipes.add(new Recipe(1, "Tojás leves", 20));
            recipes.add(new Recipe(1, "Fokhagyma krémleves", 30));
        } else if (category.equals("Főétel")){
            recipes.add(new Recipe(1, "Spagetti", 60));
            recipes.add(new Recipe(1, "Rántott hús", 45));
            recipes.add(new Recipe(1, "Bakonyi sertésborda", 100));
            recipes.add(new Recipe(1, "Marhalábszár pörkölt", 180));
        } else if (category.equals("Desszert")){
            recipes.add(new Recipe(1, "Tiramisu", 30));
            recipes.add(new Recipe(1, "Piskóta", 60));
            recipes.add(new Recipe(1, "Muffin", 45));
            recipes.add(new Recipe(1, "Mézeskalács", 120));
        }

        recipeItemAdapter = new RecipeItemAdapter(this, recipes);
        recipesRecylerView.setAdapter(recipeItemAdapter);
    }
}