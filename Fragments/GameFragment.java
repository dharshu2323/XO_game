package com.example.xo_game.Fragments;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.xo_game.ChessBoardAdapter;
import com.example.xo_game.R;

import java.util.ArrayList;


public class GameFragment extends Fragment {
    public static boolean turnO;
    private RecyclerView board;
    private ChessBoardAdapter chessBoardAdapter;
    public static boolean O=true;
    public static TextView turn,txt_win;
    private ImageView back;
    public static ImageView strokes,img_win;
    private Button reset,reset_back,reset_again;
    public static RelativeLayout win;
    public static String TAG =GameFragment.class.getName();


    public GameFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_game2, container, false);
        board=view.findViewById(R.id.board);
        turn=view.findViewById(R.id.turn);
        back=view.findViewById(R.id.back);
        reset=view.findViewById(R.id.rest);
        strokes=view.findViewById(R.id.strokes);
        win =view.findViewById(R.id.win);
        img_win=view.findViewById(R.id.img_win);
        txt_win=view.findViewById(R.id.txt_win);
        reset_again=view.findViewById(R.id.reset_again);
        reset_back=view.findViewById(R.id.reset_back);

        ArrayList<Bitmap> arrBas=new ArrayList<>();
        for(int i=0;i<9;i++){
            arrBas.add(null);
        }
        chessBoardAdapter =new ChessBoardAdapter(getContext(),arrBas);
        RecyclerView.LayoutManager layoutManager=new GridLayoutManager(getContext(),3);
        board.setLayoutManager(layoutManager);
        board.setAdapter(chessBoardAdapter);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentTransaction transaction=getActivity().getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.main_frame,new StartFragment());
                transaction.commit();
            }
        });
        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ArrayList<Bitmap> arrBas =new ArrayList<>();
                for (int i=0;i<9;i++){
                    arrBas.add(null);
                }
                chessBoardAdapter.setArrBas(arrBas);
                chessBoardAdapter.notifyDataSetChanged();
                O=true;
                turn.setText("X");
                strokes.setImageBitmap(null);
            }
        });
        reset_again.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentTransaction transaction=getActivity().getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.win,new GameFragment());
                transaction.commit();
            }
        });
        reset_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentTransaction transaction=getActivity().getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.win,new StartFragment());
                transaction.commit();
            }
        });
        return view;
    }

}