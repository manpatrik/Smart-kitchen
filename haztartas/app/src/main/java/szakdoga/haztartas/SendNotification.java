package szakdoga.haztartas;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.google.firebase.messaging.FirebaseMessaging;

import szakdoga.haztartas.FirebaseCloudMessaging.FCMSend;

public class SendNotification extends AppCompatActivity {

    private EditText etTitle;
    private EditText etMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_notification);

        etTitle = findViewById(R.id.etTitle);
        etMessage = findViewById(R.id.etMessage);

        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {
            System.out.println(":o");
            if (task.isSuccessful()){
                System.out.println("token: "+ task.getResult());
            }
        });

    }

    public void send(View view) {

        String title = etTitle.getText().toString();
        String message = etMessage.getText().toString();
        FCMSend.pushNotification(
                this,
                "eyc_OPkgT1WdyANXD80CHC:APA91bFmnVYyKq4TignkBKufPgmyHhGDvRyQh4UMH2t_pMgSalpQfh9iNe_WSCiPslugLun8K4zp1X4AQ5Jz2Y349E73AJfx6qPpm15L4Cpys_eyJqU0yvPoYhu85efD-HZo2uO3BRW9",
                title,
                message
        );
    }
}