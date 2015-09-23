package nikhanch.com.takeyourmeds.Service;

/**
 * Created by nikhanch on 8/9/2015.
 */
public class GoogleApiCommunicationErrorContext {

    public enum ErrorType{
        GooglePlayServicesAvailabilityException,
        UserRecoverableException,
        OtherException
    }

    private ErrorType mErrorType;
    private Exception mException;

    public GoogleApiCommunicationErrorContext(Exception e, ErrorType errorType){
        this. mErrorType = errorType;
        this.mException = e;
    }
    public ErrorType getErrorType() {
        return mErrorType;
    }

    public Exception getException() {
        return mException;
    }
}
