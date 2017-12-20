package in.etaminepgg.sfa.Adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by etamine on 4/6/17.
 */

public class SkuGenreTabsAdapter extends FragmentPagerAdapter
{
    private List<Fragment> fragmentsList = new ArrayList<>();
    private List<CharSequence> tabTitles = new ArrayList<>();

    public SkuGenreTabsAdapter(FragmentManager fm)
    {
        super(fm);
    }

    @Override
    public CharSequence getPageTitle(int position)
    {
        return tabTitles.get(position);
    }

    @Override
    public Fragment getItem(int position)
    {
        return fragmentsList.get(position);
    }

    @Override
    public int getCount()
    {
        return fragmentsList.size();
    }

    public void addFragment(Fragment fragment, CharSequence tabTitle)
    {
        fragmentsList.add(fragment);
        tabTitles.add(tabTitle);
    }
}
