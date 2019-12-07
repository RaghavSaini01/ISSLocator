package com.example.isslocator;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class FlyOverRecyclerAdapter extends RecyclerView.Adapter<FlyOverRecyclerAdapter.MyViewHolder> {
    private LayoutInflater layoutInflater;
    private List<FlyOverData> flyOverDataList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView duration, time;

        public MyViewHolder(View itemView) {
            super(itemView);

            duration = itemView.findViewById(R.id.duration);
            time = itemView.findViewById(R.id.time);
        }
    }

    public FlyOverRecyclerAdapter(Context ctx, List<FlyOverData> dataModelArrayList){

        layoutInflater= LayoutInflater.from(ctx);
        flyOverDataList = dataModelArrayList;
    }

    @Override
    public FlyOverRecyclerAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = layoutInflater.inflate(R.layout.fly_over_recyclerview, parent, false);
        MyViewHolder holder = new MyViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(FlyOverRecyclerAdapter.MyViewHolder holder, int position) {

        holder.time.setText("Time of Day: " + flyOverDataList.get(position).getRiseTime());
        holder.duration.setText("Duration: " + flyOverDataList.get(position).getDuration());
    }

    @Override
    public int getItemCount() {
        return flyOverDataList.size();
    }
}
