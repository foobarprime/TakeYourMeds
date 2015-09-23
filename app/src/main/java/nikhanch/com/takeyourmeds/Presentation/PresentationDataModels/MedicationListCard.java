package nikhanch.com.takeyourmeds.Presentation.PresentationDataModels;

import android.content.Context;
import android.widget.ArrayAdapter;

import it.gmariotti.cardslib.library.internal.Card;
import nikhanch.com.takeyourmeds.DataModels.MedicineAlert;
import nikhanch.com.takeyourmeds.R;

/**
 * Created by nikhanch on 9/19/2015.
 */
public class MedicationListCard extends Card {

    ArrayAdapter<MedicineAlert> medicineAlertArrayAdapter;
    public MedicationListCard(Context context, ArrayAdapter<MedicineAlert> medicineAlertArrayAdapter){
        super(context, R.layout.medication_card_view);
        this.medicineAlertArrayAdapter = medicineAlertArrayAdapter;
    }
}
