package buzz.getcoco.sample.activities;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import buzz.getcoco.iot.android.NetworkEx;
import buzz.getcoco.iot.android.ResourceEx;
import buzz.getcoco.iot.android.ZoneEx;
import buzz.getcoco.sample.Globals;
import buzz.getcoco.sample.adapters.ResourceTileAdapter;
import com.getcoco.sample.databinding.ActivityMainBinding;
import java.util.ArrayList;
import java.util.List;
import buzz.getcoco.iot.android.Identifier;

public class MainActivity extends AppCompatActivity {

  private static final String TAG = "MainActivity";
  ResourceTileAdapter tileAdapter;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
    setContentView(binding.getRoot());

    List<ResourceEx> resourceList = new ArrayList<>();
    MutableLiveData<List<ResourceEx>> resourcesObservable = new MutableLiveData<>();

    Identifier identifier = getIntent().getParcelableExtra(Globals.IDENTIFIER);

    NetworkEx network = identifier.getNetwork();

    // observing state of network
    network.getStateObservable().observe(this, state -> {
      String message = "Name: " + network.getName() + ", state: " + state;

      Log.d(TAG, "onCreate: " + message);
      MainActivity.this.runOnUiThread(() -> Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show());
    });

    // observer that adds resources from all zones to one list
    Observer<List<ResourceEx>> observer = resources -> {
      resourceList.removeAll(resources);
      resourceList.addAll(resources);
      resourcesObservable.postValue(resourceList);
    };

    network.getZoneListObservable().observe(this, zones -> {
      for (ZoneEx zone : zones) {
        zone.getResourcesObservable().removeObserver(observer);
        zone.getResourcesObservable().observe(MainActivity.this, observer);
      }
    });

    Log.d(TAG, "onCreate: resourceList" + resourceList);
    tileAdapter = new ResourceTileAdapter(resourcesObservable, this);

    binding.rvResources.setAdapter(tileAdapter);
  }
}
