package mis.tripioneer;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;

//使用google api 資訊搜集器
public class place_api extends Activity
{
    private static final int PLACE_PICKER_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        try {
            PlacePicker.IntentBuilder intentBuilder = new PlacePicker.IntentBuilder();
            Intent intent = intentBuilder.build(getApplicationContext());
            startActivityForResult(intent, PLACE_PICKER_REQUEST);
        } catch (GooglePlayServicesRepairableException e) {
            e.printStackTrace();
        } catch (GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {

        if (requestCode == PLACE_PICKER_REQUEST && resultCode == Activity.RESULT_OK)
        {
            final Place place = PlacePicker.getPlace(data, this);
            final CharSequence name = place.getName();
            final CharSequence address = place.getAddress();
            String attributions = PlacePicker.getAttributions(data);
            if (attributions == null)
            {
                attributions = "";
            }
            Log.d("gina", String.valueOf(name));
            Log.d("lisa", String.valueOf(address));
            Log.d("gina", String.valueOf(attributions));

            if (name!=null)
            {
                Log.d("cathy", String.valueOf(name));
                Log.d("ted", String.valueOf(name));
            Log.d("meg", String.valueOf(name));
            String toastMsg = String.format("選取的景點資訊: %s", name);
            Toast.makeText(this, toastMsg, Toast.LENGTH_LONG).show();

        }
    } else {
    super.onActivityResult(requestCode, resultCode, data);
        }
    }

}