package mis.tripioneer;

/**
 * Created by user on 2015/7/16.
 */
public class ViewModel
{
    private String title;
    private String imageUrl;
    private String info;
    private int type;
    private String id;

    public ViewModel(String text, String imageUrl, String info)
    {
        this.title = text;
        this.imageUrl = imageUrl;
        this.info = info;
    }

    //For recomm, TYPE 0 place ; 1 trip; 2 channel
    public void setType(int TYPE)
    {
        type = TYPE;
    }

    public void setID(String ID)
    {
        id = ID;
    }

    public String getID()
    {
        return id;
    }

    public int getType()
    {
        return type;
    }

    public String getTitle()
    {
        return title;
    }

    public String getInfo()
    {
        return info;
    }


    public String getImageUrl()
    {
        return imageUrl;
    }

}
