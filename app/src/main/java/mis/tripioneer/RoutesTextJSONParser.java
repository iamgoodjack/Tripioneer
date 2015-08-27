package mis.tripioneer;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Jenny on 2015/8/19.
 */
public class RoutesTextJSONParser
{
    private String key ="";

    public ArrayList<String> parse(JSONObject jObject,String match)
    {
        ArrayList<String> json = new ArrayList<String>();
        JSONArray jRoutes;
        JSONArray jLegs;
        JSONArray jSteps;
        Log.d("RoutesTextJSONParser","RoutesTextJSONParser");
        switch (match)
        {
            case "TEXT":
                try {
                    jRoutes = jObject.getJSONArray("routes");

                    /** Traversing all routes */
                    for (int i = 0; i < jRoutes.length(); i++)
                    {
                        jLegs = ((JSONObject) jRoutes.get(i)).getJSONArray("legs");

                        /** Traversing all legs */
                        for (int j = 0; j < jLegs.length(); j++)
                        {
                            jSteps = ((JSONObject) jLegs.get(j)).getJSONArray("steps");

                            /** Traversing all steps */
                            for (int k = 0; k < jSteps.length(); k++)
                            {
                                String t;
                                t = jSteps.getJSONObject(k).getString("html_instructions");
                                String noHTMLString = t.replaceAll("\\<.*?>", "");
                                json.add(noHTMLString);
                                Log.d("k is", String.valueOf(k));
                                Log.d("TEXT", noHTMLString);

                            }
                        }
                    }
                } catch (JSONException e)
                {
                    e.printStackTrace();
                }
                break;

            case "TRAVEL_MODE":
                try {
                    jRoutes = jObject.getJSONArray("routes");

                    /** Traversing all routes */
                    for (int i = 0; i < jRoutes.length(); i++)
                    {
                        jLegs = ((JSONObject) jRoutes.get(i)).getJSONArray("legs");

                        /** Traversing all legs */
                        for (int j = 0; j < jLegs.length(); j++)
                        {
                            jSteps = ((JSONObject) jLegs.get(j)).getJSONArray("steps");

                            /** Traversing all steps */
                            for (int k = 0; k < jSteps.length(); k++)
                            {
                                String t;
                                t = jSteps.getJSONObject(k).getString("travel_mode");
                                json.add(t);
                                Log.d("k is", String.valueOf(k));
                                Log.d("instruction", t);
                            }
                        }
                    }
                } catch (JSONException e)
                {
                    e.printStackTrace();
                }
                break;
            case "DISTANCE":
                try{
                    jRoutes = jObject.getJSONArray("routes");
                    for (int i = 0; i < jRoutes.length(); i++)
                    {
                        jLegs = ((JSONObject) jRoutes.get(i)).getJSONArray("legs");
                        for (int j = 0; j < jLegs.length(); j++)
                        {
                            String t = "";
                            t = ((JSONObject) jLegs.get(j)).getJSONObject("distance").getString("value");
                            json.add(t);
                            Log.d("j is", String.valueOf(j));
                            Log.d("distance", t);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case "DURATION":
                try{
                    jRoutes = jObject.getJSONArray("routes");
                    for (int i = 0; i < jRoutes.length(); i++)
                    {
                        jLegs = ((JSONObject) jRoutes.get(i)).getJSONArray("legs");
                        for (int j = 0; j < jLegs.length(); j++)
                        {
                            String t = "";
                            t = ((JSONObject) jLegs.get(j)).getJSONObject("duration").getString("value");
                            json.add(t);
                            Log.d("j is", String.valueOf(j));
                            Log.d("duration", t);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
        }
        Log.d("TEXT", "TEXT");
        return json;
    }
}
