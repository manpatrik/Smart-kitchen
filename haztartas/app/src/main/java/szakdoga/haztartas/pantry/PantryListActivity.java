package szakdoga.haztartas.pantry;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

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