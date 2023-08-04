package com.example.xo_game;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.xo_game.Fragments.GameFragment;

import java.util.ArrayList;

public class ChessBoardAdapter extends RecyclerView.Adapter<ChessBoardAdapter.ViewHolder> {
    private Context context;
    private ArrayList<Bitmap> arrBas,arrTest;
    private ArrayList<Bitmap> arrStroke;
    private Bitmap bnX, bnO;
    private Animation stroke_an, win;
    private String winCharcter = "o";
    private boolean checkMax=true;
    private int depth=0;


    public ChessBoardAdapter(Context context, ArrayList<Bitmap> arrBas) {
        this.context = context;
        this.arrBas = arrBas;
        bnO = BitmapFactory.decodeResource(context.getResources(), R.drawable.o);
        bnX = BitmapFactory.decodeResource(context.getResources(), R.drawable.x);
        arrStroke = new ArrayList<>();
        arrStroke.add(BitmapFactory.decodeResource(context.getResources(), R.drawable.stroke1));
        arrStroke.add(BitmapFactory.decodeResource(context.getResources(), R.drawable.stroke2));
        arrStroke.add(BitmapFactory.decodeResource(context.getResources(), R.drawable.stroke3));
        arrStroke.add(BitmapFactory.decodeResource(context.getResources(), R.drawable.stroke4));
        arrStroke.add(BitmapFactory.decodeResource(context.getResources(), R.drawable.stroke5));
        arrStroke.add(BitmapFactory.decodeResource(context.getResources(), R.drawable.stroke6));
        arrStroke.add(BitmapFactory.decodeResource(context.getResources(), R.drawable.stroke7));
        arrStroke.add(BitmapFactory.decodeResource(context.getResources(), R.drawable.stroke8));

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.chess, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder  holder, int position) {
        holder.cell.setImageBitmap(arrBas.get(position));
        stroke_an = AnimationUtils.loadAnimation(context, R.anim.stroke_an);
        GameFragment.strokes.setAnimation(stroke_an);
        win = AnimationUtils.loadAnimation(context, R.anim.win);
        if (MainActivity.multiplayer){
            play2player(holder,position);
        }else{
            computer(holder,position);
        }
        if (!checkwin()) checkDraw();
        
    }
    private void checkDraw() {
        int count = 0;
        for (int i = 0; i < arrBas.size(); i++) {
            if (arrBas.get(i) != null) {
                count++;
            }
        }
        if (count == 9) {
            GameFragment.txt_win.setVisibility(View.VISIBLE);
            GameFragment.txt_win.setAnimation(win);
            GameFragment.txt_win.setText("DRAW");
        }
    }

    private void play2player(ViewHolder holder, int position) {
        holder.cell.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(arrBas.get(position)==null&&!checkwin()){
                    if(GameFragment.turnO){
                        arrBas.set(position,bnO);
                        GameFragment.turnO=false;
                        GameFragment.turn.setText("X");
                    }else {
                        arrBas.set(position,bnX);
                        GameFragment.turnO=true;
                        GameFragment.turn.setText("O");
                    }
                    if (checkwin()){
                        win();
                    }
                    notifyItemChanged(position);
                }
            }
        });
    }

    private void computer(ViewHolder holder, int position) {
        holder.cell.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(arrBas.get(position)==null&&!checkwin()&&GameFragment.turnO){
                    if(GameFragment.turnO){
                        arrBas.set(position, bnO);
                        GameFragment.turnO = false;
                        GameFragment.turn.setText("X");
                    }
                    if(checkwin()){
                        win();
                    }
                    notifyItemChanged(position);
                    Handler handler=new Handler();
                    Runnable r =new Runnable() {
                        @Override
                        public void run() {
                            arrTest=new ArrayList<>(arrBas);
                            ArrayList<Mark> arrMark=solver(bnX);
                            if(arrMark.size()>0){
                                int max=arrMark.get(0).getPoint();
                                int id=0;
                                for(int i=0;i<arrMark.size();i++){
                                    if (max<arrMark.get(i).getPoint()){
                                        max=arrMark.get(i).getPoint();
                                        id=i;
                                    }
                                }
                                int p=id;
                                arrBas.set(arrMark.get(p).getId(), bnX);
                                if(checkwin()){
                                    win();
                                }
                                else {
                                    GameFragment.turnO=true;
                                    GameFragment.txt_win.setText("O");
                                }
                                notifyItemChanged(arrMark.get(p).getId());
                            }
                        }
                    };
                    if (!checkwin()){
                        handler.postDelayed(r,1000);
                    }

                }

            }
        });
    }
    private ArrayList<Mark> solver(Bitmap bm) {
        ArrayList<Mark> arrPoints = new ArrayList<>();
        for (int i = 0; i < 9; i++) {
            if (arrTest.get(i) == null) {
                arrTest.set(i, bm); // Set the current player's move (bm) on the board

                int result = checkwinTmp(bm);
                if (result == -100) {
                    // The game is not over yet; recursively call solver to simulate the next move
                    ArrayList<Mark> arr;
                    if (bm == bnX) {
                        depth++;
                        arr = solver(bnO); // Switch to the other player's move (bnO) for recursion
                        depth--;
                        GameFragment.turn.setText("O");
                    } else {
                        depth++;
                        arr = solver(bnX); // Switch to the other player's move (bnX) for recursion
                        depth--;
                        GameFragment.turn.setText("X");
                    }
                    int bestScore = (bm == bnX) ? Integer.MIN_VALUE : Integer.MAX_VALUE;
                    int bestMove = -1;
                    for (int j = 0; j < arr.size(); j++) {
                        int point = arr.get(j).getPoint();
                        if ((bm == bnX && point > bestScore) || (bm == bnO && point < bestScore)) {
                            bestScore = point;
                            bestMove = arr.get(j).getId();
                        }
                    }
                    if (bestMove != -1) {
                        arrPoints.add(new Mark(i, bestScore));
                    }
                } else {
                    // Game over, assign points based on the result
                    if (bm == bnX) {
                        arrPoints.add(new Mark(i, result - depth));
                    } else {
                        arrPoints.add(new Mark(i, -(result - depth)));
                    }
                }

                arrTest.set(i, null); // Reset the board state for backtracking
            }
        }
        return arrPoints;
    }




    private int checkwinTmp(Bitmap bm) {
        int countRow = 0;
        for (int i = 0; i < 9; i++){
            if(i%3==0){
                countRow = 0;
            }
            if(arrTest.get(i)==bm){
                countRow++;
            }
            if (countRow==3){
                return 10;
            }
        }
        if (arrTest.get(0)==arrTest.get(3)&&arrTest.get(3)==arrTest.get(6)&&arrTest.get(0)==bm
                ||arrTest.get(1)==arrTest.get(4)&&arrTest.get(4)==arrTest.get(7)&&arrTest.get(1)==bm
                ||arrTest.get(2)==arrTest.get(5)&&arrTest.get(5)==arrTest.get(8)&&arrTest.get(2)==bm){
            return 10;
        }
        if (arrTest.get(0)==arrTest.get(4)&&arrTest.get(4)==arrTest.get(8)&&arrTest.get(0)==bm) return 10;
        if (arrTest.get(2)==arrTest.get(4)&&arrTest.get(4)==arrTest.get(6)&&arrTest.get(2)==bm) return 10;
        int count = 0;
        for (int i = 0; i < 9; i++){
            if (arrTest.get(i)!=null){
                count++;
            }
        }
        if (count==9){
            return 0;
        }
        return -100;
    }




    private void win() {
        GameFragment.strokes.setAnimation(stroke_an);
        GameFragment.win.setAnimation(win);
        GameFragment.win.setVisibility(View.VISIBLE);
        GameFragment.win.startAnimation(win);
        if (winCharcter.equals("o")) {
            GameFragment.img_win.setImageBitmap(bnO);
        } else {
            GameFragment.img_win.setImageBitmap(bnX);
        }
        GameFragment.txt_win.setText("WIN");
    }

    private boolean checkwin() {
        if (arrBas.get(0) == arrBas.get(3)&& arrBas.get(3)==arrBas.get(6) && arrBas.get(0) != null){
            GameFragment.strokes.setImageBitmap(arrStroke.get(2));
            Checkwincharacter(0);
            return true;
        }else if (arrBas.get(1) == arrBas.get(4)&& arrBas.get(4)==arrBas.get(7) && arrBas.get(1) != null) {
            GameFragment.strokes.setImageBitmap(arrStroke.get(3));
            Checkwincharacter(1);
            return true;
        }
        else if (arrBas.get(2) == arrBas.get(5)&& arrBas.get(5)==arrBas.get(8) && arrBas.get(2) != null) {
            GameFragment.strokes.setImageBitmap(arrStroke.get(4));
            Checkwincharacter(2);
            return true;
        }
        else if (arrBas.get(0) == arrBas.get(1)&& arrBas.get(1)==arrBas.get(2) && arrBas.get(0) !=null){
            GameFragment.strokes.setImageBitmap(arrStroke.get(5));
            Checkwincharacter(0);
            return true;
        }
        else if (arrBas.get(3) == arrBas.get(4)&& arrBas.get(4)==arrBas.get(5) && arrBas.get(3) != null) {
            GameFragment.strokes.setImageBitmap(arrStroke.get(6));
            Checkwincharacter(3);
            return true;
        }
        else if (arrBas.get(6) == arrBas.get(7)&& arrBas.get(7)==arrBas.get(8) && arrBas.get(6) != null) {
            GameFragment.strokes.setImageBitmap(arrStroke.get(7));
            Checkwincharacter(6);
            return true;
        }
        else if (arrBas.get(0) == arrBas.get(4)&& arrBas.get(4)==arrBas.get(8) && arrBas.get(0) != null) {
            GameFragment.strokes.setImageBitmap(arrStroke.get(1));
            Checkwincharacter(0);
            return true;
        }
        else if (arrBas.get(2) == arrBas.get(4)&& arrBas.get(4)==arrBas.get(6) && arrBas.get(2) != null) {
            GameFragment.strokes.setImageBitmap(arrStroke.get(0));
            Checkwincharacter(2);
            return true;
        }
        return false;

    }


    private void Checkwincharacter(int i) {
        if (arrBas.get(i) == bnO) {
            winCharcter = "o";
        } else {
            winCharcter = "x";
        }
    }

    @Override
    public int getItemCount() {
        return arrBas.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView cell;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cell = itemView.findViewById(R.id.cell);
        }
    }

    public ArrayList<Bitmap> getArrBas() {
        return arrBas;
    }

    public void setArrBas(ArrayList<Bitmap> arrBas) {
        this.arrBas = arrBas;
    }
}
