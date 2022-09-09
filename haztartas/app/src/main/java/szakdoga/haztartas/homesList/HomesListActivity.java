package szakdoga.haztartas.homesList;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;
import java.util.List;

import szakdoga.haztartas.R;
import szakdoga.haztartas.UserSettingsActivity;
import szakdoga.haztartas.firebaseAuthentication.FirebaseAuthHelper;
import szakdoga.haztartas.firestore.DbHelper;
import szakdoga.haztartas.models.Home;
import szakdoga.haztartas.models.User;

public class HomesListActivity extends AppCompatActivity {

    private FirebaseAuthHelper firebaseAuthHelper;
    private DbHelper dbHelper;
    private String userId;

    private HomeItemAdapter homeItemAdapter;
    private RecyclerView homesRecylerView;
    private EditText homeNameEditText;

    public static List<Home> homes = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homes_list);

        homesRecylerView = findViewById(R.id.homesRecylerView);
        homeNameEditText = findViewById(R.id.homeNameEditText);

        firebaseAuthHelper = new FirebaseAuthHelper();
        dbHelper = new DbHelper();
        userId = this.getIntent().getStringExtra("userId");

        homeItemAdapter = new HomeItemAdapter(this, homes, userId);
        homesRecylerView.setAdapter(homeItemAdapter);
        homesRecylerView.setLayoutManager(new LinearLayoutManager(this));

        checkTokens();

    }

    @Override
    protected void onResume() {
        super.onResume();
        firebaseAuthHelper.isAuthUser(userId, this);
        homesquery();
    }

    private void checkTokens() {
        dbHelper.getUsersCollection().document(userId).get().addOnSuccessListener(data -> {
            if (data != null){
                FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {
                    if (task.isSuccessful()){
                        String mobileToken = task.getResult();
                        String mobileName = Build.MANUFACTURER + " " + Build.MODEL;
                        boolean find = false;
                        boolean findBlock = false;
                        User user = data.toObject(User.class);
                        if (user != null) {
                            for (String token : user.getTokens()){
                                if( mobileToken.equals(token)) {
                                    find = true;
                                    break;
                                }
                            }
                            for (String blockToken : user.getBlockTokens()) {
                                if (blockToken.equals(mobileToken)) {
                                    findBlock = true;
                                    break;
                                }
                            }

                            if (!find && !findBlock) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                                builder.setTitle("Értesítések engedélyezése");
                                builder.setMessage("Értesítéseket kaphat a háztartásban történt dolgokról.");

                                builder.setNegativeButton("Tiltás", (dialogInterface, i) -> {
                                    user.addBlockToken(mobileToken);
                                    dbHelper.getUsersCollection().document(userId).update("blockTokens", user.getBlockTokens());
                                });

                                builder.setPositiveButton("Engedélyez", (dialogInterface, i) -> {
                                    user.addToken(mobileToken, mobileName);
                                    dbHelper.getUsersCollection().document(userId).update("tokens", user.getTokens());
                                    dbHelper.getUsersCollection().document(userId).update("tokenNames", user.getTokenNames());
                                });

                                builder.create().show();
                            }
                        }
                    }
                });
            }
        });

    }

    public void newHome(View view) {
        if (homeNameEditText.getText().length() != 0){
            dbHelper.newHome(userId, homeNameEditText.getText().toString(),getIntent().getStringExtra("email"),this);
        } else {
            Toast.makeText(this, "Nem adott meg nevet!", Toast.LENGTH_LONG).show();
        }
    }


    /**
     * Kilistázza a saját háztartásainkat
     */
    @SuppressLint("NotifyDataSetChanged")
    private void homesquery(){
        homes.clear();
        // tulajdonos
        dbHelper.getHomeCollection().whereEqualTo("owner", userId).get().addOnSuccessListener(queryDocumentSnapshots -> {
            for (DocumentSnapshot data: queryDocumentSnapshots){
                homes.add(data.toObject(Home.class));
                homeItemAdapter.notifyDataSetChanged();
            }

            // vendégek
            dbHelper.getHomeCollection().whereArrayContains("guestIds", userId).get().addOnSuccessListener(queryDocumentSnapshots2 ->{
                for (DocumentSnapshot data: queryDocumentSnapshots2) {
                    homes.add(data.toObject(Home.class));
                    homeItemAdapter.notifyDataSetChanged();
                }
            });
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.home_list_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.logOut:
                firebaseAuthHelper.logOut(this);
                return true;

            case R.id.userSettings:
                Intent userSettingsIntent = new Intent(this, UserSettingsActivity.class);
                userSettingsIntent.putExtra("userId", userId);
                startActivity(userSettingsIntent);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}