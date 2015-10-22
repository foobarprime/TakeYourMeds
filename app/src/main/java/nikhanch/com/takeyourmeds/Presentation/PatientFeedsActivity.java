package nikhanch.com.takeyourmeds.Presentation;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import mehdi.sakout.fancybuttons.FancyButton;
import nikhanch.com.takeyourmeds.Presentation.View.SlidingTabLayout;
import nikhanch.com.takeyourmeds.R;

public class PatientFeedsActivity extends AppCompatActivity {


    private SlidingTabLayout mSlidingTabLayout;
    private ViewPager mViewPager;
    private Toolbar mToolbar;

    private List<FragmentPageData> mTabs = new ArrayList<FragmentPageData>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_feeds);

        mTabs.add(new FragmentPageData("Alerts", FeedFragmentBase.FeedFragmentType.FRAGMENT_TYPE_ALERT_LIST));
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

        mToolbar = (Toolbar) findViewById(R.id.patients_feed_toolbar);
        setSupportActionBar(mToolbar);

        mSlidingTabLayout.setViewPager(mViewPager);
        mSlidingTabLayout.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                mToolbar.setTitle(mTabs.get(position).getTitle());
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        getSupportActionBar().setTitle(mTabs.get(0).getTitle());
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_patient_feeds, menu);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void showAddDialog(MenuItem item){
        startActivityForResult(new Intent(getApplicationContext(), AddItemActivity.class), 0);
    }

    public void showShareDialog(MenuItem item){
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_share);

        FancyButton shareButton = (FancyButton) dialog.findViewById(R.id.share_button);
        shareButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        EditText email = (EditText) v.findViewById(R.id.emailToShareWith);
                        Toast.makeText(getApplicationContext(), "Sharing with " + email, Toast.LENGTH_SHORT).show();
                    }
                });
        dialog.show();
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
