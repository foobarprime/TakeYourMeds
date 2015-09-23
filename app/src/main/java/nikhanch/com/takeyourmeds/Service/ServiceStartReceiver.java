package nikhanch.com.takeyourmeds.Service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class ServiceStartReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        startDataSyncService(context);
    }

    public static void startDataSyncService(Context context){
       // Toast.makeText(context, "service start request recd", Toast.LENGTH_LONG).show();
        Intent service = new Intent(context, GoogleCalendarDataSyncService.class);
        context.startService(service);

    }
}
