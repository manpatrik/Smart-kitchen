package szakdoga.haztartas.pantry;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

import szakdoga.haztartas.R;
import szakdoga.haztartas.models.Pantry;

public class PantryListActivity extends AppCompatActivity {

    private RecyclerView foodsRecylerView;
    private PantryItemAdapter pantryItemAdapter;
    private List<Pantry> pantries = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_pantry);
        getSupportActionBar().setTitle(this.getIntent().getStringExtra("where"));

        foodsRecylerView = findViewById(R.id.foodsRecylerView);
        foodsRecylerView.setLayoutManager(new GridLayoutManager(this, 1));

        pantries.add(new Pantry(0, "Liszt", 10, "kg"));
        pantries.add(new Pantry(1, "Alma", 5, "kg"));
        pantries.add(new Pantry(1, "Tej", 2, "l"));

        pantryItemAdapter = new PantryItemAdapter(this, pantries);
        foodsRecylerView.setAdapter(pantryItemAdapter);
    }
}