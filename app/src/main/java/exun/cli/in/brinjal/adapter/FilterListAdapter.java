package exun.cli.in.brinjal.adapter;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import java.util.List;

import exun.cli.in.brinjal.R;
import exun.cli.in.brinjal.model.Filter;

/**
 * Created by n00b on 3/11/2016.
 */
public class FilterListAdapter extends BaseAdapter {
    private Activity activity;
    private LayoutInflater inflater;
    private List<Filter> filterItems;


    public FilterListAdapter(Activity activity, List<Filter> filterItems) {
        this.activity = activity;
        this.filterItems = filterItems;
    }

    @Override
    public int getCount() {
        return filterItems.size();
    }

    @Override
    public Object getItem(int location) {
        return filterItems.get(location);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        if (inflater == null)
            inflater = (LayoutInflater) activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null)
            convertView = inflater.inflate(R.layout.list_row_filter, null);

        CheckBox box = (CheckBox) convertView.findViewById(R.id.cbFilter);

        // getting Filter data for the row
        Filter m = filterItems.get(position);

        // title
        box.setText(m.getTag());

        box.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                filterItems.get(position).setIsChecked(isChecked);

                Log.d("Filter adapter", "Changed " + filterItems.get(position).getTag() + " to " + filterItems.get(position).getIsChecked());
            }
        });

        return convertView;
    }

    public void uncheckAllChildrenCascade(ViewGroup vg) {
        for (int i = 0; i < vg.getChildCount(); i++) {
            View v = vg.getChildAt(i);
            if (v instanceof CheckBox) {
                ((CheckBox) v).setChecked(false);
            } else if (v instanceof ViewGroup) {
                uncheckAllChildrenCascade((ViewGroup) v);
            }
        }
    }

}
