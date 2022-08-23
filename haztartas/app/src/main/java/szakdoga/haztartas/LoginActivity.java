package szakdoga.haztartas;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import szakdoga.haztartas.firebaseAuthentication.FirebaseAuthHelper;
import szakdoga.haztartas.homesList.HomesListActivity;

public class LoginActivity extends AppCompatActivity {

    private EditText emailEditText;
    private EditText passwordEditText;

    private FirebaseAuthHelper firebaseAuthHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);

        firebaseAuthHelper = new FirebaseAuthHelper();
    }

    @Override
    protected void onResume() {
        super.onResume();
        emailEditText.setText("");
        passwordEditText.setText("");
    }

    public void login(View view) {
        String email = emailEditText.getText().toString();
        String password = passwordEditText.getText().toString();
        if (email.length() != 0) {
            if (!email.contains("@") || !email.contains(".") || email.length() < 2 || password.length() < 8) {
                Toast.makeText(this, "Helytelen email cím vagy jelszó!", Toast.LENGTH_LONG).show();
                return;
            }

            firebaseAuthHelper.login(email, password, this);
        } else {
            firebaseAuthHelper.login("manpatrik@outlook.hu", "alma1234", this);
        }
    }

    public void register(View view) {
        String email = emailEditText.getText().toString();
        Intent registerIntent = new Intent(this, RegisterActivity.class);
        registerIntent.putExtra("email", email);
        this.startActivity(registerIntent);
    }
}