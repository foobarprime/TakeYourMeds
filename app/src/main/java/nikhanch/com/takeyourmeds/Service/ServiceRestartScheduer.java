package nikhanch.com.takeyourmeds.Service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.util.Calendar;

public class ServiceRestartScheduer extends BroadcastReceiver {

    private static final long REPEAT_TIME = 1000 * 60 * 60; //try to restart every hour
    @Override
    public void onReceive(Context context, Intent intent) {

        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, new Intent(context, ServiceStartReceiver.class), PendingIntent.FLAG_CANCEL_CURRENT);

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.HOUR, 1);

        AlarmManager service = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        service.setInexactRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), REPEAT_TIME, pendingIntent);

    }
}
