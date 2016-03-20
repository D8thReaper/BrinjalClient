package exun.cli.in.brinjal.activity.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import exun.cli.in.brinjal.R;
import exun.cli.in.brinjal.activity.MainActivity;

/**
 * Created by n00b on 3/6/2016.
 */
public class Home extends Fragment {

    private CardView searchViewLayout;

    public Home(){}

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);

        ImageView btnQR = (ImageView) rootView.findViewById(R.id.buttonScan);

        searchViewLayout = (CardView) rootView.findViewById(R.id.searchViewLayout);

        searchViewLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("Main", "Kiddan?");
                ((MainActivity) getActivity()).displayView(4);
            }
        });

        btnQR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity) getActivity()).displayView(2);
            }
        });

        return rootView;
    }
}
