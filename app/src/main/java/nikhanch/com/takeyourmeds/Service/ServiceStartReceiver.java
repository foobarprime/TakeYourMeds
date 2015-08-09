package nikhanch.com.takeyourmeds.Service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class ServiceStartReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent service = new Intent(context, GoogleCalendarDataSyncService.class);
        context.startService(service);
    }
}
