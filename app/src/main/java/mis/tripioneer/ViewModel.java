package mis.tripioneer;

/**
 * Created by user on 2015/7/16.
 */
public class ViewModel
{
    private String text;
    private String imageUrl;

    public ViewModel(String text, String imageUrl) {
        this.text = text;
        this.imageUrl = imageUrl;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
