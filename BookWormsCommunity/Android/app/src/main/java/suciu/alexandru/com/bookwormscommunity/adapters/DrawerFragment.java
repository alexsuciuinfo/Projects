package suciu.alexandru.com.bookwormscommunity.adapters;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import suciu.alexandru.com.bookwormscommunity.R;

/**
 * Created by Alexandru on 13.05.2016.
 */
public class DrawerFragment extends Fragment {

    private ActionBarDrawerToggle actionBarDrawerToggle;
    private DrawerLayout drawerLayout;

    public DrawerFragment() {
        Log.d("DrawerFragment", "Eroare");
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_drawer, container, false);
    }

    public void initializeDrawer(int itemPosition, DrawerLayout drawerLayout, final Toolbar toolbar) {
        View view = getActivity().findViewById(itemPosition);
        this.drawerLayout = drawerLayout;
        this.actionBarDrawerToggle = new ActionBarDrawerToggle(getActivity(), drawerLayout, toolbar, R.string.Opened, R.string.Closed) {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                //super.onDrawerSlide(drawerView, slideOffset);
                if (slideOffset < .5) {
                    toolbar.setAlpha(1 - slideOffset/2);
                }
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }
        };

        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        drawerLayout.post(new Runnable() {
            @Override
            public void run() {
              actionBarDrawerToggle.syncState();
            }
        });
    }

    public void closeDrawer() {
        drawerLayout.closeDrawers();
    }

}
