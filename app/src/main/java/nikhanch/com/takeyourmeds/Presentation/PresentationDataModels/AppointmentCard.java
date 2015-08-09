package nikhanch.com.takeyourmeds.Presentation.PresentationDataModels;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.ocpsoft.prettytime.PrettyTime;


import java.util.Random;

import it.gmariotti.cardslib.library.internal.Card;
import nikhanch.com.takeyourmeds.DataModels.Appointment;
import nikhanch.com.takeyourmeds.Presentation.AppointmentActivity;
import nikhanch.com.takeyourmeds.Presentation.View.CircleImageTransformation;
import nikhanch.com.takeyourmeds.R;
import timber.log.Timber;

/**
 * Created by nikhanch on 7/23/2015.
 */
public class AppointmentCard extends Card implements Comparable<AppointmentCard> {

    public Appointment getAppointment() {
        return mAppointment;
    }

    Appointment mAppointment;
    /**
     * Constructor with a custom inner layout
     * @param context
     */
    public AppointmentCard(Context context, Appointment a) {
        super(context, R.layout.appointment_row_card);
        init(a);
    }

    /**
     * Init
     */
    private void init(Appointment a){

        this.mAppointment = a;

        //Set a OnClickListener listener
        setOnClickListener(new OnCardClickListener() {
            @Override
            public void onClick(Card card, View view) {
                Intent startAppointmentActivityIntent = new Intent(getContext(), AppointmentActivity.class);

                Toast.makeText(getContext(), "Click Listener card=", Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void setupInnerViewElements(ViewGroup parent, View view) {

        if (this.mAppointment == null){
            return;
        }
        //Retrieve elements
        TextView doctorsNameTextView = (TextView) parent.findViewById(R.id.card_main_doctors_name);
        TextView procedureNameTextView = (TextView) parent.findViewById(R.id.card_main_procedure_name);
        TextView timeTextView = (TextView) parent.findViewById(R.id.card_main_time);
        ImageView imageView = (ImageView)parent.findViewById(R.id.doctorPhoto);

        if (doctorsNameTextView != null) {
            doctorsNameTextView.setText(this.mAppointment.getDoctorsName());
        }

        if (procedureNameTextView != null) {
            procedureNameTextView.setText(this.mAppointment.getProcedureName());
        }

        if (timeTextView != null) {
            String appointmentDateText = "";
            if (this.mAppointment.getAppointmentDate() != null){

                PrettyTime time = new PrettyTime();
                appointmentDateText = time.format(this.mAppointment.getAppointmentDate());
            }
            timeTextView.setText(appointmentDateText);
        }

        //region updatePhoto
        try {
            if (this.mAppointment.getDoctorPhoto() != null && !this.mAppointment.getDoctorPhoto().isEmpty()) {

                Picasso.with(getContext()).load(this.mAppointment.getDoctorPhoto()).transform(new CircleImageTransformation()).into(imageView, new Callback() {
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
                if (mAppointment.getDoctorsName() != null && !mAppointment.getDoctorsName().isEmpty()) {
                    doctorsInitials = mAppointment.getDoctorsName().substring(0, 1);
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


    }

    @Override
    public int compareTo(AppointmentCard another) {

        final int moveDown = 1;
        final int moveUp = -1;


        if (this.getAppointment().getAppointmentDate() == null){
            return moveDown;
        }
        if (another.getAppointment().getAppointmentDate() == null){
            return moveUp;
        }

        // Want the latest to be on top so they have to be further down in the array
        return (this.getAppointment().getAppointmentDate().before(another.getAppointment().getAppointmentDate())) ? moveDown : moveUp;
    }
}
