package mis.tripioneer;

import android.util.Log;

import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/************************************************************************************************************
 *  Name : ConnectServer.java
 *  Function : Connecting to URL by using a http post method
 *
 ************************************************************************************************************/
public class ConnectServer
{
    private HttpURLConnection conn = null;
    private URL url;
    public ConnectServer(String URL)
    {
        try
        {
            url = new URL(URL);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }

    /************************************************************************************************************
     * Function : connect
     * Definition : Connecting to URL by using a http post method
     * Return : String ret from http response
     ************************************************************************************************************/
    public String connect( String request_name[], String request_value[], int param_num,String CASE )
    {
        String name[]=request_name;
        String value[]=request_value;
        String ret="";
        String line;
        StringBuffer response = new StringBuffer();
        StringBuffer request = new StringBuffer();

        try
        {
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setConnectTimeout(10000);
            conn.setDoOutput(true);
            conn.setDoInput(true);
            DataOutputStream out = new DataOutputStream(conn.getOutputStream());
            for(int i=0;i<param_num;i++)
            {
                if(i == param_num-1)
                {
                    request.append(URLEncoder.encode(name[i], "UTF-8") + "=" + URLEncoder.encode(value[i], "UTF-8"));
                }
                else
                {
                    request.append(URLEncoder.encode(name[i], "UTF-8") + "=" + URLEncoder.encode(value[i], "UTF-8") + "&");
                }

            }
            out.write(request.toString().getBytes());
            out.flush();
            out.close();

            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));

            while ((line = reader.readLine()) != null)
            {
                response.append(line);
            }

            reader.close();

            if(CASE.equals("LOGIN"))
            {
                ret = JsonParser(response.toString());
            }
            else
            {
                ret = response.toString();
            }

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            return ret;
        }
    }
    /************************************************************************************************************
     * Function : JsonParser
     * Definition : Parse json format to get useful info
     * Return : String json
     ************************************************************************************************************/
    public String JsonParser(String val)
    {
        String json ="";
        try
        {
            JSONArray object = (JSONArray)new JSONTokener(val).nextValue();
            json = object.getJSONObject(0).getString("user_Pwd");

        }
        catch(JSONException e)
        {
            e.printStackTrace();
        }
        finally
        {
            return json;
        }
    }

}

