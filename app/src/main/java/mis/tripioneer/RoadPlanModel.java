package mis.tripioneer;

/**
 * Created by Jenny on 2015/8/20.
 */
public class RoadPlanModel
{
    private String title;
    private String img;
    private String info;


    public RoadPlanModel (String text, String imgurl,String info)
    {
        this.title = text;
        this.img=imgurl;
        this.info=info;
    }

    public String getTitle() { return title;}

    public String getImageUrl() { return img;}

    public String getInfo(){ return info;}

}
