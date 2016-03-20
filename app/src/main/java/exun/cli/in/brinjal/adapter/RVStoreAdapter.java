package exun.cli.in.brinjal.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import exun.cli.in.brinjal.R;
import exun.cli.in.brinjal.model.StoreList;

/**
 * Created by n00b on 3/7/2016.
 */
public class RVStoreAdapter extends RecyclerView.Adapter<RVStoreAdapter.DataHolderObject> {

    private static String LOG_TAG = "RVBlogAdapter";
    private List<StoreList> storeLists;
    private static MyClickListener myClickListener;
    Location location1 = new Location("");
    Context context;

    public static class DataHolderObject extends RecyclerView.ViewHolder
            implements View.OnClickListener {
        TextView sName,sDistance,sLocation;
        ImageView directions;

        public DataHolderObject(View itemView) {
            super(itemView);
            sName = (TextView) itemView.findViewById(R.id.storeName);
            sDistance = (TextView) itemView.findViewById(R.id.storeDistance);
            sLocation = (TextView) itemView.findViewById(R.id.storeLocation);
            directions = (ImageView) itemView.findViewById(R.id.btnLocate);
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

    public RVStoreAdapter(Context context,List<StoreList> storeLists, double lat, double longi) {
        this.storeLists = storeLists;
        location1.setLatitude(lat);
        location1.setLongitude(longi);
        this.context = context;
    }

    @Override
    public DataHolderObject onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_row_store, parent, false);

        DataHolderObject dataObjectHolder = new DataHolderObject(view);
        return dataObjectHolder;
    }

    @Override
    public void onBindViewHolder(final DataHolderObject holder, final int position) {
        holder.sName.setText(storeLists.get(position).getTitle());
        Location location2 = new Location("");
        location2.setLatitude(storeLists.get(position).getLati());
        location2.setLongitude(storeLists.get(position).getLongi());
        float disInM = location1.distanceTo(location2);
        storeLists.get(position).setDisInM(disInM);
        String distance;
        if (disInM/1000.0 < 1) {
            distance = String.format("%.02f",disInM) + " m away from you";
        }
        else {
            disInM /= 1000.0;
            distance = String.format("%.02f",disInM) + " KM away from you";
        }

        holder.sDistance.setText(distance);
        holder.sLocation.setText(storeLists.get(position).getLocality());
        holder.directions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMap(storeLists.get(position).getLati(), storeLists.get(position).getLongi());
            }
        });
    }

    public void animateTo(List<StoreList> models) {
        applyAndAnimateRemovals(models);
        applyAndAnimateAdditions(models);
        applyAndAnimateMovedItems(models);
    }

    private void applyAndAnimateMovedItems(List<StoreList> newModels) {
        for (int toPosition = newModels.size() - 1; toPosition >= 0; toPosition--) {
            final StoreList model = newModels.get(toPosition);
            final int fromPosition = storeLists.indexOf(model);
            if (fromPosition >= 0 && fromPosition != toPosition) {
                moveItem(fromPosition, toPosition);
            }
        }
    }

    private void applyAndAnimateAdditions(List<StoreList> newModels) {
        for (int i = 0, count = newModels.size(); i < count; i++) {
            final StoreList model = newModels.get(i);
            if (!storeLists.contains(model)) {
                addItem(model, i);
            }
        }
    }

    private void applyAndAnimateRemovals(List<StoreList> newModels) {
        for (int i = storeLists.size() - 1; i >= 0; i--) {
            final StoreList model = storeLists.get(i);
            if (!newModels.contains(model)) {
                deleteItem(i);
            }
        }
    }

    public void addItem(StoreList dataObj, int index) {
        storeLists.add(index, dataObj);
        notifyItemInserted(index);
    }

    public StoreList deleteItem(int index) {
        final StoreList model = storeLists.remove(index);
        notifyItemRemoved(index);
        return model;
    }

    public void moveItem(int fromPosition, int toPosition) {
        final StoreList model = storeLists.remove(fromPosition);
        storeLists.add(toPosition, model);
        notifyItemMoved(fromPosition, toPosition);
    }

    @Override
    public int getItemCount() {
        return storeLists.size();
    }

    public interface MyClickListener {
        public void onItemClick(int position, View v);
    }

    public void showMap(double PLACE_LATITUDE, double PLACE_LONGITUDE) {

        boolean installedMaps = false;

        // CHECK IF GOOGLE MAPS IS INSTALLED
        PackageManager pkManager = context.getPackageManager();
        try {
            @SuppressWarnings("unused")
            PackageInfo pkInfo = pkManager.getPackageInfo("com.google.android.apps.maps", 0);
            installedMaps = true;
        } catch (Exception e) {
            e.printStackTrace();
            installedMaps = false;
        }

        // SHOW THE MAP USING CO-ORDINATES FROM THE CHECKIN
        if (installedMaps == true) {
            String geoCode = "geo:0,0?q=" + PLACE_LATITUDE + ","
                    + PLACE_LONGITUDE;
            Intent sendLocationToMap = new Intent(Intent.ACTION_VIEW,
                    Uri.parse(geoCode));
            context.startActivity(sendLocationToMap);
        } else if (installedMaps == false) {

            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                    context);

            // SET THE ICON
            alertDialogBuilder.setIcon(R.drawable.ic_map);

            // SET THE TITLE
            alertDialogBuilder.setTitle("Google Maps Not Found");

            // SET THE MESSAGE
            alertDialogBuilder
                    .setMessage("Maps application couldn't be found. This feature requires Google Maps!")
                    .setCancelable(false)
                    .setNeutralButton("Got It",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int id) {
                                    dialog.dismiss();
                                }
                            });

            // CREATE THE ALERT DIALOG
            AlertDialog alertDialog = alertDialogBuilder.create();

            // SHOW THE ALERT DIALOG
            alertDialog.show();
        }
    }

    public void setList(List<StoreList> mList){
        this.storeLists = new ArrayList<>(mList);
    }
}
