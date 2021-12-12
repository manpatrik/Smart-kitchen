package szakdoga.haztartas;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageButton;

import szakdoga.haztartas.pantry.PantryListActivity;
import szakdoga.haztartas.recipe.RecipeCategoriesActivity;

public class homeActivity extends AppCompatActivity {
    private ImageButton freezerButton;
    private ImageButton fridgeButton;
    private ImageButton cupboardButton;
    private ImageButton freezerOpenButton;
    private ImageButton fridgeOpenButton;
    private ImageButton cupboardOpenButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        freezerButton = findViewById(R.id.freezerButton);
        fridgeButton = findViewById(R.id.fridgeButton);
        cupboardButton = findViewById(R.id.cupboardButton);
        freezerOpenButton = findViewById(R.id.freezerOpenButton);
        fridgeOpenButton = findViewById(R.id.fridgeOpenButton);
        cupboardOpenButton = findViewById(R.id.cupboardOpenButton);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onResume(){
        super.onResume();
        fridgeButton.setVisibility(View.VISIBLE);
        fridgeOpenButton.setVisibility(View.INVISIBLE);
        freezerButton.setVisibility(View.VISIBLE);
        freezerOpenButton.setVisibility(View.INVISIBLE);
        cupboardButton.setVisibility(View.VISIBLE);
        cupboardOpenButton.setVisibility(View.INVISIBLE);
    }

    public void clickFridgeButton(View view) throws InterruptedException {
        fridgeButton.setVisibility(View.INVISIBLE);
        fridgeOpenButton.setVisibility(View.VISIBLE);
        Intent pantryListIntent = new Intent(this, PantryListActivity.class);
        pantryListIntent.putExtra("where", "Hűtő");
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(pantryListIntent);
            }
        }, 300);
    }

    public void clickCupboardButton(View view) {
        cupboardButton.setVisibility(View.INVISIBLE);
        cupboardOpenButton.setVisibility(View.VISIBLE);
        Intent pantryListIntent = new Intent(this, PantryListActivity.class);
        pantryListIntent.putExtra("where", "Szekrény");
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(pantryListIntent);
            }
        }, 300);
    }

    public void clickVegetablesButton(View view) {
        Intent pantryListIntent = new Intent(this, PantryListActivity.class);
        pantryListIntent.putExtra("where", "Zöldségek/gyümülcsök");
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(pantryListIntent);
            }
        }, 300);
    }

    public void clickFreezerButton(View view) {
        freezerButton.setVisibility(View.INVISIBLE);
        freezerOpenButton.setVisibility(View.VISIBLE);
        Intent pantryListIntent = new Intent(this, PantryListActivity.class);
        pantryListIntent.putExtra("where", "Fagyasztó");
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(pantryListIntent);
            }
        }, 300);
    }

    public void clickRecipeButton(View view) {
        Intent recipesListIntent = new Intent(this, RecipeCategoriesActivity.class);
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(recipesListIntent);
            }
        }, 300);
    }
}