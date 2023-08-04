package com.example.xo_game.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.xo_game.MainActivity;
import com.example.xo_game.R;


public class StartFragment extends Fragment {
    private Button start;
    private RadioButton players,computer ;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_start, container, false);
        start=view.findViewById(R.id.start);
        players=view.findViewById(R.id.players);
        computer=view.findViewById(R.id.computer);
        players.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity.multiplayer=true;
            }
        });
        computer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity.multiplayer=false;
            }
        });
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick( View view) {
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.main_frame,new GameFragment());
                transaction.commit();
            }

        });
        return view;
    }
}