package szakdoga.haztartas.firestore;

import android.app.Activity;
import android.widget.Toast;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import szakdoga.haztartas.models.Home;
import szakdoga.haztartas.models.Pantry;

public class DbHelper implements DbHelperInterface {
    private CollectionReference homeCollection;
    private Query homeQueryGuest;
    private CollectionReference usersCollection;

    public DbHelper(){
        homeCollection = FirebaseFirestore.getInstance().collection("Homes");
        homeQueryGuest = FirebaseFirestore.getInstance().collectionGroup("Guests");
        usersCollection = FirebaseFirestore.getInstance().collection("Users");
    }

    public CollectionReference getUsersCollection() {
        return usersCollection;
    }

    public CollectionReference getHomeCollection() {
        return homeCollection;
    }

    public Query getHomeQueryGuest(){
        return homeQueryGuest;
    }


    /** Létrehozza az adatbázis Users táblájába a felhasználót
     *
     *
     */
    public void createUserProfile(String userId, String email, Activity activity) {
        System.out.println("createUser");
        Map<String, Object> userProfile = new HashMap<>();
        userProfile.put("userId", userId);
        userProfile.put("email", email);
        usersCollection.document(userId).set(userProfile);
        activity.finish();
    }

    /**
     * Létrehozza a háztartást és egy-egy minta receptet és bevásárló listát
     */
    @Override
    public void newHome(String userId, String homeName, String email, Activity activity) {
        Map<String, Object> home = new HashMap<>();
        home.put("name", homeName);
        home.put("owner", userId);
        home.put("ownerEmail", email);
        homeCollection.add(home).addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                String homeId;
                homeId = task.getResult().getId();

                List<Pantry> pantries = new ArrayList<>();
                pantries.add(new Pantry("tojás", 8, "db", "Hűtő"));
                pantries.add(new Pantry("liszt", 1.5, "kg", "Szekrény"));
                pantries.add(new Pantry("borsó", 1, "kg", "Fagyasztó"));
                pantries.add(new Pantry("répa", 4, "db", "Zöldségek/gyümülcsök"));
                pantries.add(new Pantry("fehér répa", 3, "db", "Zöldségek/gyümülcsök"));
                for(Pantry pantry : pantries){
                    homeCollection.document(homeId).collection("Pantry").add(pantry);
                }

                Map<String, Object> recipe = new HashMap<>();
                recipe.put("name", "Paradicsom leves");
                recipe.put("category", "leves");
                recipe.put("description", "Így készítsd el.");
                recipe.put("ingredients", "500;ml;paradicsomlé#" +
                        "1;csipet;só#" +
                        "1;kanál;cukor#" +
                        "2;marék;betűtészta");
                recipe.put("preparationTime", "30 perc");
                recipe.put("difficulty", 1);
                recipe.put("quantity",4);
                recipe.put("quantityUnit", "fő");
                homeCollection.document(homeId).collection("Recipes").add(recipe);

                activity.finish();
                activity.startActivity(activity.getIntent());

            } else{
                Toast.makeText(activity, "A létrehozás nem sikerült.", Toast.LENGTH_LONG).show();
            }
        });


    }

    @Override
    public void listHomes(String userId, Activity activity) {
        List<Home> homes = new ArrayList<>();

    }
}
