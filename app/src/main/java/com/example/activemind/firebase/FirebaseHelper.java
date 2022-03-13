package com.example.activemind.firebase;

import android.widget.Toast;

import androidx.annotation.NonNull;

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

import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class FirebaseHelper {

    /**
     * Update game result data for current user
     * @param gameName name of the game played: [NUMBER; SEQUENCE; WORD]
     */
    public static void updateUserGameData(String gameName, int score) {
        FirebaseUser fbU = FirebaseAuth.getInstance().getCurrentUser();
        //update only if logged into firebase account
        if (fbU != null) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();

            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("uuuu-MM-dd");
            ZonedDateTime now = ZonedDateTime.now();
            ZonedDateTime sgDateTime = now.withZoneSameInstant(ZoneId.of("Asia/Shanghai"));
//            System.out.println(dtf.format(sgDateTime));
//            System.out.println(sgDateTime.getOffset());
//            System.out.println(sgDateTime.getYear());
//            System.out.println(sgDateTime.getMonthValue());
//            System.out.println(sgDateTime.getDayOfMonth());
//            System.out.println(sgDateTime.getHour());
//            System.out.println(sgDateTime.getMinute());
//            System.out.println(sgDateTime.getSecond());

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
                        Map<String, Object> addInfo = new HashMap<>();
                        addInfo.put("HighScore", score);
                        addInfo.put("GamesPlayed", 1);
                        docRef.set(addInfo);
                    }
                }
            });
        }

    }

}
