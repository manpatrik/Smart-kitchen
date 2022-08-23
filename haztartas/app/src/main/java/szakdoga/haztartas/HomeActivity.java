package szakdoga.haztartas;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.Constraints;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import szakdoga.haztartas.firebaseAuthentication.FirebaseAuthHelper;
import szakdoga.haztartas.firestore.DbHelper;
import szakdoga.haztartas.pantry.PantryListActivity;
import szakdoga.haztartas.recipe.RecipeCategoriesActivity;

public class HomeActivity extends AppCompatActivity {
    private ImageButton freezerButton;
    private ImageButton fridgeButton;
    private ImageButton cupboardButton;
    private ImageButton freezerOpenButton;
    private ImageButton fridgeOpenButton;
    private ImageButton cupboardOpenButton;
    private ImageView helpImage1;
    private ImageView helpImage2;

    private String userId;
    private String homeId;

    private boolean help = false;

    private FirebaseAuthHelper firebaseAuthHelper;
    private DbHelper dbHelper;

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
        helpImage1 = findViewById(R.id.helpImage1);
        helpImage2 = findViewById(R.id.helpImage2);

        userId = getIntent().getStringExtra("userId");
        homeId = getIntent().getStringExtra("homeId");

        firebaseAuthHelper = new FirebaseAuthHelper();
        dbHelper = new DbHelper();

        System.out.println("HomeId: " + getIntent().getStringExtra("homeId"));
        System.out.println("UserId: " + userId);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onResume(){
        super.onResume();
        helpImage1.setVisibility(View.GONE);
        helpImage2.setVisibility(View.GONE);
        help = false;

        firebaseAuthHelper.isAuthUser(userId, this);
        fridgeButton.setVisibility(View.VISIBLE);
        fridgeOpenButton.setVisibility(View.INVISIBLE);
        freezerButton.setVisibility(View.VISIBLE);
        freezerOpenButton.setVisibility(View.INVISIBLE);
        cupboardButton.setVisibility(View.VISIBLE);
        cupboardOpenButton.setVisibility(View.INVISIBLE);

        dbHelper.getHomeCollection().document(homeId).get().addOnSuccessListener(quary ->{
            try {
                this.setTitle(quary.get("name").toString());
            } catch (Exception e) {
                finish();
            }

        });
    }

    public void clickFridgeButton(View view) throws InterruptedException {
        fridgeButton.setVisibility(View.INVISIBLE);
        fridgeOpenButton.setVisibility(View.VISIBLE);
        Intent pantryListIntent = new Intent(this, PantryListActivity.class);
        pantryListIntent.putExtra("userId", userId);
        pantryListIntent.putExtra("homeId", homeId);
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
        pantryListIntent.putExtra("userId", userId);
        pantryListIntent.putExtra("homeId", homeId);
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
        pantryListIntent.putExtra("userId", userId);
        pantryListIntent.putExtra("homeId", homeId);
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
        pantryListIntent.putExtra("userId", userId);
        pantryListIntent.putExtra("homeId", homeId);
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
        recipesListIntent.putExtra("userId", userId);
        recipesListIntent.putExtra("homeId", homeId);
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(recipesListIntent);
            }
        }, 300);
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.home_menu, menu);

        //vissza gomb
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.settings:
                Intent settingsIntent = new Intent(this, Settings.class);
                settingsIntent.putExtra("userId", userId);
                settingsIntent.putExtra("homeId", homeId);
                startActivity(settingsIntent);
                return true;

            case R.id.logOut:
                firebaseAuthHelper.logOut(this);
                return true;

            case R.id.help:
                if (help){
                    helpImage1.setVisibility(View.GONE);
                    helpImage2.setVisibility(View.GONE);
                    help = false;
                }else{
                    helpImage1.setVisibility(View.VISIBLE);
                    helpImage2.setVisibility(View.VISIBLE);
                    help = true;
                }
                return true;

            case android.R.id.home:
                onBackPressed();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}