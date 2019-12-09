package com.example.isslocator;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class AstroInfoRecyclerAdapter extends RecyclerView.Adapter<AstroInfoRecyclerAdapter.MyViewHolder> {
    private LayoutInflater layoutInflater;
    private List<AstroInfoData> astroInfoDataList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView astroName, astroLink;

        public MyViewHolder(View view) {
            super(view);

            astroName = view.findViewById(R.id.astro_name);
            astroLink = view.findViewById(R.id.astro_link);
        }
    }


    public AstroInfoRecyclerAdapter(Context ctx, List<AstroInfoData> dataModelArrayList) {

        layoutInflater= LayoutInflater.from(ctx);
        astroInfoDataList = dataModelArrayList;
    }

    @Override
    public AstroInfoRecyclerAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = layoutInflater.inflate(R.layout.astro_info_recyclerview, parent, false);
        MyViewHolder holder = new MyViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(AstroInfoRecyclerAdapter.MyViewHolder holder, int position) {
        holder.astroName.setText("Name: " + astroInfoDataList.get(position).getName());
        holder.astroLink.setText("Wikipedia: " + astroInfoDataList.get(position).getLink());
    }

    @Override
    public int getItemCount() {
        return astroInfoDataList.size();
    }
}
