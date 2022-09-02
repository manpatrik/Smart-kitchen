package szakdoga.haztartas.homesList;

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

import java.util.List;

import szakdoga.haztartas.HomeActivity;
import szakdoga.haztartas.R;
import szakdoga.haztartas.models.Home;

public class HomeItemAdapter extends RecyclerView.Adapter<HomeItemAdapter.ViewHolder> {

    private Context context;
    private List<Home> homes;
    private String userId;

    HomeItemAdapter(Context context, List<Home> homes, String userId){
        this.context = context;
        this.homes = homes;
        this.userId = userId;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new HomeItemAdapter.ViewHolder(LayoutInflater.from(context)
                .inflate(R.layout.activity_home_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bindTo(homes.get(position), context, userId);
    }

    @Override
    public int getItemCount() {
        return homes.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        private TextView homeName;
        private RelativeLayout homeItemRelLay;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            homeName = itemView.findViewById(R.id.homeItemName);
            homeItemRelLay = itemView.findViewById(R.id.homeItemRelativeLayout);
        }

        @SuppressLint({"ResourceAsColor", "SetTextI18n"})
        void bindTo(Home home, Context context, String userId){
            homeName.setText(home.getName());

            homeItemRelLay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent homeIntent = new Intent(context, HomeActivity.class);
                    homeIntent.putExtra("homeId", home.getHomeId());
                    homeIntent.putExtra("userId", userId);
                    context.startActivity(homeIntent);
                }
            });
        }
    }
}