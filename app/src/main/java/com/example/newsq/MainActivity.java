package com.example.newsq;

import android.os.Bundle;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import com.example.newsq.databinding.ActivityMainBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
    View view = binding.getRoot();
    setContentView(view);
    configureNavigation(binding);
  }

  /**
   * Configures the {@link BottomNavigationView} for the user interface.
   *
   * @param binding An instance of {@link ActivityMainBinding}.
   */
  private void configureNavigation(@NonNull ActivityMainBinding binding) {
    BottomNavigationView navView = binding.navView;
    AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(R.id.navigation_world,
        R.id.navigation_browse, R.id.navigation_search).build();
    NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
        .findFragmentById(R.id.nav_host_fragment);
    if (navHostFragment != null) {
      NavController navController = navHostFragment.getNavController();
      NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
      NavigationUI.setupWithNavController(navView, navController);
    }
  }
}