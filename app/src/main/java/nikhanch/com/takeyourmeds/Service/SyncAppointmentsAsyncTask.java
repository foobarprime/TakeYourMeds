package nikhanch.com.takeyourmeds.Service;

import android.app.Application;
import android.os.AsyncTask;

import com.google.api.client.googleapis.extensions.android.gms.auth.GooglePlayServicesAvailabilityIOException;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.Events;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import nikhanch.com.takeyourmeds.DataModels.Appointment;

/**
 * Created by nikhanch on 7/25/2015.
 */

public class SyncAppointmentsAsyncTask extends AsyncTask<com.google.api.services.calendar.Calendar, Void, SyncAppointmentsAsyncTask.AppointmentsUpdatedEvent>{

    @Override
    protected AppointmentsUpdatedEvent doInBackground(Calendar... params) {
        com.google.api.services.calendar.Calendar service = params[0];
        AppointmentsUpdatedEvent appointmentsUpdatedEvent = null;

        try {
            List<Event> events = getEventsFromCalendar(service);
            ArrayList<Appointment> appointments = getEventsAsAppointment(events);
            appointmentsUpdatedEvent = new AppointmentsUpdatedEvent(appointments);

        } catch (final GooglePlayServicesAvailabilityIOException availabilityException) {
            appointmentsUpdatedEvent = new AppointmentsUpdatedEvent(availabilityException, ErrorType.GooglePlayServicesAvailabilityException);
        } catch (UserRecoverableAuthIOException userRecoverableException) {
            appointmentsUpdatedEvent = new AppointmentsUpdatedEvent(userRecoverableException, ErrorType.UserRecoverableException);

        } catch (Exception e) {
            appointmentsUpdatedEvent = new AppointmentsUpdatedEvent(e, ErrorType.OtherException);
        }
        finally{
            return appointmentsUpdatedEvent;
        }
    }

    @Override
    protected void onPostExecute(AppointmentsUpdatedEvent appointmentsUpdatedEvent) {
        nikhanch.com.takeyourmeds.Application.getEventBus().post(appointmentsUpdatedEvent);
    }

    private ArrayList<Appointment> getEventsAsAppointment(List<Event> events){
        ArrayList<Appointment> appointments = new ArrayList<>();
        for(Event e : events){
            Appointment a = new Appointment(e);
            appointments.add(a);
        }
        return appointments;
    }

    /**
     * Fetch a list of the next 10 events from the primary calendar.
     * @return List of Strings describing returned events.
     * @throws IOException
     */
    private List<Event> getEventsFromCalendar(Calendar service) throws IOException {

        Events events = service.events().list("primary")
                .setMaxResults(10)
                .setOrderBy("startTime")
                .setSingleEvents(true)
                .execute();
        return events.getItems();
    }

    public enum ErrorType{
        GooglePlayServicesAvailabilityException,
        UserRecoverableException,
        OtherException
    };

    public class AppointmentsUpdatedEvent {

        private ArrayList<Appointment> mUpdatedAppointments;
        private ErrorContext mErrorContext;

        public AppointmentsUpdatedEvent(ArrayList<Appointment> appointments){
            this.mErrorContext = null;
            this.mUpdatedAppointments = appointments;
        }
        public AppointmentsUpdatedEvent(Exception e, ErrorType errorType){
            this.mErrorContext = new ErrorContext(e, errorType);
            this.mUpdatedAppointments = null;
        }

        public ArrayList<Appointment> getUpdatedAppointments() {
            return this.mUpdatedAppointments;
        }

        public ErrorContext     getErrorContext(){
            return this.mErrorContext;
        }

        // TODO: refactor
        // YUCK!
        public class ErrorContext{
            private ErrorType mErrorType;
            private Exception mException;

            public ErrorContext(Exception e, ErrorType errorType){
                this. mErrorType = errorType;
                this.mException = e;
            }
            public ErrorType getErrorType() {
                return mErrorType;
            }

            public Exception getException() {
                return mException;
            }
        }
    }
}
