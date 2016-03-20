package exun.cli.in.brinjal.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import exun.cli.in.brinjal.R;
import exun.cli.in.brinjal.model.TimingsList;

/**
 * Created by n00b on 3/9/2016.
 */
public class RVTimingAdapter extends RecyclerView.Adapter<RVTimingAdapter.DataHolderObject> {

    private static String LOG_TAG = "RVTimingAdapter";
    private List<TimingsList> timingItems;
    private static MyClickListener myClickListener;

    public static class DataHolderObject extends RecyclerView.ViewHolder
            implements View.OnClickListener {
        TextView day, shift1, shift2,closed;

        public DataHolderObject(final View itemView) {
            super(itemView);
            day = (TextView) itemView.findViewById(R.id.day);
            shift1 = (TextView) itemView.findViewById(R.id.timingShift1);
            shift2 = (TextView) itemView.findViewById(R.id.timingShift2);
            closed = (TextView) itemView.findViewById(R.id.timingClosed);
        }

        @Override
        public void onClick(View v) {
            myClickListener.onItemClick(getAdapterPosition(), v);
        }
    }

    public void setOnItemClickListener(MyClickListener myClickListener) {
        this.myClickListener = myClickListener;
    }

    public RVTimingAdapter(List<TimingsList> timingItems) {
        this.timingItems = timingItems;
    }

    @Override
    public DataHolderObject onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_row_timings, parent, false);

        DataHolderObject dataObjectHolder = new DataHolderObject(view);
        return dataObjectHolder;
    }

    @Override
    public void onBindViewHolder(DataHolderObject holder, int position) {
        holder.day.setText(timingItems.get(position).getDay());
        if (timingItems.get(position).isOpen()){
            String sShift1 = timingItems.get(position).getStartTime() + " a.m. - " +
                    timingItems.get(position).getBreakStart() + " p.m." ;
            holder.shift1.setText(sShift1);
            String sShift2 = timingItems.get(position).getBreakEnd() + " p.m. - " +
                    timingItems.get(position).getEndTime() + " p.m." ;
            holder.shift2.setText(sShift2);
        }
        else {
            holder.shift1.setVisibility(View.GONE);
            holder.shift2.setVisibility(View.GONE);
            holder.closed.setVisibility(View.VISIBLE);
        }
    }

    public void addItem(TimingsList dataObj, int index) {
        timingItems.add(index, dataObj);
        notifyItemInserted(index);
    }

    public void deleteItem(int index) {
        timingItems.remove(index);
        notifyItemRemoved(index);
    }

    @Override
    public int getItemCount() {
        return timingItems.size();
    }

    public interface MyClickListener {
        public void onItemClick(int position, View v);
    }
}