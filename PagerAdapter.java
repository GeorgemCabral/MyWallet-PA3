package Dir.dev.mywallet.fragment;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;


public class PagerAdapter extends FragmentPagerAdapter {

    ArrayList<Fragment> fragments = new ArrayList<>();

    public PagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }
    //add page tab
    public void addFragment(Fragment f){
        fragments.add(f);
    }

    //add title page tab

    @Override
    public CharSequence getPageTitle(int position) {
        String title = fragments.get(position).toString();
        return title.toString();
    }
}
