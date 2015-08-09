package nikhanch.com.takeyourmeds.Service;

import android.app.Service;

import com.squareup.otto.Subscribe;

/**
 * Created by nikhanch on 7/29/2015.
 */
public class CalendarManager {

    private AppointmentsCalendarManager mAppointmentsCalendarManager;
    private GoogleCalendarDataSyncService mParentService;

    public CalendarManager(GoogleCalendarDataSyncService parentService){
        this.mParentService = parentService;
        this.mAppointmentsCalendarManager = new AppointmentsCalendarManager(mParentService.getGoogleCalendarTalker().getGoogleCalendarService());
    }

    public AppointmentsCalendarManager getAppointmentsCalendarManager() {
        return mAppointmentsCalendarManager;
    }
}
