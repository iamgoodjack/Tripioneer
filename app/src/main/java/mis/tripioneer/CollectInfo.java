package mis.tripioneer;

/**
 * Created by user on 2015/8/16.
 */
public class CollectInfo
{
    protected long _id;
    protected String name;
    protected String img;
    protected int sample_img;
    protected String date;
    protected int label;

    CollectInfo(long id,String name, String img, String date, int label)
    {
        this.name = name;
        this.img = img;
        this.date = date;
        this.label = label;
        this._id = id;
    }

    CollectInfo(String name, int img, String date, int label)
    {
        this.name = name;
        this.sample_img = img;
        this.date = date;
        this.label = label;
    }

    public long getID()
    {
        return _id;
    }
}
