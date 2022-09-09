package szakdoga.haztartas.home;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.firestore.DocumentSnapshot;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

import java.io.Serializable;
import java.util.List;

import szakdoga.haztartas.R;
import szakdoga.haztartas.UserSettingsActivity;
import szakdoga.haztartas.firebaseAuthentication.FirebaseAuthHelper;
import szakdoga.haztartas.firestore.DbHelper;
import szakdoga.haztartas.models.Pantry;
import szakdoga.haztartas.pantry.CaptureAct;
import szakdoga.haztartas.pantry.ModifyPantryItem;
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

    ActivityResultLauncher<ScanOptions> barLauncher = registerForActivityResult(new ScanContract(), result -> {
        if(result.getContents() != null) {
            dbHelper.getHomeCollection().document(homeId).collection("Pantry").whereArrayContains("barcodes", result.getContents()).get().addOnSuccessListener(queryDocumentSnapshots -> {
                if (queryDocumentSnapshots.getDocuments().size() == 0){
                    Toast.makeText(this, "Nincs ilyen vonalkódú hozzávaló!", Toast.LENGTH_SHORT).show();
                    return;
                }

                DocumentSnapshot data = queryDocumentSnapshots.getDocuments().get(0);
                Pantry pantry = new Pantry(
                        data.getId(),
                        data.get("name").toString(),
                        Double.parseDouble(data.get("quantity").toString()),
                        data.get("quantityUnit").toString(),
                        data.get("where").toString(),
                        (List<String>) data.get("barcodes")
                );

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle(pantry.getName());

                LinearLayout row = new LinearLayout(this);
                row.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                row.setOrientation(LinearLayout.HORIZONTAL);
                row.setGravity(Gravity.CENTER);


                ImageButton remove = new ImageButton(this);

                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                layoutParams.rightMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5, this.getResources().getDisplayMetrics());

                remove.setLayoutParams(layoutParams);
                remove.setImageResource(R.drawable.icon_remove);
                remove.setBackgroundResource(R.color.transparent);
                remove.setScaleType(ImageView.ScaleType.FIT_XY);
                remove.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        EditText value = (EditText) ((LinearLayout)view.getParent()).getChildAt(1);
                        if (Double.parseDouble(value.getText().toString()) >= 1)
                            value.setText(Double.parseDouble(value.getText().toString())-1+"");
                    }
                });
                row.addView(remove);


                EditText quantityEditText = new EditText(this);
                quantityEditText.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL);
                quantityEditText.setText(Double.toString(pantry.getQuantity()));
                row.addView(quantityEditText);


                TextView quantityUnitText = new TextView(this);
                quantityUnitText.setText(pantry.getQuantityUnit());

                LinearLayout.LayoutParams layoutParamsTextView = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                layoutParamsTextView.rightMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5, this.getResources().getDisplayMetrics());
                layoutParamsTextView.leftMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, this.getResources().getDisplayMetrics());

                quantityUnitText.setLayoutParams(layoutParamsTextView);

                row.addView(quantityUnitText);


                ImageButton add = new ImageButton(this);
                add.setImageResource(R.drawable.icon_add);
                add.setBackgroundResource(R.color.transparent);
                add.setScaleType(ImageView.ScaleType.FIT_XY);
                add.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        EditText value = (EditText) ((LinearLayout)view.getParent()).getChildAt(1);
                        value.setText(Double.parseDouble(value.getText().toString())+1+"");
                    }
                });
                row.addView(add);

                builder.setView(row);

                builder.setPositiveButton("Mentés", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        DbHelper dbHelper = new DbHelper();
                        dbHelper.getHomeCollection().document(homeId).collection("Pantry").document(pantry.getId()).update("quantity", Double.parseDouble(quantityEditText.getText().toString()));
                        Toast.makeText(HomeActivity.this, "Sikeres Mentés", Toast.LENGTH_SHORT).show();
                    }
                });

                builder.setNegativeButton("Mégsem", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });

                builder.setNeutralButton("Módosítás", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent modifyIntent = new Intent(HomeActivity.this, ModifyPantryItem.class);
                        modifyIntent.putExtra("pantry", (Serializable) pantry);
                        modifyIntent.putExtra("userId", userId);
                        modifyIntent.putExtra("homeId", homeId);
                        startActivity(modifyIntent);
                    }
                });

                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            });
        }
    });

    public void barcodeScanner() {
        ScanOptions options = new ScanOptions();
        options.setPrompt("Tartsa a kamerát a vonalkód elé.");
        options.setBeepEnabled(false);
        options.setOrientationLocked(true);
        options.setCaptureActivity(CaptureAct.class);
        barLauncher.launch(options);
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

            case R.id.barcodeScanner:
                barcodeScanner();
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

            case R.id.userSettings:
                Intent userSettingsIntent = new Intent(this, UserSettingsActivity.class);
                userSettingsIntent.putExtra("userId", userId);
                startActivity(userSettingsIntent);
                return true;

            case android.R.id.home:
                onBackPressed();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}