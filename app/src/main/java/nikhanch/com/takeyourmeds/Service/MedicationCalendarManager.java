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
import nikhanch.com.takeyourmeds.DataModels.MedicineAlert;

/**
 * Created by nikhanch on 8/9/2015.
 */
interface SyncMedicationAsyncTaskConsumer {

    Calendar getCalendarService();

    void OnUpdated(MedicationCalendarManager.MedicationsUpdatedEvent medicationsUpdatedEvent);
}
public class MedicationCalendarManager implements SyncMedicationAsyncTaskConsumer {

    Calendar mCalendar;
    private MedicationsUpdatedEvent mLastMedicineAlertUpdatedEvent = null;
    private GoogleCalendarAccountConnectionManager mAccountConnectionManager;

    public MedicationCalendarManager(Calendar calendar, GoogleCalendarAccountConnectionManager connectionManager) {
        this.mCalendar = calendar;
        this.mAccountConnectionManager = connectionManager;
        Application.getEventBus().register(this);
    }

    @Subscribe
    public void OnConnectionStatusChanged(GoogleCalendarAccountConnectionManager.ConnectionStatus newStatus) {

        if (newStatus != null && newStatus.getConnectionState() == GoogleCalendarAccountConnectionManager.ConnectionState.StatusOk) {
            if (mLastMedicineAlertUpdatedEvent == null) {
                SyncNow();
                return;
            }

            if (mLastMedicineAlertUpdatedEvent.getErrorContext() != null) {
                SyncNow();
                return;
            }
        }
    }

    private void SyncNow() {
        new SyncMedicationAsyncTask(this).execute();
    }

    @Produce
    public MedicationsUpdatedEvent getMedicineAlerts() {
        return mLastMedicineAlertUpdatedEvent;
    }

    //region SyncMedicationAsyncTaskConsumer
    @Override
    public void OnUpdated(MedicationsUpdatedEvent medicationsUpdatedEvent) {
        this.mLastMedicineAlertUpdatedEvent = medicationsUpdatedEvent;
        if (this.mLastMedicineAlertUpdatedEvent.getErrorContext() != null) {
            mAccountConnectionManager.OnGoogleApiCommunicationError(this.mLastMedicineAlertUpdatedEvent.getErrorContext());
        } else {
            Application.getEventBus().post(this.mLastMedicineAlertUpdatedEvent);
        }
    }

    @Override
    public Calendar getCalendarService(){
        return this.mCalendar;
    }
    //endregion

    //region MedicationsUpdatedEvent
    public class MedicationsUpdatedEvent {

        public class UpdatedMedications{
            private ArrayList<MedicineAlert> Medications;

            public ArrayList<MedicineAlert> getMedications() {
                return Medications;
            }
            UpdatedMedications(ArrayList<MedicineAlert> alerts){
                this.Medications = alerts;
            }
        }
        private UpdatedMedications mUpdatedMedicineAlerts;
        private GoogleApiCommunicationErrorContext mGoogleApiCommunicationErrorContext;

        public MedicationsUpdatedEvent(ArrayList<MedicineAlert> medicineAlerts) {
            this.mGoogleApiCommunicationErrorContext = null;
            this.mUpdatedMedicineAlerts = new UpdatedMedications(medicineAlerts);
        }

        public MedicationsUpdatedEvent(Exception e, GoogleApiCommunicationErrorContext.ErrorType errorType) {
            this.mGoogleApiCommunicationErrorContext = new GoogleApiCommunicationErrorContext(e, errorType);
            this.mUpdatedMedicineAlerts = null;
        }

        public UpdatedMedications getUpdatedMedicineAlerts() {
            return this.mUpdatedMedicineAlerts;
        }

        public GoogleApiCommunicationErrorContext getErrorContext() {
            return this.mGoogleApiCommunicationErrorContext;
        }
    }
    //endregion

    //region SyncMedicationAsyncTask
    class SyncMedicationAsyncTask extends AsyncTask<Void, Void, MedicationCalendarManager.MedicationsUpdatedEvent> {

        private final String MEDICATIONS_CAL_NAME = "Medication";
        private SyncMedicationAsyncTaskConsumer consumer;
        private com.google.api.services.calendar.Calendar service;

        public SyncMedicationAsyncTask(SyncMedicationAsyncTaskConsumer consumer){
            this.consumer = consumer;
            this.service = consumer.getCalendarService();
        }

        @Override
        protected MedicationCalendarManager.MedicationsUpdatedEvent doInBackground(Void... params) {

            MedicationCalendarManager.MedicationsUpdatedEvent updatedEvent = null;

            try {
                List<Event> events = getEventsFromCalendar(service);
                ArrayList<MedicineAlert> medicineAlerts = getEventsAsMedicineAlters(events);
                updatedEvent= new MedicationCalendarManager.MedicationsUpdatedEvent(medicineAlerts);

            } catch (final GooglePlayServicesAvailabilityIOException availabilityException) {
                updatedEvent = new MedicationCalendarManager.MedicationsUpdatedEvent(availabilityException, GoogleApiCommunicationErrorContext.ErrorType.GooglePlayServicesAvailabilityException);
            } catch (UserRecoverableAuthIOException userRecoverableException) {
                updatedEvent = new MedicationCalendarManager.MedicationsUpdatedEvent(userRecoverableException, GoogleApiCommunicationErrorContext.ErrorType.UserRecoverableException);
            } catch (Exception e) {
                updatedEvent = new MedicationCalendarManager.MedicationsUpdatedEvent(e, GoogleApiCommunicationErrorContext.ErrorType.OtherException);
            } finally {
                return updatedEvent;
            }
        }

        private ArrayList<MedicineAlert> getEventsAsMedicineAlters(List<Event> events){
            ArrayList<MedicineAlert> medicineAlerts = new ArrayList<>();
            for(Event e : events){
                MedicineAlert alert = new MedicineAlert(e);
                medicineAlerts.add(alert);
            }
            return medicineAlerts;
        }

        private List<Event> getEventsFromCalendar(Calendar service) throws IOException {

            String medicationsCalendarId = "";
            List<CalendarListEntry> calendarListEntries =
                    service.calendarList().list().execute().getItems();

            for (CalendarListEntry entry : calendarListEntries){
                String calendarId = entry.getId();
                String calendarName = entry.getSummary();
                if (calendarName.equalsIgnoreCase(MEDICATIONS_CAL_NAME)){
                    medicationsCalendarId = calendarId;
                    break;
                }
                Log.d("cal", calendarId);
            }

            Events events = service.events().list(medicationsCalendarId)
                    .setMaxResults(70)
                    .setOrderBy("startTime")
                    .setSingleEvents(true)
                    .execute();
            return events.getItems();
        }
        @Override
        protected void onPostExecute(MedicationCalendarManager.MedicationsUpdatedEvent updatedEvent) {
            consumer.OnUpdated(updatedEvent);
        }
    }
    //endregion
}