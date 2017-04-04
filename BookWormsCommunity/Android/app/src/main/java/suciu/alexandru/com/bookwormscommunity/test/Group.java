package suciu.alexandru.com.bookwormscommunity.test;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Alexandru on 13.06.2016.
 */
public class Group {

    public String string;
    public final List<String> children = new ArrayList<String>();

    public Group(String string) {
        this.string = string;
    }

}