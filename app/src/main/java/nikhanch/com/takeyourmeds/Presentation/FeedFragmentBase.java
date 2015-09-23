package nikhanch.com.takeyourmeds.Presentation;

/**
 * Created by nikhanch on 8/9/2015.
 */
public class FeedFragmentBase extends GoogleCalendarDataSyncServiceTalker {

    enum FeedFragmentType{
        FRAGMENT_TYPE_APPOINTMENT_LIST,
        FRAGMENT_TYPE_MEDICATION_LIST,
        FRAGMENT_TYPE_ALERT_LIST
    }
    public static FeedFragmentBase newInstance(FeedFragmentType type) {

        FeedFragmentBase fragment = null;
        switch(type){
            case FRAGMENT_TYPE_APPOINTMENT_LIST :   fragment = new AppointmentFeedFragment(); break;
            case FRAGMENT_TYPE_MEDICATION_LIST:     fragment = new MedicationFeedFragment(); break;
            case FRAGMENT_TYPE_ALERT_LIST:          fragment = new AlertsFeedFragment(); break;
        }

        return fragment;
    }
}
