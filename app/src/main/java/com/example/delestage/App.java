package com.example.delestage;

import android.app.Application;
import com.google.firebase.database.FirebaseDatabase;

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        // C'est ici que la magie opère pour TOUTE l'application
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }
}
