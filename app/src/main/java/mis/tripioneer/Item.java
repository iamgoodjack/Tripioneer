package mis.tripioneer;

/**
 * Created by user on 2015/10/29.
 */
public class Item
{
    private long keyid;
    private String tripid;
    private String spotid;
    private String spotname;
    private String title;
    private int spotorder;
    private String spottime;
    private String spotpic;
    private int ttltime;
    private String date;

    public Item(){}

    public Item(String tripid, String spotid, String spotname, String title, int spotorder, String spottime, String spotpic, int ttltime, String date)
    {
        this.tripid = tripid;
        this.spotid = spotid;
        this.spotname = spotname;
        this.title = title;
        this.spotorder = spotorder;
        this.spottime = spottime;
        this.spotpic = spotpic;
        this.ttltime = ttltime;
        this.date = date;
    }

    public void setID(long id)
    {
        keyid = id;
    }

    public long getID()
    {
        return keyid;
    }

    public void setTripid(String tripid)
    {
        this.tripid = tripid;
    }

    public String getTripid()
    {
        return tripid;
    }

    public void setSpotid(String spotid)
    {
        this.spotid = spotid;
    }

    public String getSpotid()
    {
        return spotid;
    }

    public void setSpotname(String spotname)
    {
        this.spotname = spotname;
    }

    public String getSpotname()
    {
        return spotname;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public String getTitle()
    {
        return title;
    }

    public void setSpotorder(int spotorder)
    {
        this.spotorder = spotorder;
    }

    public int getSpotorder()
    {
        return spotorder;
    }

    public void setSpottime(String spottime)
    {
        this.spottime = spottime;
    }

    public String getSpottime()
    {
        return spottime;
    }

    public void setSpotpic(String spotpic)
    {
        this.spotpic = spotpic;
    }

    public String getSpotpic()
    {
        return spotpic;
    }

    public void setTtltime(int ttltime)
    {
        this.ttltime = ttltime;
    }

    public int getTtltime()
    {
        return ttltime;
    }

    public void setDate(String date)
    {
        this.date = date;
    }

    public String getDate()
    {
        return date;
    }
}
