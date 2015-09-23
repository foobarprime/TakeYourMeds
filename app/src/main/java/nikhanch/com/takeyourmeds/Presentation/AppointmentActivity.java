package nikhanch.com.takeyourmeds.Presentation;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import nikhanch.com.takeyourmeds.DataModels.Appointment;
import nikhanch.com.takeyourmeds.R;


public class AppointmentActivity extends Activity {

    public static final String APPOINTMENT_KEY_STRING = "Appointment";

    private Appointment appointment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointment);
        Intent i = getIntent();
        appointment = i.getExtras().getParcelable(APPOINTMENT_KEY_STRING);
        Toast.makeText(this, "appointment info found = " + appointment.getDoctorsName(), Toast.LENGTH_LONG).show();
    }
}
