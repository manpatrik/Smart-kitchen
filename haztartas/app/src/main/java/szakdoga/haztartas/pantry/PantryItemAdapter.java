package szakdoga.haztartas.pantry;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import szakdoga.haztartas.R;
import szakdoga.haztartas.models.Pantry;

public class PantryItemAdapter extends RecyclerView.Adapter<PantryItemAdapter.ViewHolder> {

    private Context context;
    private List<Pantry> pantries;

    PantryItemAdapter(Context context, List<Pantry> pantries){
        this.context = context;
        this.pantries = pantries;
    }

    @Override
    public PantryItemAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context)
                .inflate(R.layout.activity_pantry_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bindTo(pantries.get(position));
    }

    @Override
    public int getItemCount() {
        return pantries.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private TextView name;
        private TextView quantity;
        private TextView quantityUnit;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.pantryItemName);
            quantity = itemView.findViewById(R.id.quantity);
            quantityUnit = itemView.findViewById(R.id.quantityUnit);
        }

        @SuppressLint({"ResourceAsColor", "SetTextI18n"})
        void bindTo(Pantry pantry){
            name.setText(pantry.getName());
            quantity.setText(Integer.toString(pantry.getQuantity()));
            quantityUnit.setText(pantry.getQuantityUnit());
        }
    }
}