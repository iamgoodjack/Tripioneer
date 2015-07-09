package mis.tripioneer;

import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONTokener;

import java.util.ArrayList;
import java.util.List;

/************************************************************************************************************
 *  Name : ConnectServer.java
 *  Function : Connecting to URL by using a http post method
 *
 ************************************************************************************************************/
public class ConnectServer
{
    private String url="";
    public ConnectServer(String URL)
    {
        url = URL;
    }
    /************************************************************************************************************
     * Function : connect
     * Definition : Connecting to URL by using a http post method
     * Return : String ret from http response
     ************************************************************************************************************/
    public String connect(String request_name[], String request_value[], int param_num )
    {
        String name[]=request_name;
        String value[]=request_value;
        String ret="";

        try
        {

            HttpClient httpclient = new DefaultHttpClient();
            HttpPost method = new HttpPost(url);


            List<NameValuePair> vars=new ArrayList< NameValuePair>();

            for(int i=0;i< param_num;i++)
            {
                vars.add(new BasicNameValuePair(name[i],value[i]));
                Log.d("debug", "name["+i+"]=" + name[i]);
                Log.d("debug","value["+i+"]="+value[i]);
            }

            method.setEntity(new UrlEncodedFormEntity(vars, HTTP.UTF_8));


            HttpResponse response = httpclient.execute(method);
            HttpEntity entity = response.getEntity();

            if(entity != null)
            {
                ret = EntityUtils.toString(entity, "utf-8");
                Log.d("json",ret);
                ret = JsonParser(ret);
            }
            else
            {
                ret = null;
            }
        }
        catch(Exception e)
        {
            ret = "Connection Failed";
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
        String json="";
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

