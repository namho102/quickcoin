package example.requestsample;

import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;

public class M_MainActivity extends AppCompatActivity {
    private Fragment homeFg;
    private Fragment convertFg;
    private Fragment settingFg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_m__main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);
        homeFg = new MainActivity();
        convertFg = new ConvertActivity();

        DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.app_name, R.string.app_name);
        drawerLayout.setDrawerListener(drawerToggle);
        drawerToggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
//        navigationView.setNavigationItemSelectedListener(this);
        setUpNavigationView(navigationView);
        ((TextView)findViewById(R.id.title)).setText("Home");
        switchFragment(homeFg);



    }

    private void setUpNavigationView(NavigationView nav) {
        nav.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.frag_home:
                        ((TextView)findViewById(R.id.title)).setText("Home");
                        switchFragment(homeFg);
                        break;
                    case R.id.frag_convert:
                        ((TextView)findViewById(R.id.title)).setText("Convert");
                        switchFragment(convertFg);
                        break;


                    default:
                        break;
                }


                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                drawer.closeDrawer(GravityCompat.START);
                return true;
            }
        });
    }
    private void switchFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        FragmentTransaction transaction = fragmentManager.beginTransaction();

        transaction.replace(R.id.frContainer, fragment);
        transaction.commit();
    }
}
