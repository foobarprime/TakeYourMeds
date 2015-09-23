package nikhanch.com.takeyourmeds.DataModels;

import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;

import java.util.Date;

import nikhanch.com.takeyourmeds.R;

/**
 * Created by nikhanch on 9/20/2015.
 */
public class Alert extends NotifiableItem{
    private String alertContent;
    private Date nextAlertTime;
    private String notes;
    private AlertType alertType;
    private static final String ALERT_TYPE_TAG = "AlertType:";

    private static final long NOTIFICATION_MS_BEFORE_EVENT_START = 5 * 60 * 1000;

    protected Alert(AlertType type){
        this.alertType = type;
    }
    public Alert(Event e){
        if (e == null){
            throw new IllegalArgumentException("event passed to alert cant be null");
        }

        this.setId(e.getId());
        this.alertContent = e.getSummary();

        if (e.getStart() != null){
            EventDateTime eventDateTime = e.getStart();

            if (eventDateTime.getDate() != null){
                this.nextAlertTime = new Date(eventDateTime.getDate().getValue());
            }
            else if (e.getStart().getDateTime() != null){
                this.nextAlertTime = new Date(eventDateTime.getDateTime().getValue());
            }
        }
        if (this.nextAlertTime == null){
            throw new AssertionError("appointment Date is null");
        }
        this.notes = e.getDescription();
        fillFieldsFromDescription(this.notes);
    }

    private void fillFieldsFromDescription(String description){
        if (description == null){
            return;
        }
        String[] lines = description.split("\\r?\\n");
        for(String line : lines){
            if (line.startsWith(ALERT_TYPE_TAG)){
                String toadd = line.replaceFirst(ALERT_TYPE_TAG, "");
                String name = AlertType.FoodAndDrink.name();
                String str = AlertType.FoodAndDrink.toString();
                if (toadd.equalsIgnoreCase(AlertType.FoodAndDrink.name())){
                    this.alertType = AlertType.FoodAndDrink;
                }
                else if (toadd.equalsIgnoreCase(AlertType.Medication.name())){
                    this.alertType = AlertType.Medication;
                }
                else if (toadd.equalsIgnoreCase(AlertType.Miscellaneous.name())){
                    this.alertType = AlertType.Miscellaneous;
                }
            }
        }
    }
    public String getAlertContent(){
        return alertContent;
    }

    public Date getAlertTime(){
        return nextAlertTime;
    }

    public AlertType getAlertType(){
        return this.alertType;
    }
    @Override
    public long getNumMsTillAlertShouldBeDisplayed() {
        return this.nextAlertTime.getTime() - NOTIFICATION_MS_BEFORE_EVENT_START;
    }

    @Override
    public String getNotificationTitle() {
        return "Heads up";
    }

    @Override
    public String getNotificationText() {
        return this.getAlertContent();
    }

    @Override
    public int getNotificationIcon() {
        return R.mipmap.speech_bubble_icon;
    }
}


