package nikhanch.com.takeyourmeds.DataModels;

import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventAttendee;
import com.google.api.services.calendar.model.EventDateTime;

import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import nikhanch.com.takeyourmeds.R;

public class MedicineAlert extends NotifiableItem {
    private String medicineName;
    private Date nextAlertTime;

    private static final long NOTIFICATION_MS_BEFORE_EVENT_START = 5 * 60 * 1000;
    public MedicineAlert(String medicineName, Date nextAlertTime){
        this.medicineName = medicineName;
        this.nextAlertTime = nextAlertTime;
    }

    public MedicineAlert(Event event){
        // TODO: fill
        if (event == null){
            return;
        }

        this.setId(event.getId());
        this.medicineName = event.getSummary();

        if (event.getStart() != null){
            EventDateTime eventDateTime = event.getStart();

            if (eventDateTime.getDate() != null){
                this.nextAlertTime = new Date(eventDateTime.getDate().getValue());
            }
            else if (event.getStart().getDateTime() != null){
                this.nextAlertTime = new Date(eventDateTime.getDateTime().getValue());
            }
        }
        if (this.nextAlertTime == null){
            throw new AssertionError("appointment Date is null");
        }
    }

    @Override
    public long getNumMsTillAlertShouldBeDisplayed() {
        //return 5 * 1000; // 5 seconds
        return this.nextAlertTime.getTime() - NOTIFICATION_MS_BEFORE_EVENT_START ;
    }

    @Override
    public String getNotificationTitle() {
        return "Upcoming Medication";
    }

    @Override
    public String getNotificationText() {
        return getMedicineName();
    }

    @Override
    public int getNotificationIcon() {
        return R.mipmap.medication_drugs2_512;
    }

    public String getMedicineName(){
        return this.medicineName;
    }

    public Date getNextAlertTime(){
        return this.nextAlertTime;
    }
}
