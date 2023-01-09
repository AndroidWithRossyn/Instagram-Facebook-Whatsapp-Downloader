package com.allmy.allstatusdownloader.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.allmy.allstatusdownloader.Fragment.FeedFragment;
import com.allmy.allstatusdownloader.Fragment.IgtvFragment;
import com.allmy.allstatusdownloader.Fragment.StoryFragment;
import com.allmy.allstatusdownloader.Model.FeedOnBackPressed;
import com.allmy.allstatusdownloader.Model.IOnBackPressed;
import com.allmy.allstatusdownloader.Model.IgtvOnBackPressed;
import com.allmy.allstatusdownloader.R;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class ShowAllData extends AppCompatActivity {


    public static String USER_ID;
    public static String USER_NAME;
    private FrameLayout adContainerView;
    ImageView back_arroe1;
    FeedOnBackPressed feedOnBackPressed;
    boolean feedback = false;
    IOnBackPressed iOnBackPressed;
    IgtvOnBackPressed igtvOnBackPressed;
    boolean igtvback = false;
    int position;
    String profile_pic;
    StoryFragment storyFragment;
    boolean storyback = true;
    TabItem tabFeed;
    TabItem tabIgtv;
    TabLayout tabLayout;
    TabItem tabStory;
    public static String user_FULLNAME;
    TabLayout sliding_tabs;
    ViewPager viewPager;
    View feedView, storyView, IgtvView;
    LinearLayout ltab_select_feed, ltab_select_story, ltab_select_igtv, lTextTool;
    TextView tabText_feed, tabText_story, tabText_igtv;
    private FrameLayout adViewContainer;
    CircleImageView imgProfile;
    String keyPosition = "position";
    String keyUSER_ID = "USER_ID";
    String keyUSER_NAME = "USER_NAME";
    String keyUSER_FULLNAME = "USER_FULLNAME";
    String keyPROFILE_PIC = "PROFILE_PIC";
    static ArrayList<Fragment> mFragmentList = new ArrayList<>();
    AppBarLayout mAppBar;
    private static final float SCALE_MINIMUM = 0.5f;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_all_data);

        getIntentValues();

        imgProfile = findViewById(R.id.profileImage);
        TextView txt_toolbar = findViewById(R.id.txt_toolbar);
        TextView txt_toolsub = findViewById(R.id.txt_toolsub);
        back_arroe1 = findViewById(R.id.back_arroe1);

        txt_toolbar.setText(user_FULLNAME);
        String sb = "@" + USER_NAME;
        txt_toolsub.setText(sb);

        settabs();

        Glide.with(this)
                .load(profile_pic)
                .placeholder(R.drawable.applogo)
                .into(imgProfile);
    }

    private void settabs() {

        feedView = LayoutInflater.from(this).inflate(R.layout.feed_layout_tab, (ViewGroup) null);
        storyView = LayoutInflater.from(this).inflate(R.layout.story_layout_tab, (ViewGroup) null);
        IgtvView = LayoutInflater.from(this).inflate(R.layout.igtv_layout_tab, (ViewGroup) null);

        ltab_select_feed = feedView.findViewById(R.id.ltab_select_feed);
        tabText_feed = feedView.findViewById(R.id.tabText_feed);
        tabText_feed.setText("Feed");
        tabText_feed.setTextColor(getResources().getColor(R.color.white));

        ltab_select_story = storyView.findViewById(R.id.ltab_select_story);
        tabText_story = storyView.findViewById(R.id.tabTextStory);
        ltab_select_story.setBackground(null);
        tabText_story.setText("Story");
        tabText_story.setTextColor(getResources().getColor(R.color.unselText));

        ltab_select_igtv = IgtvView.findViewById(R.id.ltab_select_igtv);
        tabText_igtv = IgtvView.findViewById(R.id.tabTextIGTV);
        ltab_select_igtv.setBackground(null);
        tabText_igtv.setText("IGTV");
        tabText_igtv.setTextColor(getResources().getColor(R.color.unselText));

        sliding_tabs = findViewById(R.id.sliding_tabs);

        sliding_tabs.addTab(sliding_tabs.newTab().setCustomView(this.feedView));
        sliding_tabs.addTab(sliding_tabs.newTab().setCustomView(this.storyView));
        sliding_tabs.addTab(sliding_tabs.newTab().setCustomView(this.IgtvView));

        sliding_tabs.setTabGravity(0);

        Bundle bundle = new Bundle();
        bundle.putInt(keyPosition, position);
        bundle.putString(keyUSER_ID, USER_ID);
        bundle.putString(keyUSER_NAME, USER_NAME);
        bundle.putString(keyUSER_FULLNAME, user_FULLNAME);
        bundle.putString(keyPROFILE_PIC, profile_pic);

        Fragment feedFragment = new FeedFragment();
        feedFragment.setArguments(bundle);
        Fragment storyFragment = new StoryFragment();
        storyFragment.setArguments(bundle);
        Fragment igtvFragment = new IgtvFragment();
        igtvFragment.setArguments(bundle);

        mFragmentList.add(feedFragment);
        mFragmentList.add(storyFragment);
        mFragmentList.add(igtvFragment);

        viewPager = findViewById(R.id.viewPager);
        viewPager.setAdapter(new ViewPagerAdapter(getSupportFragmentManager(), sliding_tabs.getTabCount()));
        viewPager.setCurrentItem(0);

        back_arroe1.setOnClickListener(view -> {
            if (storyback) {
                iOnBackPressed.onBackPressed();
            } else if (feedback) {
                feedOnBackPressed.onBackPressedfeed();
            } else if (igtvback) {
                igtvOnBackPressed.onBackPressedtgtv();
            } else {
                finish();
            }
        });

        viewPager.setOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(sliding_tabs));
        sliding_tabs.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            public void onTabReselected(TabLayout.Tab tab) {

            }

            public void onTabUnselected(TabLayout.Tab tab) {
//
            }

            public void onTabSelected(TabLayout.Tab tab) {

                viewPager.setCurrentItem(tab.getPosition());
                String str = "tab.getPosition : ";
                if (tab.getPosition() == 1) {
                    feedback = false;
                    storyback = true;
                    igtvback = false;

                    if (iOnBackPressed != null) {
                        iOnBackPressed.registterBReciver();
                    }

                } else if (tab.getPosition() == 2) {
                    feedback = false;
                    storyback = false;
                    igtvback = true;
                } else {
                    feedback = true;
                    storyback = false;
                    igtvback = false;
                    if (feedOnBackPressed != null) {
                        iOnBackPressed.registterBReciver();
                    }

                }
                if (tab.getPosition() == 0) {
                    tabText_feed.setTextColor(getResources().getColor(R.color.white));

                    ltab_select_story.setBackground(null);
                    tabText_story.setTextColor(getResources().getColor(R.color.unselText));

                    ltab_select_igtv.setBackground(null);
                    tabText_igtv.setTextColor(getResources().getColor(R.color.unselText));
                } else if (tab.getPosition() == 1) {
                    ltab_select_feed.setBackground(null);
                    tabText_feed.setTextColor(getResources().getColor(R.color.unselText));

                    tabText_story.setTextColor(getResources().getColor(R.color.white));

                    ltab_select_igtv.setBackground(null);
                    tabText_igtv.setTextColor(getResources().getColor(R.color.unselText));
                } else if (tab.getPosition() == 2) {
                    tabText_igtv.setTextColor(getResources().getColor(R.color.white));

                    ltab_select_story.setBackground(null);
                    tabText_story.setTextColor(getResources().getColor(R.color.unselText));

                    ltab_select_feed.setBackground(null);
                    tabText_feed.setTextColor(getResources().getColor(R.color.unselText));
                }
            }
        });
    }


    static class ViewPagerAdapter extends FragmentPagerAdapter {
        private final int num;

        public ViewPagerAdapter(FragmentManager fragmentManager, int i) {
            super(fragmentManager);
            this.num = i;
        }

        @NotNull
        public Fragment getItem(int i) {
            return mFragmentList.get(i);
        }

        public int getCount() {
            return num;
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    public void onPause() {
        super.onPause();
    }

    public void onDestroy() {
        super.onDestroy();
    }

    private void getIntentValues() {

        position = getIntent().getIntExtra(keyPosition, 0);
        USER_ID = getIntent().getStringExtra(keyUSER_ID);
        USER_NAME = getIntent().getStringExtra(keyUSER_NAME);
        user_FULLNAME = getIntent().getStringExtra(keyUSER_FULLNAME);
        profile_pic = getIntent().getStringExtra(keyPROFILE_PIC);
    }

    public void setFeedOnBackPressedListener(FeedOnBackPressed feedOnBackPressed2) {
        feedOnBackPressed = feedOnBackPressed2;
    }

    public void setStoryOnBackPressedListener(IOnBackPressed iOnBackPressed2) {
        iOnBackPressed = iOnBackPressed2;
    }

    public void setIgtvOnBackPressedListener(IgtvOnBackPressed igtvOnBackPressed2) {
        igtvOnBackPressed = igtvOnBackPressed2;
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}