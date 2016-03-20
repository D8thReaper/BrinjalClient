package exun.cli.in.brinjal.adapter;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import exun.cli.in.brinjal.R;
import exun.cli.in.brinjal.model.CouponsList;

/**
 * Created by n00b on 3/9/2016.
 */
public class RVCouponsAdapter extends RecyclerView.Adapter<RVCouponsAdapter.DataHolderObject> {

    private static String LOG_TAG = "RVCouponsAdapter";
    private List<CouponsList> couponsItem;
    private static MyClickListener myClickListener;

    public static class DataHolderObject extends RecyclerView.ViewHolder
            implements View.OnClickListener {
        TextView sTitle;
        TextView sDescription;

        public DataHolderObject(final View itemView) {
            super(itemView);
            sTitle = (TextView) itemView.findViewById(R.id.couponsName);
            sDescription = (TextView) itemView.findViewById(R.id.couponsDescription);
            Log.i(LOG_TAG, "Adding Listener");
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            myClickListener.onItemClick(getAdapterPosition(), v);
        }
    }

    public void setOnItemClickListener(MyClickListener myClickListener) {
        this.myClickListener = myClickListener;
    }

    public RVCouponsAdapter(List<CouponsList> couponsItem) {
        this.couponsItem = couponsItem;
    }

    @Override
    public DataHolderObject onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_row_coupon, parent, false);

        DataHolderObject dataObjectHolder = new DataHolderObject(view);
        return dataObjectHolder;
    }

    @Override
    public void onBindViewHolder(DataHolderObject holder, int position) {
        holder.sTitle.setText(couponsItem.get(position).getTitle());
        holder.sDescription.setText(couponsItem.get(position).getDescription());
    }

    public void addItem(CouponsList dataObj, int index) {
        couponsItem.add(index, dataObj);
        notifyItemInserted(index);
    }

    public void deleteItem(int index) {
        couponsItem.remove(index);
        notifyItemRemoved(index);
    }

    @Override
    public int getItemCount() {
        return couponsItem.size();
    }

    public interface MyClickListener {
        public void onItemClick(int position, View v);
    }
}