/*
 * vonalkód olvasás:
 * https://www.youtube.com/watch?v=jtT60yFPelI&t=88s&ab_channel=CamboTutorial
 * https://github.com/journeyapps/zxing-android-embedded
 * */

package szakdoga.haztartas.pantry;

import android.content.DialogInterface;
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

import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

import szakdoga.haztartas.R;
import szakdoga.haztartas.firebaseAuthentication.FirebaseAuthHelper;
import szakdoga.haztartas.firestore.DbHelper;

public class NewPantryItem extends AppCompatActivity {

    EditText name;
    EditText quantity;
    Spinner quantityUnit;
    LinearLayout barcodesLayout;

    private String userId;
    private String homeId;

    private FirebaseAuthHelper firebaseAuthHelper;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_pantry_item);
        setTitle("Új hozzávaló");

        userId = getIntent().getStringExtra("userId");
        homeId = getIntent().getStringExtra("homeId");
        firebaseAuthHelper = new FirebaseAuthHelper();

        name = findViewById(R.id.newPantryItemName);
        quantity = findViewById(R.id.newPantryItemQuantity);
        quantityUnit = findViewById(R.id.newPantryItemQuantityUnitSpinner);
        barcodesLayout = findViewById(R.id.newPantryItemBarcodeslinearLayout);
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

            LinearLayout row = new LinearLayout(this);

            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.topMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5, getResources().getDisplayMetrics());
            row.setLayoutParams(layoutParams);

            TextView textView = new TextView(this);
            textView.setText(result.getContents());
            row.addView(textView);

            ImageButton imageButton = new ImageButton(this);
            imageButton.setImageResource(R.drawable.remove);
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
            DbHelper dbHelper = new DbHelper();
            //dbHelper.getHomeCollection().document(homeId).collection("Pantry").
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