package nikhanch.com.takeyourmeds.Presentation;

import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import nikhanch.com.takeyourmeds.Presentation.View.SlidingTabLayout;
import nikhanch.com.takeyourmeds.R;

public class PatientFeedsActivity extends FragmentActivity {


    private SlidingTabLayout mSlidingTabLayout;
    private ViewPager mViewPager;

    private List<FragmentPageData> mTabs = new ArrayList<FragmentPageData>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_feeds);

        mTabs.add(new FragmentPageData("Appointments", FeedFragmentBase.FeedFragmentType.FRAGMENT_TYPE_APPOINTMENT_LIST));
        mTabs.add(new FragmentPageData("Medications", FeedFragmentBase.FeedFragmentType.FRAGMENT_TYPE_MEDICATION_LIST));

        mViewPager = (ViewPager) findViewById(R.id.viewpager);
        mViewPager.setAdapter(new SamplePagerAdapter(getSupportFragmentManager()));

        mSlidingTabLayout = (SlidingTabLayout) findViewById(R.id.sliding_tabs);
        mSlidingTabLayout.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
            @Override
            public int getIndicatorColor(int position) {
                return Color.WHITE;
            }

            @Override
            public int getDividerColor(int position) {
                return Color.WHITE;
            }
        });
        mSlidingTabLayout.setViewPager(mViewPager);
    }


    static class FragmentPageData{
        private String mTitle;
        private FeedFragmentBase.FeedFragmentType mType;
        FragmentPageData(String title, FeedFragmentBase.FeedFragmentType type){
            this.mTitle = title;
            this.mType = type;
        }

        Fragment createFragment(){
            return FeedFragmentBase.newInstance(this.mType);
        }

        String getTitle(){
            return this.mTitle;
        }

    }

    class SamplePagerAdapter extends FragmentPagerAdapter {
        SamplePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int i) {
            return mTabs.get(i).createFragment();
        }

        @Override
        public int getCount() {
            return mTabs.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mTabs.get(position).getTitle();
        }

    }
}
