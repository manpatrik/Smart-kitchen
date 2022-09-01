package szakdoga.haztartas.pantry;

import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
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
import java.util.ArrayList;
import java.util.List;

import szakdoga.haztartas.HomeActivity;
import szakdoga.haztartas.R;
import szakdoga.haztartas.firebaseAuthentication.FirebaseAuthHelper;
import szakdoga.haztartas.firestore.DbHelper;
import szakdoga.haztartas.models.Pantry;

public class PantryListActivity extends AppCompatActivity {

    private RecyclerView foodsRecylerView;
    private PantryItemAdapter pantryItemAdapter;
    private List<Pantry> pantries = new ArrayList<>();
    private String userId;
    private String homeId;
    private String whereCategory;

    private FirebaseAuthHelper firebaseAuthHelper;
    private DbHelper dbHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_pantry);

        firebaseAuthHelper = new FirebaseAuthHelper();
        dbHelper = new DbHelper();

        whereCategory = getIntent().getStringExtra("where");
        getSupportActionBar().setTitle(whereCategory);

        userId = getIntent().getStringExtra("userId");
        homeId = getIntent().getStringExtra("homeId");

        foodsRecylerView = findViewById(R.id.foodsRecylerView);
        foodsRecylerView.setLayoutManager(new GridLayoutManager(this, 1));

        pantryItemAdapter = new PantryItemAdapter(this, pantries, homeId, userId);
        foodsRecylerView.setAdapter(pantryItemAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        firebaseAuthHelper.isAuthUser(userId, this);
        getPantries();
    }

    @SuppressLint("NotifyDataSetChanged")
    private void getPantries() {
        pantries.clear();
        dbHelper.getHomeCollection().document(homeId).collection("Pantry").whereEqualTo("where", whereCategory).get().addOnSuccessListener(queryDocumentSnapshots -> {
            for (DocumentSnapshot data : queryDocumentSnapshots){
                Pantry pantry = new Pantry(
                        data.getId(),
                        data.get("name").toString(),
                        Double.parseDouble(data.get("quantity").toString()),
                        data.get("quantityUnit").toString(),
                        data.get("where").toString(),
                        (List<String>) data.get("barcodes")
                );
                pantries.add(pantry);
            }
            pantryItemAdapter.notifyDataSetChanged();
        });
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
                        Toast.makeText(PantryListActivity.this, "Sikeres Mentés", Toast.LENGTH_SHORT).show();
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
                        Intent modifyIntent = new Intent(PantryListActivity.this, ModifyPantryItem.class);
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

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.pantry_list_menu, menu);

        //vissza gomb
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.newPantryItem:
                Intent newPantryItem = new Intent(this, NewPantryItem.class);
                newPantryItem.putExtra("userId", userId);
                newPantryItem.putExtra("homeId", homeId);
                newPantryItem.putExtra("where", whereCategory);
                startActivity(newPantryItem);
                return true;

            case R.id.barcodeScanner:
                barcodeScanner();
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