package szakdoga.haztartas.firebaseAuthentication;

import android.app.Activity;

public interface FirebaseAuthHelperInterface {

    public void login(String email, String password, Activity activity);

    public void register(String email, String password, Activity activity);

    public void isAuthUser(String userId, Activity activity);

    public void logOut(Activity activity);
}
