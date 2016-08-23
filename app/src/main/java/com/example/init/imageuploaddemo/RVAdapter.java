package com.example.init.imageuploaddemo;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.github.lzyzsd.circleprogress.DonutProgress;

import java.util.List;

public class RVAdapter extends RecyclerView.Adapter<RVAdapter.PersonViewHolder> {
    List<DataBean> dataBeen;

    RVAdapter(List<DataBean> dataBeen) {
        this.dataBeen = dataBeen;
    }

    public static class PersonViewHolder extends RecyclerView.ViewHolder
    {

        ImageView imageView;
        DonutProgress prog;

        PersonViewHolder(View itemView)
        {
            super(itemView);

            imageView = (ImageView) itemView.findViewById(R.id.imageview);
            prog= (DonutProgress) itemView.findViewById(R.id.donut_progress);

        }
    }

    @Override
    public int getItemCount() {
        return dataBeen.size();
    }

    @Override
    public PersonViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.share_image_layout, viewGroup, false);
        PersonViewHolder pvh = new PersonViewHolder(v);
        return pvh;
    }

    @Override
    public void onBindViewHolder(PersonViewHolder personViewHolder, int i) {
new UploadFileToServer(personViewHolder.prog,dataBeen.get(i).getImageurl()).execute();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }
}