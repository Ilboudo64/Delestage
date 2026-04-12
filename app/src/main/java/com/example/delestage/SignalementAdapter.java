package com.example.delestage;

import android.graphics.Color;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;

import java.util.List;

public class SignalementAdapter extends RecyclerView.Adapter<SignalementAdapter.MyViewHolder> {

    private List<Signalement> signalementList;

    public SignalementAdapter(List<Signalement> signalementList) {
        this.signalementList = signalementList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_recent, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Signalement s = signalementList.get(position);

        holder.nom.setText(s.getAuteur());
        holder.quartier.setText(s.getVille());
        holder.temps.setText(s.getDateHeure());

        // --- STATISTIQUES ---
        String confirmations = "<font color='#2E7D32'><b>" + s.getConfirmations() + "</b></font> confirment";
        String contestations = " • <font color='#D32F2F'><b>" + s.getContestations() + "</b></font> contestent";

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            holder.txtCompteurs.setText(Html.fromHtml(confirmations + contestations, Html.FROM_HTML_MODE_LEGACY));
        } else {
            holder.txtCompteurs.setText(Html.fromHtml(confirmations + contestations));
        }

        // --- BADGE TYPE ---
        holder.badge.setText(s.getType());
        if ("RETOUR".equalsIgnoreCase(s.getType())) {
            holder.badge.setTextColor(Color.parseColor("#2E7D32"));
            holder.badge.setBackgroundResource(R.drawable.bg_badge_green);
        } else {
            holder.badge.setTextColor(Color.parseColor("#D32F2F"));
            holder.badge.setBackgroundResource(R.drawable.bg_badge_red);
        }

        resetButtonStyle(holder);

// --- CLIC CONFIRMER ---
        holder.btnConfirmer.setOnClickListener(v -> {
            if (s.getId() == null) {
                Toast.makeText(v.getContext(), "Erreur : ID introuvable", Toast.LENGTH_SHORT).show();
                return;
            }
            appliquerStyleSelection(holder.btnConfirmer, "#2E7D32", holder.btnContester);
            incrementerCompteur(s.getId(), "confirmations");
        });

// --- CLIC CONTESTER ---
        holder.btnContester.setOnClickListener(v -> {
            if (s.getId() == null) {
                Toast.makeText(v.getContext(), "Erreur : ID introuvable", Toast.LENGTH_SHORT).show();
                return;
            }
            appliquerStyleSelection(holder.btnContester, "#D32F2F", holder.btnConfirmer);
            incrementerCompteur(s.getId(), "contestations");
        });
// --- CLIC CONFIRMER ---
        holder.btnConfirmer.setOnClickListener(v -> {
            if (s.getId() == null) {
                Toast.makeText(v.getContext(), "Erreur : ID introuvable", Toast.LENGTH_SHORT).show();
                return;
            }
            appliquerStyleSelection(holder.btnConfirmer, "#2E7D32", holder.btnContester);
            incrementerCompteur(s.getId(), "confirmations");
        });

// --- CLIC CONTESTER ---
        holder.btnContester.setOnClickListener(v -> {
            if (s.getId() == null) {
                Toast.makeText(v.getContext(), "Erreur : ID introuvable", Toast.LENGTH_SHORT).show();
                return;
            }
            appliquerStyleSelection(holder.btnContester, "#D32F2F", holder.btnConfirmer);
            incrementerCompteur(s.getId(), "contestations");
        });
    }

    // --- LOGIQUE FIREBASE ---
    private void incrementerCompteur(String signalementId, String champ) {
        if (signalementId == null) return;
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("signalements").child(signalementId).child(champ);
        ref.runTransaction(new Transaction.Handler() {
            @NonNull
            @Override
            public Transaction.Result doTransaction(@NonNull MutableData currentData) {
                Integer val = currentData.getValue(Integer.class);
                currentData.setValue((val == null) ? 1 : val + 1);
                return Transaction.success(currentData);
            }
            @Override
            public void onComplete(DatabaseError e, boolean c, DataSnapshot d) {}
        });
    }

    // --- STYLES ---
    private void appliquerStyleSelection(MaterialButton btnCliqué, String colorHex, MaterialButton btnAutre) {
        btnCliqué.setBackgroundColor(Color.parseColor(colorHex));
        btnCliqué.setTextColor(Color.WHITE);
        btnCliqué.setEnabled(false);
        btnAutre.setEnabled(false);
        btnAutre.setAlpha(0.3f);
    }

    private void resetButtonStyle(MyViewHolder holder) {
        holder.btnConfirmer.setEnabled(true);
        holder.btnContester.setEnabled(true);
        holder.btnConfirmer.setAlpha(1.0f);
        holder.btnContester.setAlpha(1.0f);
        holder.btnConfirmer.setBackgroundColor(Color.TRANSPARENT);
        holder.btnContester.setBackgroundColor(Color.TRANSPARENT);
    }

    @Override
    public int getItemCount() {
        return (signalementList != null) ? signalementList.size() : 0;
    }

    // UNE SEULE CLASSE VIEWHOLDER A LA FIN
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView nom, quartier, badge, txtCompteurs, temps;
        MaterialButton btnConfirmer, btnContester;
        ImageView imgAvatar, btnSupprimer;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            nom = itemView.findViewById(R.id.txtNomAuteur);
            temps = itemView.findViewById(R.id.txtTemps);
            quartier = itemView.findViewById(R.id.txtLocalisation);
            badge = itemView.findViewById(R.id.badgeType);
            txtCompteurs = itemView.findViewById(R.id.txtCompteurs);
            btnConfirmer = itemView.findViewById(R.id.btnConfirmer);
            btnContester = itemView.findViewById(R.id.btnContester);
            imgAvatar = itemView.findViewById(R.id.imgAvatar);
            btnSupprimer = itemView.findViewById(R.id.btnDelete); // Ton ID XML
        }
    }
}