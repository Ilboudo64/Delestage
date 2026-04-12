package com.example.delestage;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.database.FirebaseDatabase;
import java.util.List;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {

    private List<Signalement> historyList;
    private Context context; // Ajout du context pour l'AlertDialog

    // CORRECTION DU CONSTRUCTEUR
    public HistoryAdapter(List<Signalement> historyList, Context context) {
        this.historyList = historyList;
        this.context = context; // Maintenant le context est bien reçu !
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Utilise item_historique (le fichier que nous avons corrigé ensemble)
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_history, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Signalement s = historyList.get(position);

        // Affichage des données (Utilise les noms de ta classe Signalement)
        holder.txtLocation.setText(s.getVille());
        holder.txtDate.setText(s.getDateHeure()); // Changé s.getTemps() par s.getDateHeure()
        holder.txtConfirme.setText(s.getConfirmations() + " confirmations");
        holder.txtConteste.setText(s.getContestations() + " contestations");

        // Style dynamique selon le type (COUPURE ou RETOUR)
        if ("COUPURE".equalsIgnoreCase(s.getType())) {
            holder.imgIcon.setImageResource(R.drawable.ic_bolt_slash);
            holder.imgIcon.setColorFilter(Color.parseColor("#E53935"));
            holder.imgIcon.setBackgroundColor(Color.parseColor("#FFEBEB"));
            holder.txtStatus.setText("Coupure signalée");
            holder.txtStatus.setTextColor(Color.parseColor("#E53935"));
        } else {
            holder.imgIcon.setImageResource(R.drawable.ic_bolt);
            holder.imgIcon.setColorFilter(Color.parseColor("#10B981"));
            holder.imgIcon.setBackgroundColor(Color.parseColor("#E8F5E9"));
            holder.txtStatus.setText("Retour signalé");
            holder.txtStatus.setTextColor(Color.parseColor("#10B981"));
        }

        // Action du bouton supprimer (AlertDialog)
        holder.btnDelete.setOnClickListener(v -> {
            new AlertDialog.Builder(context)
                    .setTitle("Supprimer ?")
                    .setMessage("Voulez-vous supprimer ce signalement ?")
                    .setPositiveButton("Oui", (dialog, which) -> {
                        // Suppression dans Firebase via l'ID unique
                        FirebaseDatabase.getInstance().getReference("signalements")
                                .child(s.getId())
                                .removeValue()
                                .addOnSuccessListener(aVoid -> Toast.makeText(context, "Supprimé", Toast.LENGTH_SHORT).show());
                    })
                    .setNegativeButton("Non", null)
                    .show();
        });
    }

    @Override
    public int getItemCount() {
        return (historyList != null) ? historyList.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgIcon;
        ImageButton btnDelete;
        TextView txtStatus, txtDate, txtLocation, txtConfirme, txtConteste;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            // On s'assure que les IDs correspondent à ton item_historique.xml
            imgIcon = itemView.findViewById(R.id.imgStatus);
            btnDelete = itemView.findViewById(R.id.btnDelete);
            txtStatus = itemView.findViewById(R.id.txtStatusTitle);
            txtDate = itemView.findViewById(R.id.txtDate);
            txtLocation = itemView.findViewById(R.id.txtLocation);
            txtConfirme = itemView.findViewById(R.id.txtConfirmCount);
            txtConteste = itemView.findViewById(R.id.txtContestCount);
        }
    }
}