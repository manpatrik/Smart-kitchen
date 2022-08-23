package szakdoga.haztartas.firebaseAuthentication;

import android.app.Activity;
import android.content.Intent;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import szakdoga.haztartas.firestore.DbHelper;
import szakdoga.haztartas.homesList.HomesListActivity;

public class FirebaseAuthHelper implements FirebaseAuthHelperInterface {
    private final FirebaseAuth firebaseAuth;
    private String uId = null;
    private DbHelper dbHelper;

    public FirebaseAuthHelper() {
        firebaseAuth = FirebaseAuth.getInstance();
        dbHelper = new DbHelper();
    }

    public FirebaseAuth getFirebaseAuth() {
        return firebaseAuth;
    }

    /** Bejelentkezés firebase-be
     *
     * @return user id token
     */
    @Override
    public void login(String email, String password, Activity activity) {
        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(activity, task -> {
            if(task.isSuccessful()){
                uId = task.getResult().getUser().getUid();
            }else {
                Toast.makeText(activity, "A belépés nem sikerült.", Toast.LENGTH_LONG).show();
                uId =  null;
            }
            if (uId != null){
                Intent homesListIntent = new Intent(activity, HomesListActivity.class);
                homesListIntent.putExtra("userId", uId);
                homesListIntent.putExtra("email", email);
                activity.startActivity(homesListIntent);
            }
        });
    }


    /** Regisztáció firebase-be
     *
     * @return user id token
     */
    @Override
    public void register(String email, String password, Activity activity) {
        firebaseAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(activity, task -> {
            if(task.isSuccessful()){
                Toast.makeText(activity, "Sikeres regisztráció!", Toast.LENGTH_LONG).show();
                uId = task.getResult().getUser().getUid();
                dbHelper.createUserProfile(uId, email, activity);
            } else {
                Toast.makeText(activity, "Sikertelen regisztráció! " + task.getException().getMessage(), Toast.LENGTH_LONG).show();

            }
        });
    }

    /**
     * Ha nincs bejelentkezve a felhasználó, akkor leállítja a futó activity-t
     *
     */
    @Override
    public void isAuthUser(String userId, Activity activity) {
        if (firebaseAuth.getCurrentUser() != null) {
            if (!firebaseAuth.getCurrentUser().getUid().equals(userId)) {
                activity.finish();
            }
        } else {
            activity.finish();
        }
    }

    @Override
    public void logOut(Activity activity) {
        firebaseAuth.signOut();
        activity.finish();
    }


}
