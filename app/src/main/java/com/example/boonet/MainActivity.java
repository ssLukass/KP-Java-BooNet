package com.example.boonet;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.MenuItem;


import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;


import com.example.boonet.home.ui.HomeFragment;
import com.example.boonet.profile.ui.ProfileFragment;
import com.example.boonet.search.ui.SearchFragment;
import com.google.android.material.navigation.NavigationView;


public class MainActivity extends AppCompatActivity {


    private BottomNavigationView bottomNavigationView;
    public DrawerLayout drawerLayout;
    public NavigationView navigationView;
    public ActionBarDrawerToggle actionBarDrawerToggle;

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        navigationView = findViewById(R.id.nav_view);
        drawerLayout = findViewById(R.id.my_drawer_layout);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment fragment = null;


                if (item.getItemId() == R.id.page_home) {
                    fragment = new HomeFragment();
                    bottomNavigationView.setSelectedItemId(R.id.page_home);
                } else if (item.getItemId() == R.id.page_seatch) {
                    fragment = new SearchFragment();
                    bottomNavigationView.setSelectedItemId(R.id.page_seatch);
                } else if (item.getItemId() == R.id.page_profile) {
                    bottomNavigationView.setSelectedItemId(R.id.page_profile);
                    fragment = new ProfileFragment();
                }

                if (fragment != null) {
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.fragmentContainerView, fragment, null)
                            .commit();

                    drawerLayout.closeDrawers();

                    return true;
                }

                return true;
            }
        });



        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentContainerView, new HomeFragment(), null)
                .commit();

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment fragment;

                if (item.getItemId() == R.id.page_home) {
                    fragment = new HomeFragment();
                } else if (item.getItemId() == R.id.page_seatch) {
                    fragment = new SearchFragment();
                } else {
                    fragment = new ProfileFragment();
                }

                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragmentContainerView, fragment, null)
                        .commit();

                return true;
            }
        });
    }
}