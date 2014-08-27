package ir.treeco.aftabe;

import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.TranslateAnimation;
import android.widget.*;
import ir.treeco.aftabe.packages.PackageManager;
import ir.treeco.aftabe.utils.FontsHolder;
import ir.treeco.aftabe.utils.ImageManager;
import ir.treeco.aftabe.utils.LengthManager;

/**
 * Created by hamed on 8/12/14.
 */

class PackagesListScrollListener implements  AbsListView.OnScrollListener {

    private ListView packages;
    private View tabBar;

    PackagesListScrollListener(ListView packages, View tabBar) {
        this.packages = packages;
        this.tabBar = tabBar;
    }

    public void updateAdViewPadding() {

    }

    @Override
    public void onScrollStateChanged(AbsListView absListView, int i) {
        updateAdViewPadding();
    }

    @Override
    public void onScroll(AbsListView absListView, int i, int i2, int i3) {
        updateAdViewPadding();
        int barTop = 0;
        if (i == 0) {
            RelativeLayout relativeLayout = (RelativeLayout) packages.getChildAt(0);
            if (relativeLayout != null) {
                View innerView = relativeLayout.getChildAt(0);
                innerView.setPadding(0, - relativeLayout.getTop(), 0, 0);
                barTop = Math.max(relativeLayout.getTop() + innerView.getHeight(), barTop);
            }
        }
        try {
            tabBar.setTranslationY(barTop);
        } catch (java.lang.NoSuchMethodError ignore) {
            TranslateAnimation anim = new TranslateAnimation(0, 0, barTop, barTop);
            anim.setFillAfter(true);
            anim.setDuration(0);
            tabBar.startAnimation(anim);
        }
    }
}

public class PackageListFragment extends Fragment {
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final FrameLayout layout = (FrameLayout) inflater.inflate(R.layout.fragment_packages_list, container, false);
        final ListView packages =  (ListView) layout.findViewById(R.id.package_list);
        final View tabBar = layout.findViewById(R.id.tab_bar);
        tabBar.setBackground(new BitmapDrawable(getResources(), ImageManager.loadImageFromResource(inflater.getContext(), R.drawable.tabbar_background, LengthManager.getScreenWidth(), LengthManager.getTabBarHeight())));
        tabBar.setLayoutParams(new FrameLayout.LayoutParams(LengthManager.getScreenWidth(), LengthManager.getTabBarHeight()));

        Log.e("dude","brah");
        PackageManager pManager = new PackageManager(getActivity());
        try {
            pManager.refresh();
        } catch (Exception e) {
            Log.e("alak", "dolak");
            Log.d("suck", "can't refresh pManager");

//            e.printStackTrace();
        }
        Log.e("suck","muck");
        final PackageListAdapter adapter = new PackageListAdapter(getActivity(), pManager);
        packages.setAdapter(adapter);
        adapter.setFilter(0);
        adapter.notifyDataSetChanged();


        packages.setOnScrollListener(new PackagesListScrollListener(packages, tabBar));

        TextView[] textViews = new TextView[] {
                (TextView) layout.findViewById(R.id.tab_1),
                (TextView) layout.findViewById(R.id.tab_2),
                (TextView) layout.findViewById(R.id.tab_3)
        };

        for (TextView textView: textViews) {
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    adapter.setFilter(0);
                    packages.setSelection(0);
                }
            });
            textView.setTypeface(FontsHolder.getTabBarFont(layout.getContext()));
            textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, LengthManager.getScreenWidth() / 17);
            textView.setTranslationY(-LengthManager.getScreenWidth() / 80);
        }


        return layout;
    }
}
