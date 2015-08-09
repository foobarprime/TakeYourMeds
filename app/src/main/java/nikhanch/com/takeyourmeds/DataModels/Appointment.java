package nikhanch.com.takeyourmeds.DataModels;

import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventAttendee;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by nikhanch on 7/22/2015.
 */
public class Appointment {
    private String doctorsName;
    private String doctorPhoto;
    private String procedureName;
    private Date appointmentDate;
    private String location;
    private String notes;
    private ArrayList<String> reminders;
    private ArrayList<MedicineAlert> medicineAlerts;

    private static final String REMINDER_TAG = "Reminders:";
    private static final String DOCTOR_PHOTO_TAG = "Doctor Photo:";
    private static final String DOCTOR_NAME_TAG = "Doctor Name:";
    private static final String MEDICINE_ALERT_TAG = "Medicine alert:";

    /*
    public Appointment(String doctorsName, String procedureName, DateTime appointmentDate){
        this.doctorsName = doctorsName;
        this.procedureName = procedureName;
        this.appointmentDate = appointmentDate;
        this.medicineAlerts = new ArrayList();
    }
*/
    public Appointment(Event event){
        // TODO: fill
        if (event == null){
            return;
        }

        this.medicineAlerts = new ArrayList();
        this.reminders = new ArrayList();

        if (event.getAttendees() != null){
            List<EventAttendee> attendeeList = event.getAttendees();
            if (attendeeList.size() > 0){
                this.doctorsName = attendeeList.get(0).getEmail();
            }
        }

        this.procedureName = event.getSummary();

        if (event.getStart() != null){
            DateTime startTime = event.getStart().getDate();
            if (startTime != null){
                this.appointmentDate = new Date(startTime.getValue());
            }
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
            else if (line.startsWith(MEDICINE_ALERT_TAG)){

            }
        }
    }

    public void AddMedicineTime(MedicineAlert m){
        this.medicineAlerts.add(m);
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

    public ArrayList<MedicineAlert> getMedicineAlerts(){
        return this.medicineAlerts;
    }

    public String getDoctorPhoto() {
        return doctorPhoto;
    }

}

