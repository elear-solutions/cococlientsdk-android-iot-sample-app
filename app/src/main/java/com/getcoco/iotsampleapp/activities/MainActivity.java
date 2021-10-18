package com.getcoco.iotsampleapp.activities;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.getcoco.iotsampleapp.adapters.ResourceInfoAdapter;
import com.getcoco.iotsampleapp.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());

    ResourceInfoAdapter adapter = new ResourceInfoAdapter();

    binding.rvResources.setAdapter(adapter);
    setContentView(binding.getRoot());
  }
}