package nikhanch.com.takeyourmeds.Service;

import android.os.AsyncTask;
import android.util.Log;

import com.google.api.client.googleapis.extensions.android.gms.auth.GooglePlayServicesAvailabilityIOException;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.CalendarListEntry;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.Events;
import com.squareup.otto.Produce;
import com.squareup.otto.Subscribe;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import nikhanch.com.takeyourmeds.Application;
import nikhanch.com.takeyourmeds.DataModels.Appointment;

/**
 * Created by nikhanch on 7/26/2015.
 */

interface SyncAppointmentsAsyncTaskConsumer {

    Calendar getCalendarService();

    void OnUpdated(AppointmentsCalendarManager.AppointmentsUpdatedEvent appointmentsUpdatedEvent);
}

public class AppointmentsCalendarManager implements SyncAppointmentsAsyncTaskConsumer {

    Calendar mCalendar;
    private GoogleCalendarAccountConnectionManager mAccountConnectionManager;
    private AppointmentsUpdatedEvent mLastAppointmentsUpdatedEvent = null;

    public AppointmentsCalendarManager(Calendar calendar, GoogleCalendarAccountConnectionManager accountConnectionManager) {
        this.mCalendar = calendar;
        this.mAccountConnectionManager = accountConnectionManager;
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
        new SyncAppointmentsAsyncTask(this).execute();
    }

    @Produce
    public AppointmentsUpdatedEvent getAppointmentsUpdatedEvent(){
        return mLastAppointmentsUpdatedEvent;
    }

    //region SyncAppointmentsAsyncTaskConsumer
    @Override
    public Calendar getCalendarService() {
        return this.mCalendar;
    }

    @Override
    public void OnUpdated(AppointmentsUpdatedEvent appointmentsUpdatedEvent) {
        this.mLastAppointmentsUpdatedEvent = appointmentsUpdatedEvent;
        if (this.mLastAppointmentsUpdatedEvent.getErrorContext() != null){
            this.mAccountConnectionManager.OnGoogleApiCommunicationError(this.mLastAppointmentsUpdatedEvent.getErrorContext());
        }
        else {
            Application.getEventBus().post(this.mLastAppointmentsUpdatedEvent);
        }
    }
    //endregion

    //region AppointmentUpdatedEvent
    public class AppointmentsUpdatedEvent {

        public class UpdatedAppointments{
            public ArrayList<Appointment> Appointments;

            public ArrayList<Appointment> getAppointments() {
                return Appointments;
            }

            UpdatedAppointments(ArrayList<Appointment> appointmentsUpdated){
                this.Appointments = appointmentsUpdated;
            }
        }
        private UpdatedAppointments mUpdatedAppointments;
        private GoogleApiCommunicationErrorContext mGoogleApiCommunicationErrorContext;

        public AppointmentsUpdatedEvent(ArrayList<Appointment> appointments){
            this.mGoogleApiCommunicationErrorContext = null;
            this.mUpdatedAppointments = new UpdatedAppointments(appointments);
        }
        public AppointmentsUpdatedEvent(Exception e, GoogleApiCommunicationErrorContext.ErrorType errorType){
            this.mGoogleApiCommunicationErrorContext = new GoogleApiCommunicationErrorContext(e, errorType);
            this.mUpdatedAppointments = null;
        }

        public UpdatedAppointments getUpdatedAppointments() {
            return this.mUpdatedAppointments;
        }

        public GoogleApiCommunicationErrorContext getErrorContext(){
            return this.mGoogleApiCommunicationErrorContext;
        }
    }
    //endregion

    //region SyncAppointmentsAsyncTask
    class SyncAppointmentsAsyncTask extends AsyncTask<Void, Void, AppointmentsUpdatedEvent> {

        //TODO: Move this with discovery to Calendar Manager
        private final String APPOINTMENTS_CAL_NAME = "Appointments";
        private SyncAppointmentsAsyncTaskConsumer consumer;
        private com.google.api.services.calendar.Calendar service;

        SyncAppointmentsAsyncTask(SyncAppointmentsAsyncTaskConsumer consumer){
            this.consumer = consumer;
            this.service = consumer.getCalendarService();
        }

        @Override
        protected AppointmentsUpdatedEvent doInBackground(Void... params) {
            AppointmentsUpdatedEvent appointmentsUpdatedEvent = null;

            try {
                List<Event> events = getEventsFromCalendar(service);
                ArrayList<Appointment> appointments = getEventsAsAppointment(events);
                appointmentsUpdatedEvent = new AppointmentsUpdatedEvent(appointments);

            } catch (final GooglePlayServicesAvailabilityIOException availabilityException) {
                appointmentsUpdatedEvent = new AppointmentsUpdatedEvent(availabilityException, GoogleApiCommunicationErrorContext.ErrorType.GooglePlayServicesAvailabilityException);
            } catch (UserRecoverableAuthIOException userRecoverableException) {
                appointmentsUpdatedEvent = new AppointmentsUpdatedEvent(userRecoverableException, GoogleApiCommunicationErrorContext.ErrorType.UserRecoverableException);
            } catch (Exception e) {
                appointmentsUpdatedEvent = new AppointmentsUpdatedEvent(e, GoogleApiCommunicationErrorContext.ErrorType.OtherException);
            }
            finally{
                return appointmentsUpdatedEvent;
            }
        }

        @Override
        protected void onPostExecute(AppointmentsUpdatedEvent appointmentsUpdatedEvent) {
            consumer.OnUpdated(appointmentsUpdatedEvent);
        }

        private ArrayList<Appointment> getEventsAsAppointment(List<Event> events){
            ArrayList<Appointment> appointments = new ArrayList<>();
            for(Event e : events){
                Appointment a = new Appointment(e);
                appointments.add(a);
            }
            return appointments;
        }

        private List<Event> getEventsFromCalendar(Calendar service) throws IOException {

            String appointmentCalendarId = "";
            List<CalendarListEntry> calendarListEntries =
                    service.calendarList().list().execute().getItems();

            for (CalendarListEntry entry : calendarListEntries){
                String calendarId = entry.getId();
                String calendarName = entry.getSummary();
                if (calendarName.equalsIgnoreCase(APPOINTMENTS_CAL_NAME)){
                    appointmentCalendarId = calendarId;
                    break;
                }
                Log.d("cal", calendarId);
            }

            Events events = service.events().list(appointmentCalendarId)
                    .setMaxResults(10)
                    .setOrderBy("startTime")
                    .setSingleEvents(true)
                    .execute();
            return events.getItems();
        }


    }
    //endregion
}
