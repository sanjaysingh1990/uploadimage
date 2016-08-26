package com.example.init.imageuploaddemo;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.github.lzyzsd.circleprogress.DonutProgress;
import com.nostra13.universalimageloader.core.download.ImageDownloader;

import java.util.List;

public class RVAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements MyCallBack{
    List<DataBean> dataBeen;
    Activity con;
    InitializeLib lib;
    final int LOCAL_UPLOAD=1,LOCAL_SHOW=2,ONLINE_SHOW=3;
    RVAdapter(List<DataBean> dataBeen,Activity c) {
        this.dataBeen = dataBeen;
        lib= (InitializeLib) c.getApplication();
        con=c;

    }

    @Override
    public void callback(int pos)
    {
        Toast.makeText(con,"position called"+pos,Toast.LENGTH_SHORT).show();
        DataBean db=dataBeen.get(pos);
        db.setViewtype(2);
        notifyItemChanged(pos);
    }

    public static class Local_Upload extends RecyclerView.ViewHolder
    {

        ImageView imageView;
        DonutProgress prog;

        Local_Upload(View itemView)
        {
            super(itemView);

            imageView = (ImageView) itemView.findViewById(R.id.imageview);
            prog= (DonutProgress) itemView.findViewById(R.id.donut_progress);

        }
    }

    public static class Local_Show extends RecyclerView.ViewHolder
    {

        ImageView imageView;
        DonutProgress prog;

        Local_Show(View itemView)
        {
            super(itemView);

            imageView = (ImageView) itemView.findViewById(R.id.imageview);
            prog= (DonutProgress) itemView.findViewById(R.id.donut_progress);
            prog.setVisibility(View.GONE);

        }
    }

    @Override
    public int getItemCount() {
        return dataBeen.size();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        RecyclerView.ViewHolder viewHolder;
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());

        switch (viewType) {
            case LOCAL_UPLOAD:
                View v1 = inflater.inflate(R.layout.share_image_layout, viewGroup, false);
                viewHolder = new Local_Upload(v1);
                break;
            case LOCAL_SHOW:
                View v2 = inflater.inflate(R.layout.share_image_layout, viewGroup, false);
                viewHolder = new Local_Show(v2);
                break;
            default:
                View v = inflater.inflate(R.layout.share_image_layout, viewGroup, false);
                viewHolder = new Local_Show(v);
                break;
        }
        return viewHolder;

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {

        switch (viewHolder.getItemViewType()) {
            case LOCAL_UPLOAD:
                Local_Upload localUpload = (Local_Upload) viewHolder;
                new UploadFileToServer(localUpload.prog,dataBeen.get(i).getImageurl(),this,i).execute();
                lib.imageLoader.displayImage(ImageDownloader.Scheme.FILE.wrap(dataBeen.get(i).getImageurl()),localUpload.imageView, lib.options);

                break;
            case LOCAL_SHOW:
                Local_Show localShow = (Local_Show) viewHolder;
                lib.imageLoader.displayImage(ImageDownloader.Scheme.FILE.wrap(dataBeen.get(i).getImageurl()),localShow.imageView, lib.options);

                break;
            }
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }
    @Override
    public int getItemViewType(int position) {
        if (dataBeen.get(position).getViewtype()==LOCAL_UPLOAD) {
            return LOCAL_UPLOAD;
        } else if (dataBeen.get(position).getViewtype()==LOCAL_SHOW) {
            return LOCAL_SHOW;
        }
        return -1;
    }

}