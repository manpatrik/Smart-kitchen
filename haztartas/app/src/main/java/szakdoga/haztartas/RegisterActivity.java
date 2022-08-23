package szakdoga.haztartas;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import szakdoga.haztartas.firebaseAuthentication.FirebaseAuthHelper;

public class RegisterActivity extends AppCompatActivity {

    private EditText emailEditText;
    private EditText passwordEditText;
    private EditText passwordAgainEditText;

    private FirebaseAuthHelper firebaseAuthHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        setTitle("Regisztráció");

        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        passwordAgainEditText = findViewById(R.id.passwordAgainEditText);

        emailEditText.setText(this.getIntent().getStringExtra("email"));

        firebaseAuthHelper = new FirebaseAuthHelper();
    }

    public void register(View view) {
        String email = emailEditText.getText().toString();
        String password = passwordEditText.getText().toString();
        String passwordAgain = passwordAgainEditText.getText().toString();

        if (password.length()<8){
            Toast.makeText(this, "A jelszónak legalább 8 hosszúnak kell lennie!", Toast.LENGTH_LONG).show();
            return;
        }

        if (!password.equals(passwordAgain)){
            Toast.makeText(this, "A két jelszó nem egyezik", Toast.LENGTH_LONG).show();
            return;
        }
        if (!email.contains("@") || !email.contains(".") || email.length()<4){
            Toast.makeText(this, "Helytelen email cím", Toast.LENGTH_LONG).show();
            return;
        }

        firebaseAuthHelper.register(email, password, this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        //vissza gomb
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}