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
import nikhanch.com.takeyourmeds.DataModels.Alert;
import nikhanch.com.takeyourmeds.DataModels.Appointment;

/**
 * Created by nikhanch on 9/20/2015.
 */
interface SyncAlertsAsyncTaskConsumer {

    Calendar getCalendarService();

    void OnUpdated(AlertCalendarManager.AlertsUpdatedEvent alertsUpdatedEvent);
}
public class AlertCalendarManager implements SyncAlertsAsyncTaskConsumer {
    Calendar mCalendar;
    private GoogleCalendarAccountConnectionManager mAccountConnectionManager;
    private AlertsUpdatedEvent mLastAlertUpdatedEvent = null;

    public AlertCalendarManager(Calendar calendar, GoogleCalendarAccountConnectionManager accountConnectionManager) {
        this.mCalendar = calendar;
        this.mAccountConnectionManager = accountConnectionManager;
        Application.getEventBus().register(this);
    }

    @Subscribe
    public void OnConnectionStatusChanged(GoogleCalendarAccountConnectionManager.ConnectionStatus newStatus) {

        if (newStatus != null && newStatus.getConnectionState() == GoogleCalendarAccountConnectionManager.ConnectionState.StatusOk) {
            if (mLastAlertUpdatedEvent == null){
                SyncNow();
                return;
            }

            if (mLastAlertUpdatedEvent.getErrorContext() != null){
                SyncNow();
                return;
            }
        }
    }

    private void SyncNow(){
        new SyncAlertsAsyncTask(this).execute();
    }

    @Produce
    public AlertsUpdatedEvent getAlertsUpdatedEvent(){
        return mLastAlertUpdatedEvent;
    }

    //region SyncAlertsAsyncTaskConsumer
    @Override
    public Calendar getCalendarService() {
        return this.mCalendar;
    }

    @Override
    public void OnUpdated(AlertsUpdatedEvent alertsUpdatedEvent) {
        this.mLastAlertUpdatedEvent = alertsUpdatedEvent;
        if (this.mLastAlertUpdatedEvent.getErrorContext() != null){
            this.mAccountConnectionManager.OnGoogleApiCommunicationError(this.mLastAlertUpdatedEvent.getErrorContext());
        }
        else {
            Application.getEventBus().post(this.mLastAlertUpdatedEvent);
        }
    }
    //endregion

    //region AlertsUpdatedEvent
    public class AlertsUpdatedEvent {

        public class UpdatedAlerts{
            public ArrayList<Alert> Alerts;

            public ArrayList<Alert> getAlerts() {
                return Alerts;
            }

            UpdatedAlerts(ArrayList<Alert> appointmentsUpdated){
                this.Alerts = appointmentsUpdated;
            }
        }
        private UpdatedAlerts mUpdatedAlerts;
        private GoogleApiCommunicationErrorContext mGoogleApiCommunicationErrorContext;

        public AlertsUpdatedEvent(ArrayList<Alert> alerts){
            this.mGoogleApiCommunicationErrorContext = null;
            this.mUpdatedAlerts = new UpdatedAlerts(alerts);
        }
        public AlertsUpdatedEvent(Exception e, GoogleApiCommunicationErrorContext.ErrorType errorType){
            this.mGoogleApiCommunicationErrorContext = new GoogleApiCommunicationErrorContext(e, errorType);
            this.mUpdatedAlerts = null;
        }

        public UpdatedAlerts getUpdatedAlerts() {
            return this.mUpdatedAlerts;
        }

        public GoogleApiCommunicationErrorContext getErrorContext(){
            return this.mGoogleApiCommunicationErrorContext;
        }
    }
    //endregion

    //region SyncAlertsAsyncTask
    class SyncAlertsAsyncTask extends AsyncTask<Void, Void, AlertsUpdatedEvent> {

        //TODO: Move this with discovery to Calendar Manager
        private final String APPOINTMENTS_CAL_NAME = "Alerts";
        private SyncAlertsAsyncTaskConsumer consumer;
        private com.google.api.services.calendar.Calendar service;

        SyncAlertsAsyncTask(SyncAlertsAsyncTaskConsumer consumer){
            this.consumer = consumer;
            this.service = consumer.getCalendarService();
        }

        @Override
        protected AlertsUpdatedEvent doInBackground(Void... params) {
            AlertsUpdatedEvent alertsUpdatedEvent = null;

            try {
                List<Event> events = getEventsFromCalendar(service);
                ArrayList<Alert> alerts = getEventsAsAlerts(events);
                alertsUpdatedEvent = new AlertsUpdatedEvent(alerts);

            } catch (final GooglePlayServicesAvailabilityIOException availabilityException) {
                alertsUpdatedEvent = new AlertsUpdatedEvent(availabilityException, GoogleApiCommunicationErrorContext.ErrorType.GooglePlayServicesAvailabilityException);
            } catch (UserRecoverableAuthIOException userRecoverableException) {
                alertsUpdatedEvent = new AlertsUpdatedEvent(userRecoverableException, GoogleApiCommunicationErrorContext.ErrorType.UserRecoverableException);
            } catch (Exception e) {
                alertsUpdatedEvent = new AlertsUpdatedEvent(e, GoogleApiCommunicationErrorContext.ErrorType.OtherException);
            }
            finally{
                return alertsUpdatedEvent;
            }
        }

        @Override
        protected void onPostExecute(AlertsUpdatedEvent alertsUpdatedEvent) {
            consumer.OnUpdated(alertsUpdatedEvent);
        }

        private ArrayList<Alert> getEventsAsAlerts(List<Event> events){
            ArrayList<Alert> alerts = new ArrayList<Alert>();
            for(Event e : events){
                Alert a = new Alert(e);
                alerts.add(a);
            }
            return alerts;
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
