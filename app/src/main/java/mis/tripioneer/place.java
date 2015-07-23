package mis.tripioneer;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;


public class place extends ActionBarActivity {
    private Integer placeid ;
    private final static int DOWNLOAD_COMPLETE = 1;
    private String ret ;
    private final String CASE = "PLACE";
    private static final int PLACE_PARAM_NUM = 1;
    private static final int RET_PARAM_NUM = 4;
    private String[] request_name  = new String[PLACE_PARAM_NUM];
    private String[] request_values = new String[PLACE_PARAM_NUM];
    private static String[] ret_place  = new String[RET_PARAM_NUM];
    final String URL_PLACEGRAB = "http://140.115.80.224:8080/Place_Grab.php";
    private static final String URL_PREFIX_IMAGE = "http://140.115.80.224:8080/group4/image/";
    private TextView place ;
    private TextView address;
    private TextView hours;
    private TextView intro ;
    private ImageView photo;
    private static List<ViewModel> viewModels;
    private static ViewAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place);

        Bundle placedata = this.getIntent().getExtras();
        //placeid = placedata.getInt(specifyid);
        //bundle data = specifyid ,placeid is local data and have to translate into string,for request_value[0]
        
        request_name[0] = "place_ID";
        request_values[0] = placeid.toString();
        place = (TextView)findViewById(R.id.titletext);
        address = (TextView)findViewById(R.id.addresstext);
        hours = (TextView)findViewById(R.id.timetext);
        intro = (TextView)findViewById(R.id.infotext);
        photo = (ImageView)findViewById(R.id.imageView);
        new Thread(getdata).start();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    Handler handler =new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {   super.handleMessage(msg);
                 switch(msg.what)
                {   case DOWNLOAD_COMPLETE:
                        place.setText(ret_place[0]);
                        hours.setText(ret_place[1]);
                        address.setText(ret_place[2]);
                        intro.setText(ret_place[3]);

                    try
                    {
                        Picasso.with(getApplicationContext())
                        .load(URL_PREFIX_IMAGE
                            + URLEncoder.encode(ret_place[0], "UTF-8") + ".jpg")
                                .into(photo);
                    }
                    catch (UnsupportedEncodingException e)
                    {
                        e.printStackTrace();
                    }
                    break;
                    default:
                        Log.d("Nick","Download Failed");
                        break;
            }
        }
    };

    Runnable getdata = new Runnable() {
        @Override
        public void run() {
            ConnectServer conncetion = new ConnectServer(URL_PLACEGRAB);
            ret = conncetion.connect(request_name, request_values, PLACE_PARAM_NUM);
            JsonParser parser = new JsonParser(RET_PARAM_NUM,CASE);
            ret_place = parser.Parse(ret,"");

            for(int i=0; i< RET_PARAM_NUM;i++)
            Log.d("Nick", ret_place[i]);
            handler.sendEmptyMessage(DOWNLOAD_COMPLETE);
        }
    };

}
