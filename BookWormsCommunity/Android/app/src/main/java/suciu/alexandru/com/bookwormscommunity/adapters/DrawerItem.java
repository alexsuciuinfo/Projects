package suciu.alexandru.com.bookwormscommunity.adapters;

/**
 * Created by Alexandru on 13.05.2016.
 */
public class DrawerItem {
    private String title;
    private int itemPosition;

    public DrawerItem(String title, int itemPosition) {
        this.title = title;
        this.itemPosition = itemPosition;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getItemPosition() {
        return itemPosition;
    }

    public void setItemPosition(int itemPosition) {
        this.itemPosition = itemPosition;
    }
}
