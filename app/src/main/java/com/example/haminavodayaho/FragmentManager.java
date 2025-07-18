package com.example.haminavodayaho;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.example.haminavodayaho.WebSocketRequest._WebSocketForegroundService;
import com.example.haminavodayaho.WebSocketRequest._WebSocketService;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;
import java.util.List;

public class FragmentManager extends AppCompatActivity {
    private static final String PREFS_NAME = "theme_prefs";
    private static final String KEY_IS_DARK_MODE = "is_dark_mode";
    private TabLayout bottomTabLayout;
    private Toolbar topToolbar;
    private ViewPager2 viewPager2;
    AdapterBottomTab adapterBottomTab;
    static final String[] tabTitles = {
            "Home",
            "Chat",
            "Event",
            "Club",
            "Profile"
    };
    private static final int[] tabIcons = {
            R.drawable.ic_home,
            R.drawable.ic_chat,
            R.drawable.ic_event,
            R.drawable.ic_club,
            R.drawable.ic_profile
    };
    private static final int[] tabSelectedIcons = {
            R.drawable.ic_home_selected,
            R.drawable.ic_chat_selected,
            R.drawable.ic_event_selected,
            R.drawable.ic_club_selected,
            R.drawable.ic_profile_selecetd
    };
    private static final int PERMISSION_REQUEST_CODE = 123;
    @SuppressLint({"MissingInflatedId", "UseSupportActionBar"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_fragment_manager);

        requestAllPermissions();

        if (_WebSocketForegroundService.customWebSocket == null) {
            _WebSocketForegroundService.startService(this);
        }

        bottomTabLayout = findViewById(R.id.bottomTabLayout);
        viewPager2 = findViewById(R.id.viewPager2);
        topToolbar = findViewById(R.id.topToolbar);

        setSupportActionBar(topToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("HamiNavodayaHo");
        }

        topToolbar.setTitle("HamiNavodayaHo");
//        topToolbar.setSubtitle("pryog");

        adapterBottomTab = new AdapterBottomTab(this);
        viewPager2.setAdapter(adapterBottomTab);

        new TabLayoutMediator(bottomTabLayout, viewPager2, (tab, position) -> {
            setTabCustomView(tab, position, position == 0);
        }).attach();

        bottomTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();
                setTabCustomView(tab, position, true);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                int position = tab.getPosition();
                setTabCustomView(tab, position, false);
            }
            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
    }
    @SuppressLint({"MissingInflatedId", "LocalSuppress"})
    private void setTabCustomView(TabLayout.Tab tab, int position, boolean isSelected) {
        if (tab != null) {
            if (isSelected) {
                @SuppressLint("InflateParams") View tabView = LayoutInflater.from(this).inflate(R.layout.layout_bottom_tab, null);
                ImageView tabIcon = tabView.findViewById(R.id.tabIcon);
                TextView tabText = tabView.findViewById(R.id.tabText);
                tabIcon.setImageResource(tabSelectedIcons[position]);
                tabText.setText(tabTitles[position]);
                tab.setCustomView(tabView);
            } else {
                View tabView = tab.getCustomView();
                if (tabView != null) {
                    ImageView tabIcon = tabView.findViewById(R.id.tabIcon);
                    TextView tabText = tabView.findViewById(R.id.tabText);
                    tabIcon.setImageResource(tabIcons[position]);
                    tabText.setVisibility(View.GONE); // Hide text on unselect
                    tab.setCustomView(tabView);
                } else {
                    tab.setIcon(tabIcons[position]); // Fallback if custom view is null
                }
            }
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        new MenuInflater(this).inflate(R.menu.home_menu, menu);
        return  super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            openSettings();
        }else if (id == R.id.action_help) {
            openHelp();
        }else if (id == R.id.action_about) {
            openAbout();
        }else if (id == R.id.action_feedback) {
            openFeedback();
        } else if (id == R.id.action_logout) {
            logout();
        } else if (id == R.id.action_share) {
            share();
        } else if (id == R.id.action_theme) {
            changeTheme();
        } else {
            return super.onOptionsItemSelected(item);
        }
        return true;
    }

    // 📱 Open app settings
    private void openSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivity(intent);
    }
    private void openHelp(){
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.haminavodayaho.in/help"));
        startActivity(browserIntent);
    }
    private void openAbout() {
        Intent intent = new Intent(this, About.class);
        startActivity(intent);
    }
    private void openFeedback() {
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
        emailIntent.setData(Uri.parse("mailto:contact@haminavodayaho.in"));
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "App Feedback");
        emailIntent.putExtra(Intent.EXTRA_TEXT, "Hi, I want to share the following feedback...");
        try {
            startActivity(Intent.createChooser(emailIntent, "Send Feedback"));
        } catch (Exception e) {
            Log.e("error", "FragmentManager 182 Error: "+e);
        }
    }
    private void logout(){
        SharedPreferences sp = getSharedPreferences("login", MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean("user", false);
        editor.clear();
        editor.apply();
        Intent intent = new Intent(this, Login.class);
        startActivity(intent);
        finish();
    }
    private void share(){
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        String shareText = "🚀 Check out this awesome app I found! 🎉\n\n" +
                "📱 Download it now and make your life easier!\n\n" +
                "👉 https://play.google.com/store/apps/details?id=" + getPackageName() + "\n\n" +
                "👍 Don't forget to rate and share! 🌟";
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareText);
        startActivity(Intent.createChooser(shareIntent, "Share App Via"));
    }
    private void changeTheme(){
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        boolean isDark = prefs.getBoolean(KEY_IS_DARK_MODE, false);

        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(KEY_IS_DARK_MODE, !isDark);
        editor.apply();
        AppCompatDelegate.setDefaultNightMode(
                isDark ? AppCompatDelegate.MODE_NIGHT_NO : AppCompatDelegate.MODE_NIGHT_YES
        );
        recreate();
    }

    private void requestAllPermissions() {
        List<String> permissions = new ArrayList<>();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissions.add(Manifest.permission.POST_NOTIFICATIONS);
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.RECORD_AUDIO);
        }
//        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, REQUEST_MIC_PERMISSION);
//        }


        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.S) { // Android 12 and below
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE);
            }

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            }
        }

        if (!permissions.isEmpty()) {
            ActivityCompat.requestPermissions(this,
                    permissions.toArray(new String[0]), PERMISSION_REQUEST_CODE);
        }
    }
}