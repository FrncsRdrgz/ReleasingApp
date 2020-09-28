package govph.rsis.releasingapp;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class ReleasingAdapter extends RecyclerView.Adapter<ReleasingAdapter.ViewHolder> {

    private static final String TAG = "ReleasingAdapter";
    private Context context;
    public List<Seed> mSeed = new ArrayList<>();
    private scanTagClicked scanTagClickedListener;

    public void setSeeds(List<Seed> mSeed){
        this.mSeed = mSeed;
        notifyDataSetChanged();
    }
    /*public ReleasingAdapter(Context context, ArrayList<Seed> mSeed) {
        this.context = context;
        this.mSeed = mSeed;
    }*/

    public interface scanTagClicked{
        void scanTag(Seed seed,int position, View v);
    }

    public void setScanTagClickedListener(ReleasingAdapter.scanTagClicked scanTagClickedListener){
        this.scanTagClickedListener = scanTagClickedListener;
    }
    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView textVariety, textPalletCode, textQuantity, textLotCode,textAuthTag;
        public Button btnScanTag;

        public ViewHolder(View itemView) {
            super(itemView);
            textVariety = itemView.findViewById(R.id.varietyId);
            textPalletCode = itemView.findViewById(R.id.palletCode);
            textQuantity = itemView.findViewById(R.id.testView);
            textLotCode = itemView.findViewById(R.id.lotCode);
            btnScanTag = itemView.findViewById(R.id.scan_tag);
            textAuthTag = itemView.findViewById(R.id.auth_tag);

            btnScanTag.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    if(scanTagClickedListener != null && position != RecyclerView.NO_POSITION){
                        scanTagClickedListener.scanTag(mSeed.get(position),position, view);
                        Log.e(TAG, "onClick: "+position );
                        //btnScanTag.setText("Verified");
                    }
                }
            });
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_released, parent, false);
        return new ViewHolder(itemView);
    }

    // Involves populating data into the item through holder
    @Override
    public void onBindViewHolder(final ReleasingAdapter.ViewHolder viewHolder, final int position) {
        // Get the data model based on position
        final Seed seed = mSeed.get(position);
        viewHolder.textQuantity.setText(seed.getQuantity());
        viewHolder.textVariety.setText(seed.getVariety());
        viewHolder.textPalletCode.setText(seed.getPallet_code());
        viewHolder.textLotCode.setText(seed.getLotCode());
        viewHolder.textAuthTag.setText(seed.getAuthTag());

        if(seed.getVerified().equals("1")){
            viewHolder.btnScanTag.setText("Verified");
            viewHolder.btnScanTag.setEnabled(false);
        }
        else{
            viewHolder.btnScanTag.setText("Scan");
        }


    }

    // Returns the total count of items in the list
    @Override
    public int getItemCount() {
        return mSeed.size();
    }
}
