/*
 * vonalkód olvasás:
 * https://www.youtube.com/watch?v=jtT60yFPelI&t=88s&ab_channel=CamboTutorial
 * https://github.com/journeyapps/zxing-android-embedded
 * */

package szakdoga.haztartas.pantry;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
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
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.DocumentSnapshot;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import szakdoga.haztartas.R;
import szakdoga.haztartas.firebaseAuthentication.FirebaseAuthHelper;
import szakdoga.haztartas.firestore.DbHelper;
import szakdoga.haztartas.models.Pantry;

public class NewPantryItem extends AppCompatActivity {

    EditText name;
    EditText quantity;
    Spinner quantityUnit;
    Spinner whereSpinner;
    LinearLayout barcodesLayout;

    private String userId;
    private String homeId;

    private FirebaseAuthHelper firebaseAuthHelper;
    private DbHelper dbHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_pantry_item);
        setTitle("Új hozzávaló");

        userId = getIntent().getStringExtra("userId");
        homeId = getIntent().getStringExtra("homeId");
        firebaseAuthHelper = new FirebaseAuthHelper();
        dbHelper = new DbHelper();

        name = findViewById(R.id.newPantryItemName);
        quantity = findViewById(R.id.newPantryItemQuantity);
        quantityUnit = findViewById(R.id.newPantryItemQuantityUnitSpinner);
        barcodesLayout = findViewById(R.id.newPantryItemBarcodeslinearLayout);
        whereSpinner = findViewById(R.id.newPantryItemWhereSpinner);

        setWhereSpinner(getIntent().getStringExtra("where"));
    }

    private void setWhereSpinner(String where) {
        final String[] quantityUnits = getResources().getStringArray(R.array.pantry_categories);
        for (int i = 0; i < quantityUnits.length; i++){
            System.out.println(where +" == "+quantityUnits[i]);
            if(quantityUnits[i].equals(where)){
                whereSpinner.setSelection(i);
                break;
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        firebaseAuthHelper.isAuthUser(userId, this);
    }

    ActivityResultLauncher<ScanOptions> barLauncher = registerForActivityResult(new ScanContract(), result -> {
        if(result.getContents() != null){
            for(int i = 0; i < barcodesLayout.getChildCount(); i++){
                if (((TextView)((LinearLayout) barcodesLayout.getChildAt(i)).getChildAt(0)).getText().toString().equals(result.getContents())){
                    Toast.makeText(this, "Már felvette a vonalkódot!", Toast.LENGTH_SHORT).show();
                    return;
                }
            }

            dbHelper.getHomeCollection().document(homeId).collection("Pantry").whereArrayContains("barcodes", result.getContents()).get().addOnSuccessListener(queryDocumentSnapshots -> {
                for (DocumentSnapshot data : queryDocumentSnapshots){
                    System.out.println(data.get("name"));
                }
                if (queryDocumentSnapshots.size() > 0){
                    DocumentSnapshot data = queryDocumentSnapshots.getDocuments().get(0);
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("A vonalkód már szerepel az adatbázisban");
                    builder.setMessage("Szeretné módosítani ezt a hozzávalót: "+ data.get("name") +"?");

                    builder.setPositiveButton(data.get("name") + " módosítása", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            finish();
                            Pantry pantry = new Pantry(
                                    data.getId(),
                                    data.get("name").toString(),
                                    Double.parseDouble(data.get("quantity").toString()),
                                    data.get("quantityUnit").toString(),
                                    data.get("where").toString(),
                                    (List<String>) data.get("barcodes")
                            );
                            Intent intent = new Intent(NewPantryItem.this, ModifyPantryItem.class);
                            intent.putExtra("pantry", (Serializable) pantry);
                            intent.putExtra("userId", userId);
                            intent.putExtra("homeId", homeId);
                            startActivity(intent);
                        }
                    });

                    builder.setNegativeButton("Vissza", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.cancel();
                        }
                    });

                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();

                } else {
                    LinearLayout row = new LinearLayout(this);

                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    layoutParams.topMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5, getResources().getDisplayMetrics());
                    row.setLayoutParams(layoutParams);

                    TextView textView = new TextView(this);
                    textView.setText(result.getContents());
                    row.addView(textView);

                    ImageButton imageButton = new ImageButton(this);
                    imageButton.setImageResource(R.drawable.icon_remove);
                    imageButton.setScaleType(ImageView.ScaleType.FIT_XY);
                    imageButton.setBackgroundResource(R.color.transparent);

                    imageButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            barcodesLayout.removeView((View) view.getParent());
                        }
                    });

                    row.addView(imageButton);

                    row.setGravity(Gravity.CENTER_VERTICAL);
                    barcodesLayout.addView(row);
                }
            });
        }
    });

    public void barcodeScanner(View view) {
        ScanOptions options = new ScanOptions();
        options.setPrompt("Tartsa a kamerát a vonalkód elé.");
        options.setBeepEnabled(false);
        options.setOrientationLocked(true);
        options.setCaptureActivity(CaptureAct.class);
        barLauncher.launch(options);
    }

    public void saveNewPantryItem(View view) {
        String error = null;

        if(name.getText().length() == 0 ){
            error = "Nem adott meg nevet!";
        } else if(quantity.getText().length() == 0){
            error = "Nem adott meg mennyiséget!";
        } else {
            List<String> barcodes = new ArrayList<>();
            for (int i = 0; i < barcodesLayout.getChildCount(); i++){
                barcodes.add(((TextView)((LinearLayout)barcodesLayout.getChildAt(i)).getChildAt(0)).getText().toString());
            }
            Pantry pantry = new Pantry(
                    null,
                    name.getText().toString(),
                    Double.parseDouble(quantity.getText().toString()),
                    quantityUnit.getSelectedItem().toString(),
                    whereSpinner.getSelectedItem().toString(),
                    barcodes
            );
            dbHelper.getHomeCollection().document(homeId).collection("Pantry").whereEqualTo("name", name.getText().toString()).get().addOnSuccessListener(queryDocumentSnapshots -> {
                for (DocumentSnapshot data :queryDocumentSnapshots) {
                    if (data.get("where").equals(whereSpinner.getSelectedItem().toString())) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(this);
                        builder.setTitle("Létező név!");
                        builder.setMessage("Be szeretné tölteni a(z) " + data.get("name") + " adatait?");

                        builder.setNegativeButton("Nem", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                name.setText("");
                                dialogInterface.cancel();
                            }
                        });
                        builder.setPositiveButton("Igen", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                finish();
                                Pantry pantry = new Pantry(
                                        data.getId(),
                                        data.get("name").toString(),
                                        Double.parseDouble(data.get("quantity").toString()),
                                        data.get("quantityUnit").toString(),
                                        data.get("where").toString(),
                                        (List<String>) data.get("barcodes")
                                );
                                Intent intent = new Intent(NewPantryItem.this, ModifyPantryItem.class);
                                intent.putExtra("pantry", (Serializable) pantry);
                                intent.putExtra("userId", userId);
                                intent.putExtra("homeId", homeId);
                                startActivity(intent);
                            }
                        });

                        AlertDialog alertDialog = builder.create();
                        alertDialog.show();
                        return;
                    }
                }
                dbHelper.getHomeCollection().document(homeId).collection("Pantry").add(pantry).addOnSuccessListener(result -> {
                    dbHelper.getHomeCollection().document(homeId).collection("Pantry").document(result.getId()).update("id", result.getId());
                    finish();
                    Toast.makeText(this, "Sikeres mentés!", Toast.LENGTH_SHORT).show();
                });
            });
        }
        if (error != null){
            Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.sample_logout_menu, menu);

        //vissza gomb
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

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