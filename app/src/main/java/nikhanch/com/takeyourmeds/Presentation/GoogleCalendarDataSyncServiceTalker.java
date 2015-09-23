package nikhanch.com.takeyourmeds.Presentation;

import android.app.Activity;
import android.app.Dialog;
import android.support.v4.app.Fragment;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.api.client.googleapis.extensions.android.gms.auth.GooglePlayServicesAvailabilityIOException;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.squareup.otto.Subscribe;

import nikhanch.com.takeyourmeds.Application;
import nikhanch.com.takeyourmeds.Service.GoogleApiCommunicationErrorContext;
import nikhanch.com.takeyourmeds.Service.GoogleCalendarDataSyncService;
import nikhanch.com.takeyourmeds.Service.GoogleCalendarAccountConnectionManager;
import nikhanch.com.takeyourmeds.Utils.GoogleCalendarConstants;
import timber.log.Timber;

/**
 * Created by nikhanch on 7/25/2015.
 */
public abstract class GoogleCalendarDataSyncServiceTalker extends Fragment{
    protected GoogleCalendarDataSyncService mGoogleCalendarDataSyncService;

    protected BusEventListener busEventListener;



    public class BusEventListener{
        private GoogleCalendarDataSyncServiceTalker mManager;
        boolean registered = false;
        public BusEventListener(GoogleCalendarDataSyncServiceTalker manager){
            this.mManager = manager;
        }

        public void onServiceConnected(){
            if (!registered) {
                Application.getEventBus().register(this);
                registered = true;
            }
        }

        public void onServiceDisconnected(){
            if (registered){
                Application.getEventBus().unregister(this);
                registered = false;
            }
        }
        @Subscribe public void OnAppointmentCalendarSyncFailed(final GoogleApiCommunicationErrorContext googleApiCommunicationErrorContext){
            mManager.OnGoogleApiCommunicationError(googleApiCommunicationErrorContext);
        }
        @Subscribe public void onConnectionStatusAvailable(final GoogleCalendarAccountConnectionManager.ConnectionStatus status){
            mManager.onConnectionStatusAvailableEvent(status);
        }
    }

    private ServiceConnection mConnection = new ServiceConnection(){
        @Override
        public void onServiceConnected(ComponentName name, IBinder binder) {
            GoogleCalendarDataSyncService.GoogleCalendarDataSyncServiceBinder googleCalendarDataSyncServiceBinder = (GoogleCalendarDataSyncService.GoogleCalendarDataSyncServiceBinder) binder;
            mGoogleCalendarDataSyncService = googleCalendarDataSyncServiceBinder.getGoogleCalendarDataSyncService();
            busEventListener.onServiceConnected();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mGoogleCalendarDataSyncService = null;
            busEventListener.onServiceDisconnected();
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.busEventListener = new BusEventListener(this);

        Intent i = new Intent(getActivity(), GoogleCalendarDataSyncService.class);
        getActivity().bindService(i, mConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unbindService(mConnection);
    }

    public void onConnectionStatusAvailableEvent(final GoogleCalendarAccountConnectionManager.ConnectionStatus status){

        if (status != null) {
            switch (status.getConnectionState()) {
                case UserCorrectibleError:
                    showGooglePlayServicesAvailabilityErrorDialog(mGoogleCalendarDataSyncService.getGoogleCalendarTalker().getConnectionContext().getConnectionStatusCode());
                    return;
                case NeedAccont:
                    chooseAccount();
                    return;
                case StatusOk:
                    Log.d("Connection State", "Connection status is ok");
                    return;
            }
        }
    }

    public void OnGoogleApiCommunicationError(final GoogleApiCommunicationErrorContext googleApiCommunicationErrorContext) {
        // TODO: figure out when error has been corrected and issue re-sync
        if (googleApiCommunicationErrorContext != null) {
            switch (googleApiCommunicationErrorContext.getErrorType()) {
                case GooglePlayServicesAvailabilityException: {
                    GooglePlayServicesAvailabilityIOException exception = (GooglePlayServicesAvailabilityIOException) googleApiCommunicationErrorContext.getException();
                    showGooglePlayServicesAvailabilityErrorDialog(exception.getConnectionStatusCode());
                    return;
                }
                case UserRecoverableException: {
                    UserRecoverableAuthIOException exception = (UserRecoverableAuthIOException) googleApiCommunicationErrorContext.getException();
                    startActivityForResult(exception.getIntent(), GoogleCalendarConstants.REQUEST_AUTHORIZATION);
                }
                case OtherException:
                    Timber.e("Appointment update", googleApiCommunicationErrorContext.getException().getMessage());
                    Toast.makeText(getActivity(), "Appointment update error " + googleApiCommunicationErrorContext.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * Display an error dialog showing that Google Play Services is missing
     * or out of date.
     * @param connectionStatusCode code describing the presence (or lack of)
     *     Google Play Services on this device.
     */
    void showGooglePlayServicesAvailabilityErrorDialog(final int connectionStatusCode) {
        final Activity thisActivity = getActivity();
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Dialog dialog = GooglePlayServicesUtil.getErrorDialog(
                        connectionStatusCode,
                        thisActivity,
                        GoogleCalendarConstants.REQUEST_GOOGLE_PLAY_SERVICES);
                dialog.show();
            }
        });
    }

    /**
     * Starts an activity in Google Play Services so the user can pick an
     * account.
     */
    private void chooseAccount() {
        startActivityForResult(
                mGoogleCalendarDataSyncService.getGoogleCalendarTalker().getGoogleAccountCredential().newChooseAccountIntent(), GoogleCalendarConstants.REQUEST_ACCOUNT_PICKER);
    }

    /**
     * Called when an activity launched here (specifically, AccountPicker
     * and authorization) exits, giving you the requestCode you started it with,
     * the resultCode it returned, and any additional data from it.
     * @param requestCode code indicating which activity result is incoming.
     * @param resultCode code indicating the result of the incoming
     *     activity result.
     * @param data Intent (containing result data) returned by incoming
     *     activity result.
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        mGoogleCalendarDataSyncService.getGoogleCalendarTalker().OnUserActivityCompleted(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }



}
