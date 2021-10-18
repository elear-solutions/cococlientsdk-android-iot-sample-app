package com.getcoco.iotsampleapp.activities;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.getcoco.iotsampleapp.adapters.ResourceTileAdapter;
import com.getcoco.iotsampleapp.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());

    ResourceTileAdapter adapter = new ResourceTileAdapter();

    binding.rvResources.setAdapter(adapter);
    setContentView(binding.getRoot());
  }
}
