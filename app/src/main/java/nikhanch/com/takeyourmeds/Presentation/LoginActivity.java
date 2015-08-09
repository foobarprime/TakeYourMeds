package nikhanch.com.takeyourmeds.Presentation;

import android.app.Activity;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;

import com.kogitune.activity_transition.ActivityTransitionLauncher;

import java.util.Calendar;

import nikhanch.com.takeyourmeds.R;
import nikhanch.com.takeyourmeds.Service.GoogleCalendarDataSyncService;


public class LoginActivity extends Activity {


    public final String userName = "Albert";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        String welcomeMessageString = "";
        boolean isMorning = Calendar.getInstance().get(Calendar.AM_PM) == Calendar.AM;
        if (Calendar.getInstance().get(Calendar.AM_PM) == Calendar.AM){
            welcomeMessageString = getString(R.string.good_morning);
        }
        if (!isMorning){
            boolean isAfternoon = Calendar.getInstance().get(Calendar.HOUR_OF_DAY) < 15;
            welcomeMessageString = (isAfternoon) ?
                    getString(R.string.good_afternoon) : getString(R.string.good_evening);
        }


        welcomeMessageString += " " + userName + "!";
        TextView welcomeMessageTextView = (TextView)findViewById(R.id.welcomeMessage);
        welcomeMessageTextView.setText(welcomeMessageString);
        welcomeMessageTextView.setTextColor(ColorStateList.valueOf(Color.WHITE));

        startService(new Intent(this, GoogleCalendarDataSyncService.class));

    }

    public void onViewAppointmentsClicked(View v){
        showAppointmentList();
    }

    private void showAppointmentList(){
        startActivity(new Intent(getApplicationContext(), PatientFeedsActivity.class));

    }
}
