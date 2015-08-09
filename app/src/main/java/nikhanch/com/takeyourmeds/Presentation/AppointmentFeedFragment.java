package nikhanch.com.takeyourmeds.Presentation;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.CardArrayAdapter;
import it.gmariotti.cardslib.library.view.CardListView;
import nikhanch.com.takeyourmeds.Application;
import nikhanch.com.takeyourmeds.DataModels.Appointment;
import nikhanch.com.takeyourmeds.Presentation.PresentationDataModels.AppointmentCard;
import nikhanch.com.takeyourmeds.Presentation.View.SlidingTabLayout;
import nikhanch.com.takeyourmeds.R;

public class AppointmentFeedFragment extends FeedFragmentBase  {


    CardArrayAdapter mCardArrayAdapter;
    CardListView listView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_appointment_feed, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setContentView(R.layout.activity_appointment_list);

        ArrayList<Card> appointmentCards = new ArrayList<>();
        mCardArrayAdapter = new CardArrayAdapter(getActivity(), appointmentCards);

        listView = (CardListView) getActivity().findViewById(R.id.appointmentCardListView);
        if (listView != null) {
            listView.setAdapter(mCardArrayAdapter);
        }

        Application.getEventBus().register(this);
    }

    @Subscribe
    public void OnAppointmentsUpdated(ArrayList<Appointment> updatedAppointments){
        //List<Appointment> appointments = getAppointments();
        ArrayList<Card> appointmentCards = getAppointmentCards(updatedAppointments);
        Collections.sort(appointmentCards, new Comparator<Card>() {
            @Override
            public int compare(Card lhs, Card rhs) {
                AppointmentCard lhsAppointmentCard = (AppointmentCard) lhs;
                AppointmentCard rhsAppointmentCard = (AppointmentCard) rhs;
                return lhsAppointmentCard.compareTo(rhsAppointmentCard);
            }
        });

        mCardArrayAdapter.addAll(appointmentCards);
    }

    private ArrayList<Card> getAppointmentCards(List<Appointment> appointments){
        ArrayList<Card> appointmentCards = new ArrayList<Card>();
        for (Appointment a : appointments){
            AppointmentCard card = new AppointmentCard(getActivity(), a);
            appointmentCards.add(card);
        }
        return appointmentCards;
    }
}
