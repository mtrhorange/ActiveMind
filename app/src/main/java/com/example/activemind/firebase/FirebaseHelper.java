package com.example.activemind.firebase;

import androidx.annotation.NonNull;

import com.applandeo.materialcalendarview.EventDay;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class FirebaseHelper {

    /**
     * Update game result data for current user
     * @param gameName name of the game user played
     * @param score score attained by user
     */
    public static void updateUserGameData(String gameName, int score) {
        FirebaseUser fbU = FirebaseAuth.getInstance().getCurrentUser();
        //update only if logged into firebase account
        if (fbU != null) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();

            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("uuuu-MM-dd");
            ZonedDateTime now = ZonedDateTime.now();
            ZonedDateTime sgDateTime = now.withZoneSameInstant(ZoneId.of("Asia/Shanghai"));

            DocumentReference docRef = db.collection("Users").document(fbU.getUid().toString()).collection(gameName).document(dtf.format(sgDateTime));

            //check if record for date exists
            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        //update high score for the day
                        if (score > Math.toIntExact(document.getLong("HighScore"))) {
                            docRef.update("HighScore", score);
                        }
                        //increment games played
                        docRef.update("GamesPlayed", FieldValue.increment(1));
                    }
                    else {
                        //create entry and document fields
                        Map<String, Object> addInfo = new HashMap<>();
                        addInfo.put("HighScore", score);
                        addInfo.put("GamesPlayed", 1);
                        docRef.set(addInfo);
                    }
                }
            });

            //create entry in history collection as well
            DocumentReference histDocRef = db.collection("Users").document(fbU.getUid().toString()).collection("History").document(dtf.format(sgDateTime));

            histDocRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    DocumentSnapshot document = task.getResult();
                    if (!(document.exists())) {
                        Map<String, Object> addInfo = new HashMap<>();
                        addInfo.put("Year", sgDateTime.getYear());
                        addInfo.put("Month", sgDateTime.getMonthValue());
                        addInfo.put("Day", sgDateTime.getDayOfMonth());
                        histDocRef.set(addInfo);
                    }
                }
            });
        }

    }

}
