package szakdoga.haztartas.firestore;

import android.app.Activity;
import android.widget.Toast;

import androidx.annotation.WorkerThread;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;

import szakdoga.haztartas.homesList.HomeItemAdapter;
import szakdoga.haztartas.homesList.HomesListActivity;
import szakdoga.haztartas.models.Home;
import szakdoga.haztartas.models.Ingredient;
import szakdoga.haztartas.models.Pantry;
import szakdoga.haztartas.models.Recipe;
import szakdoga.haztartas.models.User;

public class DbHelper implements DbHelperInterface {
    private CollectionReference homeCollection;
    private CollectionReference usersCollection;

    public DbHelper(){
        homeCollection = FirebaseFirestore.getInstance().collection("Homes");
        usersCollection = FirebaseFirestore.getInstance().collection("Users");
    }

    public CollectionReference getUsersCollection() {
        return usersCollection;
    }

    public CollectionReference getHomeCollection() {
        return homeCollection;
    }


    /** Létrehozza az adatbázis Users táblájába a felhasználót
     *
     *
     */
    public void createUserProfile(String userId, String email, Activity activity) {
        User user = new User(userId, email);
        usersCollection.document(userId).set(user);
        activity.finish();
    }

    /**
     * Létrehozza a háztartást és egy-egy minta receptet, hozzávalót és bevásárló listát
     */
    @Override
    public void newHome(String userId, String homeName, String email, Activity activity) {
        Home home = new Home(null, homeName, email, userId, Arrays.asList(), Arrays.asList());

        homeCollection.add(home).addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                String homeId = task.getResult().getId();
                homeCollection.document(homeId).update("homeId", homeId);

                // Minta hozzávalók feltöltése a háztartásba
                List<Pantry> pantries = new ArrayList<>();
                pantries.add(new Pantry("tojás", 8, "darab", "Hűtő", Arrays.asList()));
                pantries.add(new Pantry("liszt", 1.5, "kg", "Szekrény",Arrays.asList()));
                pantries.add(new Pantry("paradicsomlé", 750, "ml", "Szekrény",Arrays.asList()));
                pantries.add(new Pantry("borsó", 1, "kg", "Fagyasztó",Arrays.asList()));
                pantries.add(new Pantry("répa", 4, "darab", "Zöldségek/gyümülcsök",Arrays.asList()));
                pantries.add(new Pantry("fehér répa", 3, "darab", "Zöldségek/gyümülcsök",Arrays.asList()));
                for(Pantry pantry : pantries){
                    homeCollection.document(homeId).collection("Pantry").add(pantry).addOnSuccessListener(documentReference -> {
                        homeCollection.document(homeId).collection("Pantry").document(documentReference.getId()).update("id", documentReference.getId());
                    });
                }

                //minta recept
                Recipe recipe = new Recipe(
                        null,
                        "Paradicsom leves",
                        "leves",
                        "Így készítsd el.",
                        "30 perc",
                        1,
                        4,
                        "fő"
                );
                recipe.addIngredient(new Ingredient("500", "ml","paradicsomlé"));
                recipe.addIngredient(new Ingredient("1", "csipet","só"));
                recipe.addIngredient(new Ingredient("1", "evőkanál","liszt"));
                recipe.addIngredient(new Ingredient("1", "evőkanál","cukor"));
                recipe.addIngredient(new Ingredient("2", "marék","betűtészta"));

                homeCollection.document(homeId).collection("Recipes").add(recipe).addOnSuccessListener(result -> {
                    getHomeCollection().document(homeId).collection("Recipes").document(result.getId()).update("id", result.getId());
                });

                activity.finish();
                activity.startActivity(activity.getIntent());

            } else{
                Toast.makeText(activity, "A létrehozás nem sikerült.", Toast.LENGTH_LONG).show();
            }
        });


    }

    // https://stackoverflow.com/questions/49125183/how-delete-a-collection-or-subcollection-from-firestore
    public void deleteCollection(final CollectionReference collection, Executor executor) {
        Tasks.call(executor, () -> {
            int batchSize = 10;
            Query query = collection.orderBy(FieldPath.documentId()).limit(batchSize);
            List<DocumentSnapshot> deleted = deleteQueryBatch(query);

            while (deleted.size() >= batchSize) {
                DocumentSnapshot last = deleted.get(deleted.size() - 1);
                query = collection.orderBy(FieldPath.documentId()).startAfter(last.getId()).limit(batchSize);

                deleted = deleteQueryBatch(query);
            }

            return null;
        });
    }

    @WorkerThread
    private List<DocumentSnapshot> deleteQueryBatch(final Query query) throws Exception {
        QuerySnapshot querySnapshot = Tasks.await(query.get());

        WriteBatch batch = query.getFirestore().batch();
        for (DocumentSnapshot snapshot : querySnapshot) {
            batch.delete(snapshot.getReference());
        }
        Tasks.await(batch.commit());

        return querySnapshot.getDocuments();
    }

}
