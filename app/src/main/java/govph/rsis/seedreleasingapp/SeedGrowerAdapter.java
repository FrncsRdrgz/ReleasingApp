package govph.rsis.seedreleasingapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class SeedGrowerAdapter extends RecyclerView.Adapter<SeedGrowerAdapter.ViewHolder> {
    private static final String TAG = "SeedGrowerAdapter";
    private Context context;
    private SGClicked sgClickedListener;
    List<Seed> seeds = new ArrayList<>();

    /*public SeedGrowerAdapter(Context context, ArrayList<SeedGrower> mSeedGrower) {
        this.context = context;
        this.mSeedGrower = mSeedGrower;
    }*/
    public void setOrders(List<Seed> seeds) {
        this.seeds = seeds;
        notifyDataSetChanged();
    }

    public interface SGClicked {
        void onSGclicked(String orderId);
    }

    public void setSgClickedListener(SeedGrowerAdapter.SGClicked sgClickedListener) {
        this.sgClickedListener = sgClickedListener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView  textOrderId, textStatus;
        public ViewHolder(View view) {
            super(view);
            textOrderId = view.findViewById(R.id.listOrderId);
            textStatus = view.findViewById(R.id.tvStatus);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View v = layoutInflater.inflate(R.layout.item_seed_growers, parent,false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(SeedGrowerAdapter.ViewHolder viewHolder, final int position) {
        Seed seed = seeds.get(position);
        viewHolder.textOrderId.setText(seed.getOrderId());
        viewHolder.textStatus.setText("Released");

        /*button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (sgClickedListener != null) {
                    sgClickedListener.onSGclicked(mSeedGrower.get(position).getOrderId());
                }
            }
        });*/
    }

    @Override
    public int getItemCount() {return seeds.size();}
}
