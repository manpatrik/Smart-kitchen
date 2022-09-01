package szakdoga.haztartas.pantry;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.text.InputType;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.Serializable;
import java.util.List;

import io.grpc.internal.SharedResourceHolder;
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
                    AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                    builder.setTitle(pantry.getName());

                    LinearLayout row = new LinearLayout(view.getContext());
                    row.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    row.setOrientation(LinearLayout.HORIZONTAL);
                    row.setGravity(Gravity.CENTER);


                    ImageButton remove = new ImageButton(view.getContext());

                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    layoutParams.rightMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5, view.getResources().getDisplayMetrics());

                    remove.setLayoutParams(layoutParams);
                    remove.setImageResource(R.drawable.icon_remove);
                    remove.setBackgroundResource(R.color.transparent);
                    remove.setScaleType(ImageView.ScaleType.FIT_XY);
                    remove.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            EditText value = (EditText) ((LinearLayout)view.getParent()).getChildAt(1);
                            if (Double.parseDouble(value.getText().toString()) >= 1)
                                value.setText(Double.parseDouble(value.getText().toString())-1+"");
                        }
                    });
                    row.addView(remove);


                    EditText quantityEditText = new EditText(view.getContext());
                    quantityEditText.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL);
                    quantityEditText.setText(Double.toString(pantry.getQuantity()));
                    row.addView(quantityEditText);


                    TextView quantityUnitText = new TextView(view.getContext());
                    quantityUnitText.setText(pantry.getQuantityUnit());

                    LinearLayout.LayoutParams layoutParamsTextView = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    layoutParamsTextView.rightMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5, view.getResources().getDisplayMetrics());
                    layoutParamsTextView.leftMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, view.getResources().getDisplayMetrics());

                    quantityUnitText.setLayoutParams(layoutParamsTextView);

                    row.addView(quantityUnitText);


                    ImageButton add = new ImageButton(view.getContext());
                    add.setImageResource(R.drawable.icon_add);
                    add.setBackgroundResource(R.color.transparent);
                    add.setScaleType(ImageView.ScaleType.FIT_XY);
                    add.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            EditText value = (EditText) ((LinearLayout)view.getParent()).getChildAt(1);
                            value.setText(Double.parseDouble(value.getText().toString())+1+"");
                        }
                    });
                    row.addView(add);

                    builder.setView(row);

                    builder.setPositiveButton("Mentés", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            DbHelper dbHelper = new DbHelper();
                            dbHelper.getHomeCollection().document(homeId).collection("Pantry").document(pantry.getId()).update("quantity", Double.parseDouble(quantityEditText.getText().toString()));
                            ((PantryListActivity)view.getContext()).onResume();
                            Toast.makeText(view.getContext(), "Sikeres Mentés", Toast.LENGTH_SHORT).show();
                        }
                    });

                    builder.setNegativeButton("Mégsem", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.cancel();
                        }
                    });

                    builder.setNeutralButton("Módosítás", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Intent modifyIntent = new Intent(view.getContext(), ModifyPantryItem.class);
                            modifyIntent.putExtra("pantry", (Serializable) pantry);
                            modifyIntent.putExtra("userId", userId);
                            modifyIntent.putExtra("homeId", homeId);
                            view.getContext().startActivity(modifyIntent);
                        }
                    });

                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
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
                                    ((PantryListActivity) view.getContext()).onResume();
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