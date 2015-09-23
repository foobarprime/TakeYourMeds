package nikhanch.com.takeyourmeds.Service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

public class GoogleCalendarDataSyncService extends Service {

    private final IBinder mBinder = new GoogleCalendarDataSyncServiceBinder();
    private GoogleCalendarAccountConnectionManager mGoogleCalendarTalker;
    private CalendarManager mCalendarManager;
    private AppNotificationManager mNotificationManager;

    private boolean serviceStarted = false;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        StartServiceIfRequired();

        return Service.START_STICKY;
    }

    @Override
    public void onDestroy() {
        this.serviceStarted = false;
    }


    private void StartServiceIfRequired(){
        if (this.serviceStarted){
            return;
        }

        this.mGoogleCalendarTalker = new GoogleCalendarAccountConnectionManager(this);
        this.mCalendarManager = new CalendarManager(this);
        this.mNotificationManager = new AppNotificationManager(this);
        this.serviceStarted = true;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return this.mBinder;
    }

    public GoogleCalendarAccountConnectionManager getGoogleCalendarTalker(){
        return this.mGoogleCalendarTalker;
    }
    public class GoogleCalendarDataSyncServiceBinder extends Binder {
        public GoogleCalendarDataSyncService getGoogleCalendarDataSyncService(){
            return GoogleCalendarDataSyncService.this;
        }
    }

    public boolean isServiceStarted() {
        return serviceStarted;
    }
}




