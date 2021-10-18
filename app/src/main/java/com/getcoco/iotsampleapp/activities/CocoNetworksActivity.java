package com.getcoco.iotsampleapp.activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import com.getcoco.iotsampleapp.adapters.NetworkListAdapter;
import com.getcoco.iotsampleapp.databinding.ActivityCoconetworksBinding;

public class CocoNetworksActivity extends AppCompatActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    ActivityCoconetworksBinding binding = ActivityCoconetworksBinding.inflate(getLayoutInflater());

    NetworkListAdapter adapter = new NetworkListAdapter();
    binding.rvNetworks.setAdapter(adapter);
    binding.btnConnect.setOnClickListener(v -> {

    });

    setContentView(binding.getRoot());
  }
}
