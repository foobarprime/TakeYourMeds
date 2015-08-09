package nikhanch.com.takeyourmeds;

import com.squareup.otto.Bus;
import com.squareup.otto.ThreadEnforcer;
import static timber.log.Timber.DebugTree;
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
        this.eventBus = new EventBus(ThreadEnforcer.MAIN);

        if (BuildConfig.DEBUG) {
            Timber.plant(new DebugTree());
        } else {
            // TODO: add crash reportingfrom arca
            //Timber.plant(new CrashReportingTree());
        }
    }

    public static EventBus getEventBus() {
        return eventBus;
    }
}
