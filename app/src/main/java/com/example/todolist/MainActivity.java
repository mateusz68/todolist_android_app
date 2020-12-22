package com.example.todolist;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout drawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,drawer,toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        if(savedInstanceState == null)
        {
            TaskListFragment taskListFragment = new TaskListFragment();
            Bundle bundle = new Bundle();
            bundle.putString(TaskListFragment.ARG_PARAM1, "0");
            taskListFragment.setArguments(bundle);
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_cointainer,
                    taskListFragment).commit();
            navigationView.setCheckedItem(R.id.nav_today_task);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        TaskListFragment taskListFragment;
        Bundle bundle;
        switch (item.getItemId()){
            case R.id.nav_today_task:
                taskListFragment = new TaskListFragment();
                bundle = new Bundle();
                bundle.putString(TaskListFragment.ARG_PARAM1, "0");
                taskListFragment.setArguments(bundle);
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_cointainer,
                        taskListFragment).commit();
                break;
            case R.id.nav_future_task:
                taskListFragment = new TaskListFragment();
                bundle = new Bundle();
                bundle.putString(TaskListFragment.ARG_PARAM1, "1");
                taskListFragment.setArguments(bundle);
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_cointainer,
                        taskListFragment).commit();
                break;
            case R.id.nav_archive_task:
                taskListFragment = new TaskListFragment();
                bundle = new Bundle();
                bundle.putString(TaskListFragment.ARG_PARAM1, "2");
                taskListFragment.setArguments(bundle);
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_cointainer,
                        taskListFragment).commit();
                break;
            case R.id.nav_note_list:
                NotesListFragment notesListFragment = new NotesListFragment();
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_cointainer,
                        notesListFragment).commit();
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;

    }

    @Override
    public void onBackPressed() {
        if(drawer.isDrawerOpen(GravityCompat.START)){
            drawer.closeDrawer(GravityCompat.START);
        }else {
            super.onBackPressed();
        }

    }
}