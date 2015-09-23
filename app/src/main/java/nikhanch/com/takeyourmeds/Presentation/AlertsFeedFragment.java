package nikhanch.com.takeyourmeds.Presentation;

import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.squareup.otto.Subscribe;

import org.buraktamturk.loadingview.LoadingView;
import org.w3c.dom.Text;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.view.CardViewNative;
import nikhanch.com.takeyourmeds.Application;
import nikhanch.com.takeyourmeds.DataModels.Alert;
import nikhanch.com.takeyourmeds.DataModels.AlertType;
import nikhanch.com.takeyourmeds.R;
import nikhanch.com.takeyourmeds.Service.AlertCalendarManager;
import nikhanch.com.takeyourmeds.Utils.DateTimeUtils;
import nikhanch.com.takeyourmeds.Utils.ListUtils;

/**
 * Created by nikhanch on 9/20/2015.
 */
public class AlertsFeedFragment extends FeedFragmentBase{

    Calendar alertsForDate = Calendar.getInstance();

    ArrayList<AlertListManager> listManagers = null;
    ArrayList<Alert> allAlerts = null;

    public AlertsFeedFragment(){
        // Required empty constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_alert_feed, container, false);
    }



    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        alertsForDate.setTimeInMillis(System.currentTimeMillis());
        ImageView imageView = (ImageView) getActivity().findViewById(R.id.alerts_calendar_imageView);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int currentMonth = alertsForDate.get(Calendar.MONTH);
                int currentYear = alertsForDate.get(Calendar.YEAR);
                int currentDayOfMonth = alertsForDate.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog pickerDialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        alertsForDate.set(year, monthOfYear, dayOfMonth);
                        setAlertsForUserDatePicked();
                    }
                }, currentYear, currentMonth, currentDayOfMonth);
                pickerDialog.show();
            }
        });
        listManagers = new ArrayList<>();
        listManagers.add(new MedicationsAlertListManager(this));
        listManagers.add(new FoodAndDrinkAlertListManager(this));
        listManagers.add(new MiscAlertListManager(this));


        Application.getEventBus().register(this);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Application.getEventBus().unregister(this);
    }

    @Subscribe
    public void OnAlertsUpdatedEvent(AlertCalendarManager.AlertsUpdatedEvent updatedAlertsEvent){

        if (updatedAlertsEvent == null || updatedAlertsEvent.getUpdatedAlerts() == null){
            return;
        }

        allAlerts = updatedAlertsEvent.getUpdatedAlerts().getAlerts();
        setAlertsForUserDatePicked();
    }

    private void setAlertsForUserDatePicked(){
        ArrayList<Alert> todaysAlerts = new ArrayList<>();
        for (Alert a : allAlerts){
            if (DateTimeUtils.isOnSameDay(a.getAlertTime().getTime(), this.alertsForDate)) {
                todaysAlerts.add(a);
            }
        }

        for (AlertListManager manager : listManagers){
            manager.OnAlertsUpdated(todaysAlerts);
        }


    }

    class DummyAlert extends Alert{
        DummyAlert(){
            super(AlertType.Dummy);
        }
    }
    //region ListManagers
    abstract class AlertListManager{

        LinearLayout mLayout = null;
        AlertFeedArrayAdapter adapter = null;
        ListView alertListView = null;
        AlertListManager(AlertsFeedFragment parent){
            Card medicationList = new Card(parent.getActivity(), R.layout.alert_card_view);
            CardViewNative cardViewNative = (CardViewNative)parent.getActivity().findViewById(getCardViewId());
            cardViewNative.setCard(medicationList);
            //mLayout = (LinearLayout)parent.getActivity().findViewById(getLinearLayoutId());

            alertListView = (ListView)cardViewNative.findViewById(R.id.alert_list_view);


            ArrayList<Alert> alertsToShow = new ArrayList<>();
            alertsToShow.add(new DummyAlert());
            adapter = new AlertFeedArrayAdapter(getActivity(), alertsToShow, getNoAlertsMessage());
            alertListView.setAdapter(adapter);
            ListUtils.setListViewHeightBasedOnChildren(alertListView);
            alertListView.setEnabled(false);
            alertListView.setOnTouchListener(new View.OnTouchListener() {
                // Setting on Touch Listener for handling the touch inside ScrollView
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    // Disallow the touch request for parent scroll on touch of child view
                    v.getParent().requestDisallowInterceptTouchEvent(true);

                    return false;
                }
            });
        }

        void OnAlertsUpdated(ArrayList<Alert> todaysAlert){
            AlertType filter = getAlertFilter();
            ArrayList<Alert> toAdd = new ArrayList<>();
            for (Alert a : todaysAlert){
                if (a.getAlertType() == filter){
                    toAdd.add(a);
                }
            }
            adapter.clear();
            if (toAdd.size() == 0){
                toAdd.add(new DummyAlert());
            }
            adapter.addAll(toAdd);
            ListUtils.setListViewHeightBasedOnChildren(alertListView);
            alertListView.setEnabled(false);
           // mLayout.setVisibility(View.VISIBLE);

        }

        protected abstract String getNoAlertsMessage();
        protected abstract int getCardViewId();
        protected abstract int getLinearLayoutId();
        protected abstract AlertType getAlertFilter();

    }

    class MedicationsAlertListManager extends AlertListManager{

        public MedicationsAlertListManager(AlertsFeedFragment feedFragment){
            super(feedFragment);
        }

        @Override
        protected int getLinearLayoutId() {
            return 0;
            //return R.id.alerts_medications;
        }

        @Override
        protected int getCardViewId() {
            return R.id.alertsMedicationsCard;
        }

        @Override
        protected AlertType getAlertFilter() {
            return AlertType.Medication;
        }

        @Override
        protected String getNoAlertsMessage() {
            return "No Medication related Alerts today";
        }
    }

    class FoodAndDrinkAlertListManager extends AlertListManager{

        public FoodAndDrinkAlertListManager (AlertsFeedFragment feedFragment){
            super(feedFragment);
        }

        @Override
        protected int getLinearLayoutId() {
            return 0;
            //return R.id.alerts_food;
        }
        @Override
        protected int getCardViewId() {
            return R.id.alertsFoodCard;
        }

        @Override
        protected AlertType getAlertFilter() {
            return AlertType.FoodAndDrink;
        }

        @Override
        protected String getNoAlertsMessage() {
            return "No Food and Drink related Alerts today";
        }
    }

    class MiscAlertListManager extends AlertListManager{

        public MiscAlertListManager (AlertsFeedFragment feedFragment){
            super(feedFragment);
        }

        @Override
        protected int getLinearLayoutId() {
            return 0;
            //return R.id.alerts_misc;
        }
        @Override
        protected int getCardViewId() {
            return R.id.alertsMiscCard;
        }

        @Override
        protected AlertType getAlertFilter() {
            return AlertType.Miscellaneous;
        }

        @Override
        protected String getNoAlertsMessage() {
            return "No Alerts for today";
        }
    }
    //endregion

    //region AlertFeedArrayAdapter
    class AlertFeedArrayAdapter extends ArrayAdapter<Alert>{

        String noAlertsMessage;
        public AlertFeedArrayAdapter(Context context, ArrayList<Alert> alerts, String noAlertsMessage) {
            super(context, R.layout.alert_row, alerts);
            this.noAlertsMessage = noAlertsMessage;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Alert alert = getItem(position);
            if (alert.getAlertType() == AlertType.Dummy){
                View customView = LayoutInflater.from(getContext()).inflate(R.layout.no_alerts_row, parent, false);
                TextView message = (TextView)customView.findViewById(R.id.no_alerts_message);
                message.setText(noAlertsMessage);
                return customView;
            }
            else {
                View customView = LayoutInflater.from(getContext()).inflate(R.layout.alert_row, parent, false);


                final TextView alertTime = (TextView) customView.findViewById(R.id.alert_time);
                final TextView alertName = (TextView) customView.findViewById(R.id.alert_name);

                alertName.setText(alert.getAlertContent());
                alertTime.setText(DateFormat.getTimeInstance(DateFormat.SHORT).format(alert.getAlertTime()));
                return customView;
            }
        }
    }
    //endregion
}
