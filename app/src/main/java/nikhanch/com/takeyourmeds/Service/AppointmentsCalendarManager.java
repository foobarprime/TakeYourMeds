package nikhanch.com.takeyourmeds.Service;

import com.google.api.services.calendar.Calendar;
import com.squareup.otto.Produce;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;

import nikhanch.com.takeyourmeds.Application;
import nikhanch.com.takeyourmeds.DataModels.Appointment;

/**
 * Created by nikhanch on 7/26/2015.
 */
public class AppointmentsCalendarManager {

    Calendar mCalendar;
    private SyncAppointmentsAsyncTask.AppointmentsUpdatedEvent mLastAppointmentsUpdatedEvent = null;

    public SyncAppointmentsAsyncTask.AppointmentsUpdatedEvent getAppointmentsUpdatedEvent() {
        return mLastAppointmentsUpdatedEvent;
    }

    public AppointmentsCalendarManager(Calendar calendar) {
        this.mCalendar = calendar;
        Application.getEventBus().register(this);
    }

    @Subscribe public void OnConnectionStatusChanged(GoogleCalendarAccountConnectionManager.ConnectionStatus newStatus) {

        if (newStatus != null && newStatus.getConnectionState() == GoogleCalendarAccountConnectionManager.ConnectionState.StatusOk) {
            if (mLastAppointmentsUpdatedEvent == null){
                SyncNow();
                return;
            }

            if (mLastAppointmentsUpdatedEvent.getErrorContext() != null){
                SyncNow();
                return;
            }
        }
    }

    private void SyncNow(){
        new SyncAppointmentsAsyncTask().execute(mCalendar);
    }
    @Subscribe
    public void OnAppointmentUpdated(SyncAppointmentsAsyncTask.AppointmentsUpdatedEvent appointmentsUpdatedEvent){
        this.mLastAppointmentsUpdatedEvent = appointmentsUpdatedEvent;
        if (this.mLastAppointmentsUpdatedEvent.getErrorContext() != null){
            Application.getEventBus().post(this.mLastAppointmentsUpdatedEvent.getErrorContext());
        }
        else {
            Application.getEventBus().post(this.mLastAppointmentsUpdatedEvent.getUpdatedAppointments());
        }
    }

    @Produce
    public SyncAppointmentsAsyncTask.AppointmentsUpdatedEvent.ErrorContext getErrorContext(){
        if (mLastAppointmentsUpdatedEvent != null){
            return mLastAppointmentsUpdatedEvent.getErrorContext();
        }
        return null;
    }

    @Produce
    public ArrayList<Appointment> getAppointments(){
        if (mLastAppointmentsUpdatedEvent != null) {
            return mLastAppointmentsUpdatedEvent.getUpdatedAppointments();
        }
        return null;
    }
}
