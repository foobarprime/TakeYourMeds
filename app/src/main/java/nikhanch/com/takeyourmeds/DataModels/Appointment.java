package nikhanch.com.takeyourmeds.DataModels;

import android.app.DatePickerDialog;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventAttendee;
import com.google.api.services.calendar.model.EventDateTime;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import nikhanch.com.takeyourmeds.R;

/**
 * Created by nikhanch on 7/22/2015.
 */
public class Appointment extends NotifiableItem implements Parcelable{
    private String doctorsName;
    private String doctorPhoto;
    private String procedureName;
    private Date appointmentDate;
    private String location;
    private String notes;
    private ArrayList<String> reminders;

    private static final String REMINDER_TAG = "Reminders:";
    private static final String DOCTOR_PHOTO_TAG = "Doctor Photo:";
    private static final String DOCTOR_NAME_TAG = "Doctor Name:";
    private static final long NOTIFICATION_MS_BEFORE_APPOINTMENT_START = 30 * 60 * 1000;

    /*
    public Appointment(String doctorsName, String procedureName, DateTime appointmentDate){
        this.doctorsName = doctorsName;
        this.procedureName = procedureName;
        this.appointmentDate = appointmentDate;
        this.medicineAlerts = new ArrayList();
    }
*/

    public Appointment(Event event){

        if (event == null){
            return;
        }

        this.setId(event.getId());
        this.reminders = new ArrayList();

        if (event.getAttendees() != null){
            List<EventAttendee> attendeeList = event.getAttendees();
            if (attendeeList.size() > 0){
                this.doctorsName = attendeeList.get(0).getEmail();
            }
        }

        this.procedureName = event.getSummary();

        if (event.getStart() != null){
            EventDateTime eventDateTime = event.getStart();

            if (eventDateTime.getDate() != null){
                this.appointmentDate = new Date(eventDateTime.getDate().getValue());
            }
            else if (event.getStart().getDateTime() != null){
                this.appointmentDate = new Date(eventDateTime.getDateTime().getValue());
            }
        }
        if (this.appointmentDate == null){
            throw new AssertionError("appointment Date is null");
        }
        this.location = event.getLocation();
        this.notes = event.getDescription();
        fillFieldsFromDescription(this.notes);
    }

    private void fillFieldsFromDescription(String description){
        if (description == null){
            return;
        }
        String[] lines = description.split("\\r?\\n");
        for(String line : lines){
            if (line.startsWith(REMINDER_TAG)){
                String toadd = line.replaceFirst(REMINDER_TAG, "");
                this.reminders.add(toadd);
            }
            else if (line.startsWith(DOCTOR_PHOTO_TAG)){
                String url = line.replaceFirst(DOCTOR_PHOTO_TAG, "").trim();
                this.doctorPhoto = url;
            }
            else if (line.startsWith(DOCTOR_NAME_TAG)){
                String name = line.replaceFirst(DOCTOR_NAME_TAG, "");
                this.doctorsName = name;
            }
        }
    }

    @Override
    public long getNumMsTillAlertShouldBeDisplayed() {
        //return System.currentTimeMillis();
        return this.getAppointmentDate().getTime() - NOTIFICATION_MS_BEFORE_APPOINTMENT_START;
    }

    @Override
    public String getNotificationTitle() {
        return "Upcoming Appointment";
    }
    @Override
    public String getNotificationText() {
        return this.getProcedureName();
    }
    @Override
    public int getNotificationIcon() {
        return R.mipmap.doctor_appointment_512;
    }

    public String getDoctorsName(){
        return this.doctorsName;
    }

    public String getProcedureName() {
        return this.procedureName;
    }

    public Date getAppointmentDate(){
        return this.appointmentDate;
    }

    public String getDoctorPhoto() {
        return doctorPhoto;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        /**
         * private String doctorsName;
         private String doctorPhoto;
         private String procedureName;
         private Date appointmentDate;
         private String location;
         private String notes;
         private ArrayList<String> reminders;
         */

        dest.writeString(doctorsName);
        dest.writeString(doctorPhoto);
        dest.writeString(procedureName);
        dest.writeLong(appointmentDate.getTime());
        dest.writeString(location);
        dest.writeString(notes);
        dest.writeStringList(reminders);
    }
    public Appointment(Parcel in){
        doctorsName = in.readString();
        doctorPhoto = in.readString();
        procedureName = in.readString();
        appointmentDate = new Date(in.readLong());
        location = in.readString();
        notes = in.readString();
        reminders = new ArrayList<String>();
        in.readStringList(reminders);
    }
    public static final Parcelable.Creator<Appointment> CREATOR = new Parcelable.Creator<Appointment>() {

        public Appointment createFromParcel(Parcel in) {
            return new Appointment(in);
        }

        public Appointment[] newArray(int size) {
            return new Appointment[size];
        }
    };
}

