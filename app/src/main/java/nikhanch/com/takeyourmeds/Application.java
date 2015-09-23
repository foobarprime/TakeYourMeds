package nikhanch.com.takeyourmeds;

import android.support.annotation.Nullable;
import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.squareup.otto.Bus;
import com.squareup.otto.ThreadEnforcer;
import static timber.log.Timber.DebugTree;
import io.fabric.sdk.android.Fabric;
import nikhanch.com.takeyourmeds.Utils.EventBus;
import timber.log.Timber;

/**
 * Created by nikhanch on 7/26/2015.
 */
public class Application extends android.app.Application {

    private static EventBus eventBus;

    @Override
    public void onCreate() {
        super.onCreate();



        if (BuildConfig.DEBUG) {
            Timber.plant(new DebugTree());
        }
        else{
            Fabric.with(this, new Crashlytics());
            Timber.plant(new CrashlyticsTree());
        }

        this.eventBus = new EventBus(ThreadEnforcer.MAIN);
    }

    public static EventBus getEventBus() {
        return eventBus;
    }

    public class CrashlyticsTree extends Timber.Tree {
        private static final String CRASHLYTICS_KEY_PRIORITY = "priority";
        private static final String CRASHLYTICS_KEY_TAG = "tag";
        private static final String CRASHLYTICS_KEY_MESSAGE = "message";

        @Override
        protected void log(int priority, @Nullable String tag, @Nullable String message, @Nullable Throwable t) {
            if (priority == Log.VERBOSE || priority == Log.DEBUG || priority == Log.INFO) {
                return;
            }

            Crashlytics.setInt(CRASHLYTICS_KEY_PRIORITY, priority);
            Crashlytics.setString(CRASHLYTICS_KEY_TAG, tag);
            Crashlytics.setString(CRASHLYTICS_KEY_MESSAGE, message);

            if (t == null) {
                Crashlytics.logException(new Exception(message));
            } else {
                Crashlytics.logException(t);
            }
        }
    }
}
