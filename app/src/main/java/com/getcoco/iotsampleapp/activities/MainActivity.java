package com.getcoco.iotsampleapp.activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.getcoco.iotsampleapp.Globals;
import com.getcoco.iotsampleapp.adapters.ResourceTileAdapter;
import com.getcoco.iotsampleapp.databinding.ActivityMainBinding;

import buzz.getcoco.api.android.Identifier;

public class MainActivity extends AppCompatActivity {
  private ResourceTileAdapter tileAdapter;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());

    Identifier identifier = getIntent().getParcelableExtra(Globals.IDENTIFIER);

    tileAdapter = new ResourceTileAdapter(identifier.getNetwork(), this);

    binding.rvResources.setAdapter(tileAdapter);
    setContentView(binding.getRoot());
  }

  @Override
  protected void onNewIntent(Intent intent) {
    super.onNewIntent(intent);

    Identifier identifier = getIntent().getParcelableExtra(Globals.IDENTIFIER);

    tileAdapter.setNetwork(identifier.getNetwork());
  }
}
