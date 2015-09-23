package nikhanch.com.takeyourmeds.Service;

import android.os.AsyncTask;
import android.util.Log;

import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.CalendarListEntry;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by nikhanch on 7/29/2015.
 */
public class CalendarManager {

    private AppointmentsCalendarManager mAppointmentsCalendarManager;
    private MedicationCalendarManager mMedicationCalendarManager;
    private AlertCalendarManager mAlertCalendarManager;
    private GoogleCalendarDataSyncService mParentService;

    public CalendarManager(GoogleCalendarDataSyncService parentService){
        this.mParentService = parentService;
        this.mAlertCalendarManager = new AlertCalendarManager(
                mParentService.getGoogleCalendarTalker().getGoogleCalendarService(),
                mParentService.getGoogleCalendarTalker());

        this.mMedicationCalendarManager = new MedicationCalendarManager(
                mParentService.getGoogleCalendarTalker().getGoogleCalendarService(),
                mParentService.getGoogleCalendarTalker());

        this.mAppointmentsCalendarManager = new AppointmentsCalendarManager(
                mParentService.getGoogleCalendarTalker().getGoogleCalendarService(),
                mParentService.getGoogleCalendarTalker());



    }

    public AppointmentsCalendarManager getAppointmentsCalendarManager() {
        return mAppointmentsCalendarManager;
    }

    public MedicationCalendarManager getMedicationCalendarManager() {
        return mMedicationCalendarManager;
    }

    public AlertCalendarManager getAlertCalendarManager() {
        return mAlertCalendarManager;
    }

}