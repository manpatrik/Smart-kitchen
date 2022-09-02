package szakdoga.haztartas;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.firestore.DocumentSnapshot;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import szakdoga.haztartas.firebaseAuthentication.FirebaseAuthHelper;
import szakdoga.haztartas.firestore.DbHelper;
import szakdoga.haztartas.models.Home;

public class Settings extends AppCompatActivity {

    private FirebaseAuthHelper firebaseAuthHelper;
    private DbHelper dbHelper;

    private String userId;
    private String homeId;
    private Home home;
    private boolean owner = true;

    private boolean nameIsChanged = false;
    private boolean deleteHousehold = false;

    private EditText householdNameEditText;
    private EditText passwordEditText;
    private EditText emailAddressEditText;
    private Button newHouseholdMemberButton;
    private Button DeleteHouseholdButton;
    private Button nameSaveButton;
    private Button DeleteHouseholdButtonCancel;
    private ImageButton addHouseholdMemberImageButton;
    private ImageButton EditHouseholdNameimageButton;
    private LinearLayout householdMembersLayout;
    private TextView ownerText;
    private TextView passwordText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        this.setTitle("Beállítások");

        firebaseAuthHelper = new FirebaseAuthHelper();
        dbHelper = new DbHelper();

        householdNameEditText = findViewById(R.id.householdNameEditText);
        newHouseholdMemberButton = findViewById(R.id.newHouseholdMemberButton);
        emailAddressEditText = findViewById(R.id.emailAddressEditText);
        addHouseholdMemberImageButton = findViewById(R.id.addHouseholdMemberImageButton);
        DeleteHouseholdButton = findViewById(R.id.DeleteHouseholdButton);
        EditHouseholdNameimageButton = findViewById(R.id.EditHouseholdNameimageButton);
        nameSaveButton = findViewById(R.id.nameSaveButton);
        householdMembersLayout = findViewById(R.id.householdMembersLayout);
        ownerText = findViewById(R.id.ownerText);
        passwordText = findViewById(R.id.passwordText);
        DeleteHouseholdButtonCancel = findViewById(R.id.DeleteHouseholdButtonCancel);
        passwordEditText = findViewById(R.id.passwordEditText);

        userId = getIntent().getStringExtra("userId");
        homeId = getIntent().getStringExtra("homeId");

        dbHelper.getHomeCollection().document(homeId).get().addOnSuccessListener(data ->{
            home = new Home(
                    data.getId(),
                    data.get("name").toString(),
                    data.get("ownerEmail").toString(),
                    data.get("owner").toString(),
                    (List<String>) data.get("guestIds"),
                    (List<String>) data.get("guestEmails")
            );

            householdNameEditText.setText(home.getName());
            ownerText.setText(home.getOwnerEmail());

            if(!data.get("owner").toString().equals(userId)){
                owner = false;
                DeleteHouseholdButton.setVisibility(View.GONE);
                EditHouseholdNameimageButton.setVisibility(View.GONE);
                newHouseholdMemberButton.setVisibility(View.GONE);
            }

            householdGuestsList();
        });
    }

    @Override
    protected void onResume() {
        firebaseAuthHelper.isAuthUser(userId, this);
        super.onResume();

    }

    public void enableHouseholdNameEdit(View view) {
        nameSaveButton.setVisibility(View.VISIBLE);
        householdNameEditText.setEnabled(true);
        nameIsChanged = true;
    }

    public void nameSave(View view) {
        if(nameIsChanged){
            dbHelper.getHomeCollection().document(homeId).update("name", householdNameEditText.getText().toString());
            nameSaveButton.setVisibility(View.GONE);
            householdNameEditText.setEnabled(false);
            nameIsChanged = false;
        }
    }

    public void DeleteHousehold(View view) {
        if (deleteHousehold){
            String password = passwordEditText.getText().toString();
            if (password.length() != 0){
                firebaseAuthHelper.getFirebaseAuth().signInWithEmailAndPassword(home.getOwnerEmail(), password).addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // háztartás törlése
                        dbHelper.getHomeCollection().document(homeId).delete();
                        finish();
                    } else {
                        Toast.makeText(this, "Hibás jelszó!", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                Toast.makeText(this, "Nem írta be a jelszavát!", Toast.LENGTH_SHORT).show();
            }
        } else {
            deleteHousehold = true;
            passwordEditText.setVisibility(View.VISIBLE);
            DeleteHouseholdButtonCancel.setVisibility(View.VISIBLE);
            passwordText.setVisibility(View.VISIBLE);
        }
    }

    public void DeleteHouseholdCancel(View view) {
        deleteHousehold = false;
        passwordEditText.setVisibility(View.GONE);
        DeleteHouseholdButtonCancel.setVisibility(View.GONE);
        passwordText.setVisibility(View.GONE);
    }

    public void newHouseholdMemberShow(View view) {
        newHouseholdMemberButton.setVisibility(View.GONE);
        emailAddressEditText.setVisibility(View.VISIBLE);
        addHouseholdMemberImageButton.setVisibility(View.VISIBLE);
    }

    public void addHouseholdMember(View view) {
        newHouseholdMemberButton.setVisibility(View.VISIBLE);
        emailAddressEditText.setVisibility(View.GONE);
        addHouseholdMemberImageButton.setVisibility(View.GONE);
        String guestEmail = emailAddressEditText.getText().toString();
        //megnézem hogy megosztották-e már ezzel a felhasználóval a háztartást
        if (!home.getGuestEmails().contains(guestEmail)){

            //megnézem hogy létezik-e ilyen email címmel felhasználó
            dbHelper.getUsersCollection().whereEqualTo("email", guestEmail).get().addOnSuccessListener(queryDocumentSnapshots -> {
                if(queryDocumentSnapshots.getDocuments().size() != 0) {
                    DocumentSnapshot data = queryDocumentSnapshots.getDocuments().get(0);
                    // az otthonba bekerülnek a vendég adatai
                    home.addGuest(data.get("userId").toString(), guestEmail);
                    dbHelper.getHomeCollection().document(homeId).update("guestIds", home.getGuestIds());
                    dbHelper.getHomeCollection().document(homeId).update("guestEmails", home.getGuestEmails());

                    Toast.makeText(this, "Sikeres hozzáadás!", Toast.LENGTH_SHORT).show();
                    householdGuestsList();
                } else{
                    Toast.makeText(this, "Nincs ilyen felhasználó!", Toast.LENGTH_SHORT).show();
                }
            });

        } else {
            Toast.makeText(this, guestEmail+" már a háztartás tagja!", Toast.LENGTH_SHORT).show();
        }
        emailAddressEditText.setText("");
    }

    private void removeGuest(String email, String userId, String homeId){
        AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
        builder1.setMessage("Biztosan törölni szeretné\n" + email + "\n-t a háztartásból?");
        builder1.setCancelable(true);

        builder1.setPositiveButton(
                "Törlés",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // törlés az adatbázisból
                        home.deleteGuest(userId);
                        dbHelper.getHomeCollection().document(homeId).update("guestIds", home.getGuestIds());
                        dbHelper.getHomeCollection().document(homeId).update("guestEmails", home.getGuestEmails());

                        householdGuestsList();
                        dialog.cancel();
                    }
                });

        builder1.setNegativeButton(
                "Mégsem",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alert11 = builder1.create();
        alert11.show();

    }

    private void householdGuestsList(){
        householdMembersLayout.removeAllViews();
        for (int i = 0; i < home.getGuestEmails().size(); i++){
            LinearLayout row = new LinearLayout(this);
            row.setOrientation(LinearLayout.HORIZONTAL);
            row.setGravity(Gravity.CENTER_VERTICAL);

            TextView email = new TextView(this);
            email.setText(home.getGuestEmails().get(i));
            row.addView(email);
            if (owner){
                ImageButton remove = new ImageButton(this);
                remove.setImageResource(R.drawable.icon_remove);
                remove.setBackgroundResource(R.color.transparent);

                int finalI = i;
                remove.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        removeGuest(home.getGuestEmails().get(finalI), home.getGuestIds().get(finalI), homeId);
                    }
                });
                row.addView(remove);
            }

            householdMembersLayout.addView(row);
        }
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