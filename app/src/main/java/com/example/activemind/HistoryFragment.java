package com.example.activemind;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.applandeo.materialcalendarview.CalendarView;
import com.applandeo.materialcalendarview.EventDay;
import com.example.activemind.databinding.FragmentHistoryBinding;
import com.example.activemind.databinding.FragmentProfileBinding;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

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

        List<EventDay> events = new ArrayList<>();

        for(int i = 1;i < 10;i++) {
            Calendar calendar = new GregorianCalendar(2022,2,i);
            events.add(new EventDay(calendar, R.drawable.check));
        }
        Calendar calendar1 = Calendar.getInstance();
        calendar1.set(2022, 2, 10);
        events.add(new EventDay(calendar1, R.drawable.uncheck));

//or
//        events.add(new EventDay(calendar, new Drawable()));
//or if you want to specify event label color
//        events.add(new EventDay(calendar, R.drawable.sample_icon, Color.parseColor("#228B22")));

        CalendarView calendarView = binding.calendarView;
        calendarView.setEvents(events);

        return binding.getRoot();
    }
}