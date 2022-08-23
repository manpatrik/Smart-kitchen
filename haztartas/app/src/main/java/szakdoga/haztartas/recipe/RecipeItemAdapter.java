package szakdoga.haztartas.recipe;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.Serializable;
import java.util.List;

import szakdoga.haztartas.R;
import szakdoga.haztartas.models.Recipe;

public class RecipeItemAdapter extends RecyclerView.Adapter<RecipeItemAdapter.ViewHolder> {

    private Context context;
    private List<Recipe> recipes;
    private String userId;
    private String homeId;

    public RecipeItemAdapter(Context context, List<Recipe> recipes, String userId, String homeId) {
        this.context = context;
        this.recipes = recipes;
        this.userId = userId;
        this.homeId = homeId;
    }

    @Override
    public RecipeItemAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context)
                .inflate(R.layout.activity_recipe_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bindTo(recipes.get(position), userId, homeId);
    }

    @Override
    public int getItemCount() {
        return recipes.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        private TextView name;
        private TextView preparationTime;
        private RelativeLayout recipeLayout;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.recipeItemName);
            preparationTime = itemView.findViewById(R.id.preparationTime);
            recipeLayout = itemView.findViewById(R.id.recipeLayout);

        }

        @SuppressLint({"ResourceAsColor", "SetTextI18n"})
        void bindTo(Recipe recipe, String userId, String homeId){
             name.setText(recipe.getName());
             preparationTime.setText(recipe.getPreparationTime());

             recipeLayout.setOnClickListener(view ->{
                 Intent recipeDetailsIntent = new Intent(context, RecipeDetailsActivity.class);
                 recipeDetailsIntent.putExtra("recipe", (Serializable) recipe);
                 recipeDetailsIntent.putExtra("userId", userId);
                 recipeDetailsIntent.putExtra("homeId", homeId);
                 context.startActivity(recipeDetailsIntent);
             });
        }
    }
}