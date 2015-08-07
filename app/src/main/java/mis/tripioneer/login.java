package mis.tripioneer;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class Login extends AppCompatActivity
{
    private String pwd="";
    private String ret_pwd="";
    private String ret_register_status="";
    private String CASE="";
    private final int LOGIN_NUM_PARAM = 1;
    private final int REGISTER_NUM_PARAM = 2;
    private final int MAX_NUM_PARAM = Math.max(LOGIN_NUM_PARAM,REGISTER_NUM_PARAM);
    private String[] request_name = new String[MAX_NUM_PARAM];
    private String[] request_value = new String[MAX_NUM_PARAM];

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Button login = (Button) findViewById(R.id.login);
        Button register = (Button) findViewById(R.id.register);
        final TextView password = (TextView)findViewById(R.id.editText);
        final TextView account = (TextView)findViewById(R.id.username);

        View.OnClickListener login_listener = new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                CASE = "LOGIN";
                request_name[0] = "useraccount";
                request_value[0] = account.getText().toString();
                pwd = password.getText().toString();
                new Thread(run_login).start();
            }
        };

        View.OnClickListener register_listener = new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                CASE = "REGISTER";
                request_name[0] = "requestname0";
                request_name[1] = "requestname1";
                request_value[0] = account.getText().toString();
                request_value[1] = password.getText().toString();

                new Thread(run_register).start();
            }
        };
        login.setOnClickListener(login_listener);
        register.setOnClickListener(register_listener);
    }

    //TODO:登入需要ACTION BAR嗎? 要長怎樣?
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_login, menu);
        return super.onCreateOptionsMenu(menu);

    }

    protected void onPause() {
        super.onPause();


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }

    Handler handler_Success = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            super.handleMessage(msg);
            switch(CASE)
            {
                case "LOGIN":
                    if(pwd.equals(ret_pwd))
                    {
                        Toast.makeText(getApplicationContext(), "Hi", Toast.LENGTH_LONG).show();
                        gotoRecommendation();
                     }
                    else
                    {
                        Toast.makeText(getApplicationContext(), "Wrong Account/Password!", Toast.LENGTH_LONG).show();
                    }
                    break;
                case "REGISTER":
                    Toast.makeText(getApplicationContext(),ret_register_status , Toast.LENGTH_LONG).show();
                    break;
                default:
                    break;
            }
        }
    };

    Handler handler_Error = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            super.handleMessage(msg);
            Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_LONG).show();
        }
    };

    Handler handler_Nodata = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            super.handleMessage(msg);
            Toast.makeText(getApplicationContext(), "Nodata", Toast.LENGTH_LONG).show();
        }
    };

    Runnable run_login = new Runnable()
    {
        @Override
        public void run()
        {

            ArrayList<String> ret;
            final String URL_LOGIN = "http://140.115.80.224:8080/Login_Authentication.php";


            ConnectServer connection = new ConnectServer(URL_LOGIN);
            ret_pwd = connection.connect(request_name, request_value, LOGIN_NUM_PARAM);

            JsonParser parser = new JsonParser(CASE);
            ret = parser.Parse(ret_pwd,"user_Pwd");
            ret_pwd = ret.get(0);

            if(ret_pwd == null)
            {
                handler_Nodata.sendMessage(new Message());
            }
            else
            {
                handler_Success.sendMessage(new Message());
            }
        }
    };

    Runnable run_register = new Runnable()
    {
        @Override
        public void run()
        {

            final String URL_REGISTER = "http://140.115.80.224:8080/SignUp.php";

            ConnectServer connection = new ConnectServer(URL_REGISTER);
            ret_register_status = connection.connect(request_name,request_value,REGISTER_NUM_PARAM);

            if(ret_register_status == null)
            {
                handler_Nodata.sendMessage(new Message());
            }
            else
            {
                handler_Success.sendMessage(new Message());
            }
        }
    };

    public void gotoRecommendation()
    {
        Intent GO_TO_RECOMM = new Intent(Login.this, Recommendation.class);
        startActivity(GO_TO_RECOMM);
    }

}
