package nikhanch.com.takeyourmeds.Presentation;

import android.app.Activity;
import android.app.TabActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.squareup.otto.Subscribe;

import java.text.ParseException;
import java.text.SimpleDateFormat;
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
import nikhanch.com.takeyourmeds.DataModels.MedicineAlert;
import nikhanch.com.takeyourmeds.R;
import nikhanch.com.takeyourmeds.Service.AppointmentsCalendarManager;
import nikhanch.com.takeyourmeds.Service.SyncAppointmentsAsyncTask;


public class AppointmentListActivity extends GoogleCalendarDataSyncServiceTalker {

    CardArrayAdapter mCardArrayAdapter;
    CardListView listView;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getActivity().setContentView(R.layout.activity_appointment_list);
        Application.getEventBus().register(this);

        ArrayList<Card> appointmentCards = new ArrayList<>();
        mCardArrayAdapter = new CardArrayAdapter(getActivity(), appointmentCards);

        listView = (CardListView) getActivity().findViewById(R.id.appointmentCardListView);
        if (listView != null) {
            listView.setAdapter(mCardArrayAdapter);
        }

    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Subscribe public void OnAppointmentsUpdated(ArrayList<Appointment> updatedAppointments){
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

    /*
        private ArrayList<Appointment> getAppointments(){
            ArrayList<Appointment> appointmentsToReturn = new ArrayList<Appointment>();

            SimpleDateFormat appointmentFormat = new SimpleDateFormat("yyyy/MM/dd hh:mm");
            SimpleDateFormat medicineFormat = new SimpleDateFormat("hh:mm");

            try {
                Appointment appointment1 = new Appointment("Dr. Jane Doe", "CT Scan", appointmentFormat.parse("2015/05/22 10:30"));
                appointment1.AddMedicineTime(new MedicineAlert("Nyquil", medicineFormat.parse("18:00")));
                appointmentsToReturn.add(appointment1);

                Appointment appointment2 = new Appointment("Dr. John Smith", "CT Scan", appointmentFormat.parse("2015/05/18 15:30"));
                appointment1.AddMedicineTime(new MedicineAlert("Nyquil", medicineFormat.parse("18:00")));
                appointmentsToReturn.add(appointment2);

                Appointment appointment3 = new Appointment("Dr. John Smith", "General Visit", appointmentFormat.parse("2015/05/15 17:30"));
                appointmentsToReturn.add(appointment3);


            } catch (ParseException e) {
                e.printStackTrace();
            }

            return appointmentsToReturn;
        }
    */

    private ArrayList<Card> getAppointmentCards(List<Appointment> appointments){
        ArrayList<Card> appointmentCards = new ArrayList<Card>();
        for (Appointment a : appointments){
            AppointmentCard card = new AppointmentCard(getActivity(), a);
            appointmentCards.add(card);
        }
        return appointmentCards;
    }

    public void OnMedicationViewImageClicked(View v) {
        Toast.makeText(getActivity().getApplicationContext(), "Medication view is under Construction", Toast.LENGTH_LONG).show();
    }

    public void OnAppointmentViewImageClicked(View v) {
        Toast.makeText(getActivity().getApplicationContext(), "Already showing appointment view", Toast.LENGTH_LONG).show();
    }
}
