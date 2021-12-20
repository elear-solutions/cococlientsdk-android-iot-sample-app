package buzz.getcoco.iot.sample.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.MutableLiveData;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import buzz.getcoco.iot.Network;
import buzz.getcoco.iot.android.NetworkEx;
import buzz.getcoco.iot.sample.Globals;
import buzz.getcoco.iot.sample.adapters.NetworkListAdapter;
import buzz.getcoco.iot.sample.databinding.ActivityCoconetworksBinding;
import buzz.getcoco.iot.CocoClient;
import buzz.getcoco.iot.android.Identifier;
import java.util.ArrayList;
import java.util.List;

public class CocoNetworksActivity extends AppCompatActivity {

  private static final String TAG = "CocoNetworksActivity";

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    SwipeRefreshLayout.OnRefreshListener listener;
    ActivityCoconetworksBinding binding = ActivityCoconetworksBinding.inflate(getLayoutInflater());

    setContentView(binding.getRoot());

    MutableLiveData<List<NetworkEx>> networkListObservable = new MutableLiveData<>();

    NetworkListAdapter adapter = new NetworkListAdapter(this, network -> {
        network.connect();

        startActivity(
                new Intent(CocoNetworksActivity.this, MainActivity.class)
                        .setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        .putExtra(Globals.IDENTIFIER, Identifier.getIdentifier(network)));

        finish();
    });

    networkListObservable.observe(this, adapter::setItemList);

    binding.rvNetworks.setAdapter(adapter);

    binding.swipeToRefresh.setOnRefreshListener(listener = () -> {
      Log.d(TAG, "onCreate: refreshing");

      CocoClient
          .getInstance()
          .getAllNetworks((networkList, throwable) -> {
            if (null == networkList) {
              Log.d(TAG, "onCreate: no networks present");
              return;
            }

            Log.d(TAG, "onCreate: networks: " + networkList);
            List<NetworkEx> networkExes = new ArrayList<>();

            for (Network network : networkList) {
              if (!(network instanceof NetworkEx)) {
                Log.e(TAG, "onCreate: illegal state");
                continue;
              }

              networkExes.add((NetworkEx) network);
            }

            networkListObservable.postValue(networkExes);
          });

      binding.swipeToRefresh.setRefreshing(false);
    });

    binding.swipeToRefresh.setRefreshing(true);
    listener.onRefresh();
  }
}
