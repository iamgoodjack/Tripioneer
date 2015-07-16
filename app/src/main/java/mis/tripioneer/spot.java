package mis.tripioneer;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.net.URL;


public class spot extends ActionBarActivity {
    protected Integer placenum = 0;
    final TextView place = (TextView)findViewById(R.id.titletext);
    final TextView address = (TextView)findViewById(R.id.addresstext);
    final TextView hours = (TextView)findViewById(R.id.timetext);
    final TextView intro = (TextView)findViewById(R.id.infotext);
    final String URL_PLACEGRAB = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spot);
        Button favorite = (Button)findViewById(R.id.favoritebutton);
        //Button guide = (Button)findViewById(R.id.guidebutton);
        View.OnClickListener addintofavorite = new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        };
        favorite.setOnClickListener(addintofavorite);
        new Thread(getdata).start();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_spot, menu);
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
    Runnable getdata = new Runnable() {
        @Override
        public void run() {
            ConnectServer conncet = new ConnectServer(URL_PLACEGRAB);

        }
    };
}
