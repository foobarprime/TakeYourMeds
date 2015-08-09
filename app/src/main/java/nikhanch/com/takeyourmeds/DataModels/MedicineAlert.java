package nikhanch.com.takeyourmeds.DataModels;

import java.util.Date;
import java.util.GregorianCalendar;

public class MedicineAlert {
    private String medicineName;
    private Date nextAlertTime;

    public MedicineAlert(String medicineName, Date nextAlertTime){
        this.medicineName = medicineName;
        this.nextAlertTime = nextAlertTime;
    }

    public String getMedicineName(){
        return this.medicineName;
    }

    public Date getNextAlertTime(){
        return this.nextAlertTime;
    }
}
