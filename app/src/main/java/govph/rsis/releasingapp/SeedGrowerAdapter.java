package govph.rsis.releasingapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class SeedGrowerAdapter extends RecyclerView.Adapter<SeedGrowerAdapter.ViewHolder> {
    private static final String TAG = "SeedGrowerAdapter";
    private Context context;
    private SGClicked sgClickedListener;
    public ArrayList<SeedGrower> mSeedGrower;

    public SeedGrowerAdapter(Context context, ArrayList<SeedGrower> mSeedGrower) {
        this.context = context;
        this.mSeedGrower = mSeedGrower;
    }

    public interface SGClicked {
        void onSGclicked(String orderId);
    }

    public void setSgClickedListener(SeedGrowerAdapter.SGClicked sgClickedListener) {
        this.sgClickedListener = sgClickedListener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView textFullname, textOrderId, textStatus;
        public ViewHolder(View view) {
            super(view);
            textFullname = view.findViewById(R.id.sgListName);
            textOrderId = view.findViewById(R.id.listOrderId);
            textStatus = view.findViewById(R.id.tvStatus);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View v = layoutInflater.inflate(R.layout.item_seed_growers, parent,false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(SeedGrowerAdapter.ViewHolder viewHolder, final int position) {
        SeedGrower seedGrower = mSeedGrower.get(position);

        viewHolder.textFullname.setText(seedGrower.getFullname());
        viewHolder.textOrderId.setText(seedGrower.getOrderId());

        if(seedGrower.getStatus() == 1){
            viewHolder.textStatus.setText("For Scan");
        }
        else{
            viewHolder.textStatus.setText("Done");
        }

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
    public int getItemCount() {return mSeedGrower.size();}
}
