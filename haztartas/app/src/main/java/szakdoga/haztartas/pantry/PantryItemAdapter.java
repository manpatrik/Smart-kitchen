package szakdoga.haztartas.pantry;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import java.io.Serializable;
import java.util.List;

import szakdoga.haztartas.R;
import szakdoga.haztartas.firestore.DbHelper;
import szakdoga.haztartas.models.Pantry;

public class PantryItemAdapter extends RecyclerView.Adapter<PantryItemAdapter.ViewHolder> {

    private Context context;
    private List<Pantry> pantries;
    String homeId;
    String userId;

    PantryItemAdapter(Context context, List<Pantry> pantries, String homeId, String userId){
        this.context = context;
        this.pantries = pantries;
        this.homeId = homeId;
        this.userId = userId;
    }

    @Override
    public PantryItemAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context)
                .inflate(R.layout.pantry_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bindTo(pantries.get(position), homeId, userId);
    }

    @Override
    public int getItemCount() {
        return pantries.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private TextView name;
        private TextView quantity;
        private TextView quantityUnit;
        private ImageButton deleteButton;
        private ImageButton modifyButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.pantryItemName);
            quantity = itemView.findViewById(R.id.quantity);
            quantityUnit = itemView.findViewById(R.id.quantityUnit);
            deleteButton = itemView.findViewById(R.id.deleteButton);
            modifyButton = itemView.findViewById(R.id.modifyButton);
        }

        @SuppressLint({"ResourceAsColor", "SetTextI18n"})
        void bindTo(Pantry pantry, String homeId, String userId){
            name.setText(pantry.getName());
            String quantityInString = Double.toString(pantry.getQuantity());
            if (quantityInString.charAt(quantityInString.length()-1)=='0'){
                quantityInString = quantityInString.substring(0, quantityInString.length()-2);
            }
            quantity.setText(quantityInString);
            quantityUnit.setText(pantry.getQuantityUnit());

            modifyButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent modifyIntent = new Intent(view.getContext(), ModifyPantryItem.class);
                    modifyIntent.putExtra("pantry", (Serializable) pantry);
                    modifyIntent.putExtra("userId", userId);
                    modifyIntent.putExtra("homeId", homeId);
                    view.getContext().startActivity(modifyIntent);
                }
            });

            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                    builder.setMessage("Mit szeretne csinálni ezzel: "+pantry.getName()+"?");
                    builder.setCancelable(true);

                    builder.setPositiveButton(
                            "Nullázás",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    // a hozzávaló 0-ra állítása
                                    System.out.println("almaa"+pantry.getId());
                                    DbHelper dbHelper = new DbHelper();
                                    dbHelper.getHomeCollection().document(homeId).collection("Pantry").document(pantry.getId()).update("quantity", 0);
                                    ((PantryListActivity) view.getContext()).onResume();
                                }
                            });

                    builder.setNegativeButton(
                            "Törlés az adatbázisból",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    // törlés az adatbázisból
                                    DbHelper dbHelper = new DbHelper();
                                    dbHelper.getHomeCollection().document(homeId).collection("Pantry").document(pantry.getId()).delete();
                                }
                            });

                    builder.setNeutralButton(
                            "Mégsem",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.cancel();
                                }
                            });

                    AlertDialog alert = builder.create();
                    alert.show();
                }
            });
        }
    }
}