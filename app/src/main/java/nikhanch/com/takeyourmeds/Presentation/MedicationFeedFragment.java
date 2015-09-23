package nikhanch.com.takeyourmeds.Presentation;

import android.app.DatePickerDialog;
import android.content.Context;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.squareup.otto.Subscribe;

import org.buraktamturk.loadingview.LoadingView;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.view.CardViewNative;
import nikhanch.com.takeyourmeds.Application;
import nikhanch.com.takeyourmeds.DataModels.MedicineAlert;
import nikhanch.com.takeyourmeds.R;
import nikhanch.com.takeyourmeds.Service.MedicationCalendarManager;
import nikhanch.com.takeyourmeds.Utils.DateTimeUtils;


public class MedicationFeedFragment extends FeedFragmentBase{

    ListView medicationListView = null;
    ArrayList<MedicineAlert> allAlerts;
    ArrayAdapter<MedicineAlert> medicineAlertArrayAdapter = null;
    Calendar mUserDatePicked = Calendar.getInstance();
    TextView mMedicationForDateTextView;

    public MedicationFeedFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_medication_feed, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Card medicationList = new Card(getActivity(), R.layout.medication_card_view);
        CardViewNative cardViewNative = (CardViewNative)getActivity().findViewById(R.id.medicationCardViewNative);
        cardViewNative.setCard(medicationList);

        mMedicationForDateTextView = (TextView)getActivity().findViewById(R.id.medication_for_date);

        mUserDatePicked.setTimeInMillis(System.currentTimeMillis());
        setMedicationForDateTextView();

        ImageView imageView = (ImageView) getActivity().findViewById(R.id.medication_calendar_imageView);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int currentMonth = mUserDatePicked.get(Calendar.MONTH);
                int currentYear = mUserDatePicked.get(Calendar.YEAR);
                int currentDayOfMonth = mUserDatePicked.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog pickerDialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        mUserDatePicked.set(year, monthOfYear, dayOfMonth);
                        setMedicationForDateTextView();
                        setMedicationForUserDatePicked();
                    }
                }, currentYear, currentMonth, currentDayOfMonth);
                pickerDialog.show();
            }
        });
        medicationListView = (ListView)getActivity().findViewById(R.id.medication_list_view);
        medicineAlertArrayAdapter = new MedicationFeedArrayAdapter(getActivity(), new ArrayList<MedicineAlert>());
        medicationListView.setAdapter(medicineAlertArrayAdapter);

        Application.getEventBus().register(this);
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Application.getEventBus().unregister(this);
    }
    private void setMedicationForDateTextView() {
        String message = "Medications for ";
        message += DateFormat.getDateInstance().format(new Date(mUserDatePicked.getTimeInMillis()));
        mMedicationForDateTextView.setText(message);
    }
    public void setMedicationForUserDatePicked(){
        ArrayList<MedicineAlert> medsForToday = new ArrayList<>();
        for (int i = 0; i < allAlerts.size(); i++){
            if (!(allAlerts.get(i) instanceof MedicineAlert)){
                String lineToBreakOn;
            }
            else{
                MedicineAlert m = allAlerts.get(i);
                if (DateTimeUtils.isOnSameDay(m.getNextAlertTime().getTime(), mUserDatePicked)) {
                    medsForToday.add(m);
                }
            }
        }
        assert(medsForToday.size() > 0);

        CardViewNative cardViewNative = (CardViewNative)getActivity().findViewById(R.id.medicationCardViewNative);
        cardViewNative.setVisibility(View.VISIBLE);
        LoadingView view = (LoadingView)getActivity().findViewById(R.id.medicationFeedLoading);
        view.setLoading(false);
        view.setVisibility(View.GONE);

        medicineAlertArrayAdapter.clear();
        medicineAlertArrayAdapter.addAll(medsForToday);

    }


    @Subscribe
    public void OnMedicationsUpdated(MedicationCalendarManager.MedicationsUpdatedEvent medicationsUpdatedEvent){
        if (medicationsUpdatedEvent == null || medicationsUpdatedEvent.getUpdatedMedicineAlerts() == null){
            return;
        }

        OnMedicineAlertsUpdated(medicationsUpdatedEvent.getUpdatedMedicineAlerts());
    }

    public void OnMedicineAlertsUpdated(MedicationCalendarManager.MedicationsUpdatedEvent.UpdatedMedications alerts){
        allAlerts = alerts.getMedications();
        setMedicationForUserDatePicked();
    }




    class MedicationFeedArrayAdapter extends ArrayAdapter<MedicineAlert>{

        public MedicationFeedArrayAdapter(Context context, ArrayList<MedicineAlert> medicineAlerts) {
            super(context, R.layout.medication_row, medicineAlerts);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View customView = LayoutInflater.from(getContext()).inflate(R.layout.medication_row, parent, false);
            MedicineAlert alert = getItem(position);

            final TextView medicationTime = (TextView) customView.findViewById(R.id.medication_time);
            final TextView medicationName = (TextView) customView.findViewById(R.id.medication_name);
            CheckBox checkBox = (CheckBox)customView.findViewById(R.id.medication_taken);

            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked){
                        medicationName.setPaintFlags(medicationName.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                        medicationTime.setPaintFlags(medicationTime.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                    }
                    else{
                        medicationName.setPaintFlags(medicationName.getPaintFlags() & ~Paint.STRIKE_THRU_TEXT_FLAG);
                        medicationTime.setPaintFlags(medicationTime.getPaintFlags() & ~Paint.STRIKE_THRU_TEXT_FLAG);
                    }
                }
            });

            medicationName.setText(alert.getMedicineName());
            medicationTime.setText(DateFormat.getTimeInstance(DateFormat.SHORT).format(alert.getNextAlertTime()));
            return customView;
        }
    }
}
