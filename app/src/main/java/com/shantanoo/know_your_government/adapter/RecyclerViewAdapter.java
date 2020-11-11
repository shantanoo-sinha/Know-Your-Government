package com.shantanoo.know_your_government.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.shantanoo.know_your_government.MainActivity;
import com.shantanoo.know_your_government.R;
import com.shantanoo.know_your_government.model.Official;
import com.shantanoo.know_your_government.viewholder.ViewHolder;

import java.util.List;

/**
 * Created by Shantanoo on 10/25/2020.
 */
public class RecyclerViewAdapter extends RecyclerView.Adapter<ViewHolder> {

    private final List<Official> officials;
    private final MainActivity mainActivity;

    public RecyclerViewAdapter(MainActivity mainActivity, List<Official> officials) {
        this.mainActivity = mainActivity;
        this.officials = officials;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_view_row, parent, false);
        view.setOnClickListener(mainActivity);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Official official = officials.get(position);
        holder.bind(official);
    }

    @Override
    public int getItemCount() {
        return officials.size();
    }
}
