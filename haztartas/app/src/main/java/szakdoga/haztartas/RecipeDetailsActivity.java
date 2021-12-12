package szakdoga.haztartas;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.inputmethodservice.Keyboard;
import android.os.Bundle;
import android.provider.FontsContract;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.ArrayList;

public class RecipeDetailsActivity extends AppCompatActivity {
    private LinearLayout ingredientslayout;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_details);
        ingredientslayout = findViewById(R.id.ingredientslayout);
        for (int i = 0; i < 5; i++){
            TextView name = new TextView(this);
            name.setText("100 g vaj");
            ingredientslayout.addView(name);
        }
    }

    @SuppressLint("SetTextI18n")
    public void clickRemovePersonButton(View view) {
        TextView howManyPerson = findViewById(R.id.HowManyPerson);
        LinearLayout ingredientsTable = findViewById(R.id.ingredientslayout);
        int num = Integer.parseInt((String) howManyPerson.getText());
        if (num > 1){
            num--;
        }
        howManyPerson.setText(num + "");
        for (int i = 0; i < ingredientslayout.getChildCount(); i++){
            ((TextView) ingredientslayout.getChildAt(i)).setText("200");
        }
    }

    public void clickAddPersonButton(View view) {
        TextView howManyPerson = findViewById(R.id.HowManyPerson);
        LinearLayout ingredientsTable = findViewById(R.id.ingredientslayout);
        int num = Integer.parseInt((String) howManyPerson.getText());
        num++;
        howManyPerson.setText(num + "");
    }
}