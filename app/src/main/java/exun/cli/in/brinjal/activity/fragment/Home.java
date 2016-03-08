package exun.cli.in.brinjal.activity.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import exun.cli.in.brinjal.R;
import exun.cli.in.brinjal.activity.MainActivity;
import exun.cli.in.brinjal.helper.SessionManager;

/**
 * Created by n00b on 3/6/2016.
 */
public class Home extends Fragment {

    SessionManager sessionManager;

    public Home(){}

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);

        // Session manager
        sessionManager = new SessionManager(getActivity().getApplicationContext());


        TextView location = (TextView) rootView.findViewById(R.id.textViewLocation);
        ImageView btnQR = (ImageView) rootView.findViewById(R.id.buttonScan);
        SearchView searchView = (SearchView) rootView.findViewById(R.id.searchView);
        searchView.setQueryRefinementEnabled(true);
        searchView.setIconified(false);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Toast.makeText(getActivity(), "Abe " + query + " search karega? :/", Toast.LENGTH_SHORT).show();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
//                Toast.makeText(getContext(), "Abe " + newText + " change karega? :/", Toast.LENGTH_SHORT).show();
                return true;
            }
        });

        btnQR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity) getActivity()).displayView(2);
            }
        });

        String sLocation = "No location data found!";
        if (!sessionManager.getLocality().isEmpty() && !sessionManager.getCity().isEmpty())
            sLocation = "You are at " + sessionManager.getLocality() + ", " + sessionManager.getCity();

        location.setText(sLocation);

        return rootView;
    }
}
