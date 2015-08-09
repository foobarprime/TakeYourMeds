package nikhanch.com.takeyourmeds.Service;

import android.accounts.AccountManager;
import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.CalendarScopes;
import com.squareup.otto.Produce;

import java.util.Arrays;

import nikhanch.com.takeyourmeds.Application;
import nikhanch.com.takeyourmeds.Utils.GoogleCalendarConstants;

/**
 * Created by nikhanch on 7/25/2015.
 */
public class GoogleCalendarAccountConnectionManager {

    private com.google.api.services.calendar.Calendar mService;
    private GoogleAccountCredential mCredential;
    private Service mParentService;
    private ConnectionStatus mConnectionStatus = null;


    // static defaults
    private static final String[] SCOPES = { CalendarScopes.CALENDAR_READONLY };

    public GoogleCalendarAccountConnectionManager(Service parentService) {

        Application.getEventBus().register(this);
        this.mParentService = parentService;

        // Initialize credentials and service object.
        SharedPreferences settings = parentService.getSharedPreferences(parentService.getClass().getName(), Context.MODE_PRIVATE);
        mCredential = GoogleAccountCredential.usingOAuth2(
                parentService.getApplicationContext(), Arrays.asList(SCOPES))
                .setBackOff(new ExponentialBackOff())
                .setSelectedAccountName(settings.getString(GoogleCalendarConstants.PREF_ACCOUNT_NAME, null));

        mService = new com.google.api.services.calendar.Calendar.Builder(
                AndroidHttp.newCompatibleTransport(), GsonFactory.getDefaultInstance(), mCredential)
                .setApplicationName("Google Calendar API Android Quickstart")
                .build();
        // Start syncing if possible
       startSyncIfPossible();
    }

    private void startSyncIfPossible(){

        Handler handlerToRunAsyncMsg = new Handler(mParentService.getMainLooper());
        Runnable asyncMsg = new Runnable() {
            @Override
            public void run() {
                if (isGooglePlayServicesAvailable()) {
                    if (mCredential.getSelectedAccountName() == null) {
                        fireEvent(ConnectionState.NeedAccont, ConnectionResult.SUCCESS);
                    }
                    else {
                        if (isDeviceOnline()) {
                            fireEvent(ConnectionState.StatusOk, ConnectionResult.SUCCESS);
                        }
                        else{
                            String errorText = "No network connection available.";
                            Toast.makeText(mParentService, errorText, Toast.LENGTH_LONG).show();
                        }
                    }
                }
            }
        };
        handlerToRunAsyncMsg.post(asyncMsg);
    }
    /**
     * Check that Google Play services APK is installed and up to date. Will
     * launch an error dialog for the user to update Google Play Services if
     * possible.
     * @return true if Google Play Services is available and up to
     *     date on this device; false otherwise.
     */
    // HACKHACK: break down into seperate types
    public boolean isGooglePlayServicesAvailable() {
        final int connectionStatusCode =
                GooglePlayServicesUtil.isGooglePlayServicesAvailable(mParentService);
        if (GooglePlayServicesUtil.isUserRecoverableError(connectionStatusCode)) {
            fireEvent(ConnectionState.UserCorrectibleError, connectionStatusCode);
            return false;
        } else if (connectionStatusCode != ConnectionResult.SUCCESS ) {
            return false;
        }
        return true;
    }

    public void OnUserActivityCompleted(int requestCode, int resultCode, Intent data){
        switch(requestCode) {
            case GoogleCalendarConstants.REQUEST_GOOGLE_PLAY_SERVICES:
                if (resultCode == Activity.RESULT_OK && isGooglePlayServicesAvailable()){
                    startSyncIfPossible();
                }
                else
                {
                    isGooglePlayServicesAvailable();
                }
                break;
            case GoogleCalendarConstants.REQUEST_ACCOUNT_PICKER:
                if (resultCode == Activity.RESULT_OK && data != null &&
                        data.getExtras() != null) {
                    String accountName =
                            data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
                    if (accountName != null) {
                        getGoogleAccountCredential().setSelectedAccountName(accountName);
                        SharedPreferences settings =
                                mParentService.getSharedPreferences(mParentService.getClass().getName(), Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putString(GoogleCalendarConstants.PREF_ACCOUNT_NAME, accountName);
                        editor.commit();
                    }
                    startSyncIfPossible();
                }
                else if (resultCode == Activity.RESULT_CANCELED) {
                    Toast.makeText(mParentService, "Please pick an account", Toast.LENGTH_SHORT).show();
                }
                break;
            case GoogleCalendarConstants.REQUEST_AUTHORIZATION:
                if (resultCode != Activity.RESULT_OK) {
                    fireEvent(ConnectionState.NeedAccont, ConnectionResult.SUCCESS);
                }
                else{
                    fireEvent(ConnectionState.StatusOk, ConnectionResult.SUCCESS);
                }
                break;
        }

    }
    /**
     * Checks whether the device currently has a network connection.
     * @return true if the device has a network connection, false otherwise.
     */
    private boolean isDeviceOnline() {
        ConnectivityManager connMgr =
                (ConnectivityManager) mParentService.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    private void fireEvent(ConnectionState newState, int newConnectionStatusCode){
        if (this.mConnectionStatus == null){
            this.mConnectionStatus = new ConnectionStatus();
        }
        this.mConnectionStatus.setConnectionState(newState, newConnectionStatusCode);
        Application.getEventBus().post(this.mConnectionStatus);
    }


    //region Getters
    @Produce
    public ConnectionStatus getConnectionContext() {
        return mConnectionStatus;
    }
    public GoogleAccountCredential getGoogleAccountCredential() {
        return mCredential;
    }

    public Calendar getGoogleCalendarService() {
        return mService;
    }

    //endregion

    //region ConnectionState
    public enum ConnectionState{
        UserCorrectibleError,
        NeedAccont,
        StatusOk
    };

    public class ConnectionStatus {
        private ConnectionState mConnectionState;
        private int mConnectionStatusCode;

        public ConnectionStatus(){
            this.mConnectionState = ConnectionState.StatusOk;
            this.mConnectionStatusCode = ConnectionResult.SUCCESS;
        }

        public ConnectionState getConnectionState(){
            return this.mConnectionState;
        }

        public void setConnectionState(ConnectionState newState, int newConnectionStatusCode){
            this.mConnectionState = newState;
            this.mConnectionStatusCode = newConnectionStatusCode;
        }

        public void setConnectionState(ConnectionState newState){
            setConnectionState(newState, ConnectionResult.SUCCESS);
        }

        public int getConnectionStatusCode(){
            return this.mConnectionStatusCode;
        }
    }
    //endregion
}


