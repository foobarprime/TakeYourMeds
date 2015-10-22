package nikhanch.com.takeyourmeds.Presentation;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.DateFormatSymbols;
import java.util.Calendar;

import mehdi.sakout.fancybuttons.FancyButton;
import nikhanch.com.takeyourmeds.R;

public class AddItemActivity extends AppCompatActivity {

    Toolbar mToolbar;
    Button mStartingDateButton;
    Button mEndingDateButton;
    Button mStartingTimeButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);

        mToolbar = (Toolbar) findViewById(R.id.add_item_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Add Medication");

        mStartingDateButton = (Button) findViewById(R.id.startingDateButton);
        mEndingDateButton = (Button) findViewById(R.id.endingDateButton);
        mStartingTimeButton = (Button) findViewById(R.id.startingTimeButton);
        FancyButton addMedicationButton = (FancyButton)findViewById(R.id.add_medication_button);
        addMedicationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveAndClose();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add_item, menu);
        return true;
    }

    public void showStartingDatePicker(View v){
        DatePickerDialog pickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                UpdateButtonText(mStartingDateButton, year, monthOfYear, dayOfMonth, true);
            }
        }, Calendar.getInstance().get(Calendar.YEAR), Calendar.getInstance().get(Calendar.MONTH), Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
        pickerDialog.show();
    }

    public void showEndingDatePicker(View v){
        DatePickerDialog pickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                UpdateButtonText(mEndingDateButton, year, monthOfYear, dayOfMonth, false);
            }
        }, Calendar.getInstance().get(Calendar.YEAR), Calendar.getInstance().get(Calendar.MONTH), Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
        pickerDialog.show();
    }

    public void showTimePicker(View v){
        TimePickerDialog dialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
              //  String time = new StringBuilder().append(padTime(hourOfDay)).append(":").append(padTime(minute)).toString();

                String am_pm = "";

                Calendar datetime = Calendar.getInstance();
                datetime.set(Calendar.HOUR_OF_DAY, hourOfDay);
                datetime.set(Calendar.MINUTE, minute);

                if (datetime.get(Calendar.AM_PM) == Calendar.AM)
                    am_pm = "AM";
                else if (datetime.get(Calendar.AM_PM) == Calendar.PM)
                    am_pm = "PM";

                String strHrsToShow = (datetime.get(Calendar.HOUR) == 0) ?"12":datetime.get(Calendar.HOUR)+"";

                String time = strHrsToShow + ":" + datetime.get(Calendar.MINUTE)+" "+am_pm;

                mStartingTimeButton.setText("AT " + time);

            }
        }, Calendar.getInstance().get(Calendar.HOUR_OF_DAY), Calendar.getInstance().get(Calendar.MINUTE), false);
        dialog.show();
    }

    private void UpdateButtonText(Button button, int year, int monthOfYear, int dayOfMonth, boolean isStarting) {
        String shortPrefix = isStarting ? "Starting " : "Until ";
        String lonPrefix = isStarting ? "Starting on " : "Until ";
        int currentMonth = Calendar.getInstance().get(Calendar.MONTH);
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        int currentDayOfMonth = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
        if (currentYear == year && currentMonth == monthOfYear){
            if (currentDayOfMonth == dayOfMonth) {
                button.setText(shortPrefix + "Today");
                return;
            }
            else if (currentDayOfMonth + 1 == (dayOfMonth)){
                button.setText(shortPrefix + "Tomorrow");
                return;
            }
        }

        String monthName = getMonthForInt(monthOfYear);
        button.setText(shortPrefix + monthName + " " + dayOfMonth + ", " + year);
    }

    String getMonthForInt(int num) {
        String month = "wrong";
        DateFormatSymbols dfs = new DateFormatSymbols();
        String[] months = dfs.getMonths();
        if (num >= 0 && num <= 11 ) {
            month = months[num];
        }
        return month;
    }

    public void saveMedication(MenuItem item){
        saveAndClose();
    }

    private void saveAndClose(){
        Toast.makeText(getApplicationContext(), "Saving Medication", Toast.LENGTH_LONG).show();
        finish();

    }

}
