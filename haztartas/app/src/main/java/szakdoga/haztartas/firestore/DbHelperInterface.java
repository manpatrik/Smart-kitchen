package szakdoga.haztartas.firestore;

import android.app.Activity;

public interface DbHelperInterface {

    public void newHome(String sserId, String homeName, String email, Activity activity);

    public void listHomes(String userId, Activity activity);

    public void createUserProfile(String userId, String email, Activity activity);
}
