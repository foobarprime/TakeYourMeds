package nikhanch.com.takeyourmeds.Service;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.SystemClock;
import android.text.format.DateUtils;
import android.widget.Toast;

import com.squareup.otto.Subscribe;
import com.squareup.seismic.ShakeDetector;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Dictionary;
import java.util.HashMap;

import nikhanch.com.takeyourmeds.Application;
import nikhanch.com.takeyourmeds.DataModels.Alert;
import nikhanch.com.takeyourmeds.DataModels.Appointment;
import nikhanch.com.takeyourmeds.DataModels.MedicineAlert;
import nikhanch.com.takeyourmeds.DataModels.NotifiableItem;
import nikhanch.com.takeyourmeds.R;


/**
 * Created by nikhanch on 9/5/2015.
 */

public class AppNotificationManager implements ShakeDetector.Listener  {

    private ArrayList<MedicineAlert> medicineAlerts;
    private ArrayList<Appointment> appointments;
    private ArrayList<Alert> alerts;
    private HashMap<String, NotifiableItem> notificationsSet = new HashMap<>();

    GoogleCalendarDataSyncService parentService;

    public AppNotificationManager(GoogleCalendarDataSyncService parentService){
        this.parentService = parentService;

        SensorManager sensorManager = (SensorManager) parentService.getSystemService(Context.SENSOR_SERVICE);
        ShakeDetector sd = new ShakeDetector(this);
        sd.start(sensorManager);

        Application.getEventBus().register(this);

    }

    @Override
    public void hearShake() {
        Toast.makeText(parentService, "Shake found", Toast.LENGTH_SHORT).show();
        Collection<NotifiableItem> notifiableItems = notificationsSet.values();
        NotifiableItem earliestItem = null;
        for (NotifiableItem n : notifiableItems){
            if (!n.isForceNotified()) {
                long numMsTillNotificiation = n.getNumMsTillAlertShouldBeDisplayed();
                if (numMsTillNotificiation > 0) {
                    if (earliestItem == null) {
                        earliestItem = n;
                    } else if (earliestItem != null && numMsTillNotificiation < earliestItem.getNumMsTillAlertShouldBeDisplayed()) {
                        earliestItem = n;
                    }
                }
            }
        }

        if (earliestItem != null){
            Toast.makeText(parentService, "item found " + earliestItem.getNotificationText(), Toast.LENGTH_SHORT).show();
            Notification n = getNotification(earliestItem);
            scheduleNotification(n, earliestItem.getNotificationText(), earliestItem.getId().hashCode(), 5 * 1000 /*5 seconds from now*/ );
            earliestItem.setForceNotified(true);
        }
    }

    @Subscribe
    public void OnMedicineAlertsUpdated(MedicationCalendarManager.MedicationsUpdatedEvent event){
        if (event != null && event.getUpdatedMedicineAlerts() != null)
        medicineAlerts = event.getUpdatedMedicineAlerts().getMedications();
        UpdateNotifications();
    }

    @Subscribe
    public void OnAppointmentsUpdated(AppointmentsCalendarManager.AppointmentsUpdatedEvent updatedAppointments){

        if (updatedAppointments != null && updatedAppointments.getUpdatedAppointments() != null){
            appointments = updatedAppointments.getUpdatedAppointments().getAppointments();
            UpdateNotifications();
        }

    }

    @Subscribe
    public void OnAlertsUpdated(AlertCalendarManager.AlertsUpdatedEvent event){
        if (event != null && event.getUpdatedAlerts() != null){
            this.alerts = event.getUpdatedAlerts().getAlerts();
            UpdateNotifications();
        }
    }

    private void UpdateNotifications(){
        if (medicineAlerts!= null) {
            for (MedicineAlert m : medicineAlerts) {
                if (DateUtils.isToday(m.getNextAlertTime().getTime()) && !notificationsSet.containsKey(m.getId()) && !m.isForceNotified()) {
                    Notification n = getNotification(m);
                    scheduleNotification(n, m.getMedicineName(), m.getId().hashCode(), m.getNumMsTillAlertShouldBeDisplayed());
                    notificationsSet.put(m.getId(), m);
                }
            }
        }

        if (appointments != null) {
            for (Appointment a : appointments) {
                if (DateUtils.isToday(a.getAppointmentDate().getTime()) && !notificationsSet.containsKey(a.getId()) && !a.isForceNotified()) {
                    Notification n = getNotification(a);
                    scheduleNotification(n, a.getProcedureName(), a.getId().hashCode(), a.getNumMsTillAlertShouldBeDisplayed());
                    notificationsSet.put(a.getId(), a);

                }
            }
        }

        if (this.alerts != null){
            for (Alert a : alerts) {
                if (DateUtils.isToday(a.getAlertTime().getTime()) && !notificationsSet.containsKey(a.getId()) && !a.isForceNotified()) {
                    Notification n = getNotification(a);
                    scheduleNotification(n, a.getAlertContent(), a.getId().hashCode(), a.getNumMsTillAlertShouldBeDisplayed());
                    notificationsSet.put(a.getId(), a);

                }
            }
        }
    }

    private void scheduleNotification(Notification notification, String title, int id, long delay) {

        Intent notificationIntent = new Intent(parentService, NotificationPublisher.class);
        notificationIntent.putExtra(NotificationPublisher.NOTIFICATION_ID, id);
        notificationIntent.putExtra(NotificationPublisher.NOTIFICATION, notification);
        notificationIntent.putExtra(NotificationPublisher.TITLE, title);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(parentService, id, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        long futureInMillis = SystemClock.elapsedRealtime() + delay;

        AlarmManager alarmManager = (AlarmManager)parentService.getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, futureInMillis, pendingIntent);
    }

    private Notification getNotification(NotifiableItem n) {
        Notification.Builder builder = new Notification.Builder(parentService);
        builder.setContentTitle(n.getNotificationTitle());
        builder.setContentText(n.getNotificationText());
        builder.setSmallIcon(n.getNotificationIcon());

        return builder.build();
    }


}
