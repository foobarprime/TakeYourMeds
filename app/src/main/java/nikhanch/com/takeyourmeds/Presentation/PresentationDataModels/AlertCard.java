package nikhanch.com.takeyourmeds.Presentation.PresentationDataModels;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import it.gmariotti.cardslib.library.internal.Card;
import nikhanch.com.takeyourmeds.DataModels.Alert;
import nikhanch.com.takeyourmeds.R;

/**
 * Created by nikhanch on 9/20/2015.
 */
public class AlertCard extends Card implements Comparable<AlertCard>{
    public Alert getAlert() {
        return mAlert;
    }

    Alert mAlert;
    /**
     * Constructor with a custom inner layout
     */
    public AlertCard(Context context, Alert a) {
        super(context, R.layout.alert_row);
        init(a);
    }

    /**
     * Init
     */
    private void init(Alert a){

        this.mAlert = a;

    }

    @Override
    public void setupInnerViewElements(ViewGroup parent, View view) {

        if (this.mAlert == null){
            return;
        }
        /*
        //Retrieve elements
        TextView doctorsNameTextView = (TextView) parent.findViewById(R.id.card_main_doctors_name);
        TextView procedureNameTextView = (TextView) parent.findViewById(R.id.card_main_procedure_name);
        TextView timeTextView = (TextView) parent.findViewById(R.id.card_main_time);
        ImageView imageView = (ImageView)parent.findViewById(R.id.doctorPhoto);

        if (doctorsNameTextView != null) {
            doctorsNameTextView.setText(this.mAlert.getDoctorsName());
        }

        if (procedureNameTextView != null) {
            procedureNameTextView.setText(this.mAlert.getProcedureName());
        }

        if (timeTextView != null) {
            String appointmentDateText = "";
            if (this.mAlert.getAppointmentDate() != null){

                PrettyTime time = new PrettyTime();
                appointmentDateText = time.format(this.mAlert.getAppointmentDate());
            }
            timeTextView.setText(appointmentDateText);
        }

        //region updatePhoto
        try {
            if (this.mAlert.getDoctorPhoto() != null && !this.mAlert.getDoctorPhoto().isEmpty()) {

                Picasso.with(getContext()).load(this.mAlert.getDoctorPhoto()).transform(new CircleImageTransformation()).into(imageView, new Callback() {
                    @Override
                    public void onSuccess() {
                        Timber.d("success");
                    }

                    @Override
                    public void onError() {
                        Timber.d("error");
                    }
                });
            } else {
                String doctorsInitials;
                if (mAlert.getDoctorsName() != null && !mAlert.getDoctorsName().isEmpty()) {
                    doctorsInitials = mAlert.getDoctorsName().substring(0, 1);
                } else {
                    Random r = new Random();
                    doctorsInitials = Character.toString((char) (r.nextInt(26) + 'A'));
                }

                int color = ColorGenerator.MATERIAL.getRandomColor();

                TextDrawable drawable = TextDrawable.builder().buildRound(doctorsInitials, color);

                imageView.setImageDrawable(drawable);
            }
        }
        catch (Exception e){
            String msg = e.getMessage();
            String error = msg;
        }
        //endregion

*/
    }
    @Override
    public int compareTo(AlertCard another) {

        final int moveDown = 1;
        final int moveUp = -1;


        if (this.getAlert().getAlertTime() == null){
            return moveDown;
        }
        if (another.getAlert().getAlertTime() == null){
            return moveUp;
        }

        // Want the latest to be on top so they have to be further down in the array
        return (this.getAlert().getAlertTime().before(another.getAlert().getAlertTime())) ? moveDown : moveUp;
    }
}
