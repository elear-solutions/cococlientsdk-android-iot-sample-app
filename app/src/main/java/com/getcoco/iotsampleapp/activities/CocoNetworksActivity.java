package com.getcoco.iotsampleapp.activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.getcoco.iotsampleapp.Globals;
import com.getcoco.iotsampleapp.adapters.NetworkListAdapter;
import com.getcoco.iotsampleapp.databinding.ActivityCoconetworksBinding;

import java.util.ArrayList;

import buzz.getcoco.api.CocoClient;
import buzz.getcoco.api.android.Identifier;

public class CocoNetworksActivity extends AppCompatActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    ActivityCoconetworksBinding binding = ActivityCoconetworksBinding.inflate(getLayoutInflater());

    NetworkListAdapter adapter = new NetworkListAdapter(this, network -> {
        network.connect();

        startActivity(
                new Intent(CocoNetworksActivity.this, MainActivity.class)
                        .setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        .putExtra(Globals.IDENTIFIER, Identifier.getIdentifier(network)));

        finish();
    });

    binding.rvNetworks.setAdapter(adapter);

    binding.swipeToRefresh.setOnRefreshListener(() -> CocoClient
            .getInstance()
            .getAllNetworks((networkList, throwable) -> {
              if (null == networkList)
                return;

              adapter.setItemList(new ArrayList<>(Globals.downCast(networkList)));
            }));

    setContentView(binding.getRoot());
  }
}
