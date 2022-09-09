package szakdoga.haztartas;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.messaging.FirebaseMessaging;

import szakdoga.haztartas.firebaseAuthentication.FirebaseAuthHelper;
import szakdoga.haztartas.firestore.DbHelper;
import szakdoga.haztartas.home.Settings;
import szakdoga.haztartas.models.User;

public class UserSettingsActivity extends AppCompatActivity {

    private String userId;
    private FirebaseAuthHelper firebaseAuthHelper;
    private DbHelper dbHelper;
    String email;
    User user;
    String mobileToken;

    private TextView emailTextView;
    private EditText oldPasswordEditText;
    private EditText passwordEditText;
    private EditText passwordAgainEditText;
    private Button updatePasswordFormButton;
    private Button notificationEnableButton;
    private LinearLayout updatePasswordLayout;
    private LinearLayout notificationTokensLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_settings);

        setTitle("Fiók beállítások");

        userId = getIntent().getStringExtra("userId");
        firebaseAuthHelper = new FirebaseAuthHelper();
        dbHelper = new DbHelper();
        email = firebaseAuthHelper.getFirebaseAuth().getCurrentUser().getEmail();

        emailTextView = findViewById(R.id.emailTextView);
        oldPasswordEditText = findViewById(R.id.oldPasswordEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        passwordAgainEditText = findViewById(R.id.passwordAgainEditText);
        updatePasswordFormButton = findViewById(R.id.updatePasswordFormButton);
        notificationEnableButton = findViewById(R.id.notificationEnableButton);
        updatePasswordLayout = findViewById(R.id.updatePasswordLayout);
        notificationTokensLayout = findViewById(R.id.notificationTokensLayout);

        emailTextView.setText(email);
        updatePasswordLayout.setVisibility(View.GONE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        firebaseAuthHelper.isAuthUser(userId, this);

        dbHelper.getUsersCollection().document(userId).get().addOnSuccessListener(data -> {
            user = data.toObject(User.class);
            setNotificationTokensLayout();

            FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    mobileToken = task.getResult().toString();
                    if (user.isHaveToken(mobileToken)) {
                        notificationEnableButton.setVisibility(View.GONE);
                    }
                }
            });
        });
    }

    private void setNotificationTokensLayout() {
        notificationTokensLayout.removeAllViews();
        for (int i = 0; i < user.getTokens().size(); i++){
            LinearLayout row = new LinearLayout(this);
            row.setOrientation(LinearLayout.HORIZONTAL);
            row.setGravity(Gravity.CENTER_VERTICAL);

            TextView tokenName = new TextView(this);
            tokenName.setText(user.getTokenNames().get(i));
            row.addView(tokenName);

            ImageButton remove = new ImageButton(this);
            remove.setImageResource(R.drawable.icon_remove);
            remove.setBackgroundResource(R.color.transparent);

            int finalI = i;
            remove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ((LinearLayout)view.getParent()).removeAllViews();
                    if (mobileToken.equals(user.getTokens().get(finalI))){
                        notificationEnableButton.setVisibility(View.VISIBLE);
                    }

                    System.out.println("mi van he"+ user.getTokens().get(finalI));
                    user.removeToken(user.getTokens().get(finalI));
                    System.out.println("mi van he"+ user.getTokens().size());
                    dbHelper.getUsersCollection().document(userId).update("tokens", user.getTokens());
                    dbHelper.getUsersCollection().document(userId).update("tokenNames", user.getTokenNames());
                }
            });
            row.addView(remove);


            notificationTokensLayout.addView(row);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.sample_logout_menu, menu);

        //vissza gomb
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.logOut:
                firebaseAuthHelper.logOut(this);
                return true;

            case android.R.id.home:
                onBackPressed();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void updatePasswordBack(View view) {
        updatePasswordFormButton.setVisibility(View.VISIBLE);
        updatePasswordLayout.setVisibility(View.GONE);
        oldPasswordEditText.setText("");
        passwordEditText.setText("");
        passwordAgainEditText.setText("");
    }

    public void updatePassword(View view) {
        String oldPassword = oldPasswordEditText.getText().toString();
        String password = passwordEditText.getText().toString();
        String passwordAgain = passwordAgainEditText.getText().toString();

        if ( ! password.equals(passwordAgain) ){
            Toast.makeText(this, "A két jelszó nem egyezik!", Toast.LENGTH_SHORT).show();
            return;
        } else if(password.length() < 8){
            Toast.makeText(this, "A jelszónak legalább 8 hosszúnak kell lennie!", Toast.LENGTH_SHORT).show();
            return;
        }

        firebaseAuthHelper.getFirebaseAuth().signInWithEmailAndPassword(email, oldPassword).addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                firebaseAuthHelper.getFirebaseAuth().getCurrentUser().updatePassword(password);
                Toast.makeText(this, "Sikeres változtatás!", Toast.LENGTH_SHORT).show();
                updatePasswordFormButton.setVisibility(View.VISIBLE);
                updatePasswordLayout.setVisibility(View.GONE);
                oldPasswordEditText.setText("");
                passwordEditText.setText("");
                passwordAgainEditText.setText("");
            } else {
                Toast.makeText(this, "Hibás régi jelszó!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void updatePasswordForm(View view) {
        updatePasswordFormButton.setVisibility(View.GONE);
        updatePasswordLayout.setVisibility(View.VISIBLE);
    }

    public void notificationEnable(View view) {
        String mobileName = Build.MANUFACTURER + " " + Build.MODEL;

        user.addToken(mobileToken, mobileName);
        user.removeBlockToken(mobileToken);
        dbHelper.getUsersCollection().document(userId).update("tokens", user.getTokens());
        dbHelper.getUsersCollection().document(userId).update("tokenNames", user.getTokenNames());
        dbHelper.getUsersCollection().document(userId).update("blockTokens", user.getBlockTokens());
        onResume();
    }
}