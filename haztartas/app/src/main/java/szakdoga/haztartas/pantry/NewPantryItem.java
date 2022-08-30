/*
 * vonalkód olvasás:
 * https://www.youtube.com/watch?v=jtT60yFPelI&t=88s&ab_channel=CamboTutorial
 * https://github.com/journeyapps/zxing-android-embedded
 * */

package szakdoga.haztartas.pantry;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

import szakdoga.haztartas.R;

public class NewPantryItem extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_pantry_item);
    }

    ActivityResultLauncher<ScanOptions> barLauncher = registerForActivityResult(new ScanContract(), result -> {
        if(result.getContents() != null){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Vonalkód");
            builder.setMessage(result.getContents());
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            }).show();
        }
    });

    public void barcodeScanner(View view) {
        ScanOptions options = new ScanOptions();
        options.setPrompt("Tartsa a kamerát a vonalkód elé.");
        options.setBeepEnabled(false);
        options.setOrientationLocked(true);
        options.setCaptureActivity(CaptureAct.class);
        barLauncher.launch(options);
    }

    public void saveNewPantryItem(View view) {
    }
}