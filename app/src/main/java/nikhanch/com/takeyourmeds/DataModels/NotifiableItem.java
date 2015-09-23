package nikhanch.com.takeyourmeds.DataModels;

/**
 * Created by nikhanch on 9/5/2015.
 */
public abstract class NotifiableItem {
    private String id;
    private boolean forceNotified;

    protected void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public boolean isForceNotified() {
        return forceNotified;
    }

    public void setForceNotified(boolean forceNotified) {
        this.forceNotified = forceNotified;
    }

    public abstract long getNumMsTillAlertShouldBeDisplayed();

    public abstract String getNotificationTitle();

    public abstract String getNotificationText();

    public abstract int getNotificationIcon();
}
