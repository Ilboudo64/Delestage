package com.example.delestage;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ZoneAdapter extends RecyclerView.Adapter<ZoneAdapter.ViewHolder> {
    private List<ZoneStats> zoneList;

    public ZoneAdapter(List<ZoneStats> zoneList) {
        this.zoneList = zoneList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_zone, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ZoneStats zone = zoneList.get(position);
        holder.tvRank.setText(String.valueOf(position + 1));
        holder.tvZoneName.setText(zone.getNomZone());
        holder.tvCount.setText(String.valueOf(zone.getNombreCoupures()));

        // Couleurs exactes du modèle
        if (position == 0) {
            holder.tvRank.getBackground().setTint(Color.parseColor("#FF3B30")); // Rouge vif
        } else if (position == 1) {
            holder.tvRank.getBackground().setTint(Color.parseColor("#FF9500")); // Orange
        } else if (position == 2) {
            holder.tvRank.getBackground().setTint(Color.parseColor("#FFCC00")); // Ambre/Jaune
        } else {
            holder.tvRank.getBackground().setTint(Color.parseColor("#8E8E93")); // Gris
        }
    }

    @Override
    public int getItemCount() {
        return Math.min(zoneList.size(), 5); // ON LIMITE À 5 ZONES ICI
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvRank, tvZoneName, tvCount;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvRank = itemView.findViewById(R.id.tvRank);
            tvZoneName = itemView.findViewById(R.id.tvZoneName);
            tvCount = itemView.findViewById(R.id.tvCount);
        }
    }
}