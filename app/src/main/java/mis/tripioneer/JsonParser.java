package mis.tripioneer;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONTokener;

/************************************************************************************************************
 * File : JsonParser
 * Function: Parse json format to get useful info
 * Return : String json
 ************************************************************************************************************/
public class JsonParser
{
    private int array_length = 0;   //
    private String key ="";
    private String CASE ="";

    public JsonParser(int param_num, String scenario)
    {
        array_length = param_num;
        CASE = scenario;

    }


    public String[] Parse(String val, String match)

    {
        String json[] = new String[array_length];
        JSONArray object;
        key = match;

        try
        {
            switch (CASE)
            {
                case "LOGIN":
                    object = (JSONArray)new JSONTokener(val).nextValue();

                    for(int i=0;i<object.length();i++)
                    {
                        json[i] = object.getJSONObject(i).getString(key);
                    }

                    break;
                case "RECOMMENDATION":
                    object = (JSONArray)new JSONTokener(val).nextValue();

                    for(int i=0;i<object.length();i++)
                    {
                        json[i] = object.getJSONObject(i).getString(key);
                    }
                    break;
                case "TRIP":
                    object = (JSONArray)new JSONTokener(val).nextValue();

                    for(int i=0;i<object.length();i++)
                    {
                        json[i] = object.getJSONObject(i).getString(key);
                        Log.d("trip", Integer.toString(object.length()));
                    }
                    break;
                case "PLACE":
                    object = new JSONArray(val);
                    for(int i=0;i<object.length();i++)
                    {
                        json[i] = object.getString(i);
                    }
                default:
                    break;
            }

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
