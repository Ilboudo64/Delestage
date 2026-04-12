package com.example.delestage;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class InfoAdapter extends RecyclerView.Adapter<InfoAdapter.ViewHolder> {

    private List<InfoOfficielle> infoList;

    public InfoAdapter(List<InfoOfficielle> infoList) {
        this.infoList = infoList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_info_officielle, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        InfoOfficielle info = infoList.get(position);

        holder.txtTitre.setText(info.titre);
        holder.txtDescription.setText(info.description);
        holder.txtPeriode.setText(info.periode);
        holder.txtZones.setText(info.zones);

        // Gestion du badge de statut (En cours / Terminé)
        if (info.status != null && info.status.equalsIgnoreCase("Terminé")) {
            holder.badgeStatus.setText("● Terminé");
            holder.badgeStatus.setBackgroundResource(R.drawable.badge_green_light); // À créer si besoin
            holder.badgeStatus.setTextColor(android.graphics.Color.parseColor("#4CAF50"));
        } else {
            holder.badgeStatus.setText("● En cours");
            holder.badgeStatus.setBackgroundResource(R.drawable.badge_red_light);
            holder.badgeStatus.setTextColor(android.graphics.Color.parseColor("#E53935"));
        }
    }

    @Override
    public int getItemCount() {
        return infoList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtTitre, txtDescription, txtPeriode, txtZones, badgeStatus;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtTitre = itemView.findViewById(R.id.txtTitreInfo);
            txtDescription = itemView.findViewById(R.id.txtDescriptionInfo);
            txtPeriode = itemView.findViewById(R.id.txtPeriodeInfo);
            txtZones = itemView.findViewById(R.id.txtZonesInfo);
            badgeStatus = itemView.findViewById(R.id.badgeStatusInfo);
        }
    }
}