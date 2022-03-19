package com.example.activemind;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.applandeo.materialcalendarview.CalendarView;
import com.applandeo.materialcalendarview.EventDay;
import com.example.activemind.databinding.FragmentHistoryBinding;
import com.example.activemind.firebase.FirebaseHelper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class HistoryFragment extends Fragment {

    public HistoryFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    FragmentHistoryBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentHistoryBinding.inflate(inflater, container, false);
        // Inflate the layout for this fragment

        setupCalendar();

        return binding.getRoot();
    }

    /**
     * Setup calendar markings based on firebase data
     */
    private void setupCalendar() {
        FirebaseUser fbU = FirebaseAuth.getInstance().getCurrentUser();
        //proceed only if logged into firebase account
        if (fbU != null) {
            List<EventDay> events = new ArrayList<>();

            FirebaseFirestore db = FirebaseFirestore.getInstance();
            CollectionReference colRef = db.collection("Users").document(fbU.getUid().toString()).collection("History");

            ZonedDateTime now = ZonedDateTime.now();
            ZonedDateTime sgDateTime = now.withZoneSameInstant(ZoneId.of("Asia/Shanghai"));

            colRef.whereEqualTo("Year", sgDateTime.getYear()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot doc : task.getResult()) {
                            Calendar calendar = new GregorianCalendar(sgDateTime.getYear(),
                                    Math.toIntExact(doc.getLong("Month") - 1),
                                    Math.toIntExact(doc.getLong("Day")));
                            events.add(new EventDay(calendar, R.drawable.check));
                        }

                        CalendarView calendarView = binding.calendarView;
                        calendarView.setEvents(events);
                    }
                }
            });
        }
        //prompt user to log in to be able to view
        else {
            Toast.makeText(getContext(), "Log in to view history", Toast.LENGTH_SHORT).show();
        }
    }

}