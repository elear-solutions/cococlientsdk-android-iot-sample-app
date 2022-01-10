package buzz.getcoco.iot.sample.activities;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.MutableLiveData;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.widget.Toast;
import buzz.getcoco.iot.sample.databinding.ActivitySplashBinding;
import java.io.File;
import java.util.Objects;
import buzz.getcoco.auth.Constants;
import buzz.getcoco.auth.LoginActivity;
import buzz.getcoco.iot.CocoClient;
import buzz.getcoco.iot.PlatformInterface;
import buzz.getcoco.iot.android.CreatorEx;

public class SplashActivity extends AppCompatActivity {

  private static final String TAG = "SplashActivity";

  // querying tokens to CocoLoginActivity which starts a browser for login
  private final ActivityResultLauncher<Intent> resultLauncher =
      registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
        Intent dataIntent = result.getData();

        Objects.requireNonNull(dataIntent);

        Log.d(TAG, "data: " + dataIntent);

        if (RESULT_OK != result.getResultCode()) {
          Toast
              .makeText(this, dataIntent.getStringExtra(Constants.KEY_FAILURE), Toast.LENGTH_SHORT)
              .show();
          return;
        }

        if (dataIntent.hasExtra(Constants.KEY_AUTH_STATE)) {
          Log.d(TAG, "authState: " + dataIntent.getStringExtra(Constants.KEY_AUTH_STATE));
          CocoClient.getInstance().setTokens(dataIntent.getStringExtra(Constants.KEY_AUTH_STATE));
          startCocoNetActivity();
          return;
        }

        Log.d(TAG, "illegal state");
      });

  private final MutableLiveData<Pair<String, String>> authListener = new MutableLiveData<>();

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    ActivitySplashBinding binding = ActivitySplashBinding.inflate(getLayoutInflater());

    setContentView(binding.getRoot());

    init(getFilesDir(), authListener);
    authListener.observe(this, pair -> {
      Intent intent = new Intent(this, LoginActivity.class);

      intent.putExtra(Constants.AUTH_ENDPOINT, pair.first)
          .putExtra(Constants.TOKEN_ENDPOINT, pair.second)
          .putExtra(Constants.SCOPE, "network.mgmt");

      resultLauncher.launch(intent);
    });

    CocoClient.getInstance().getAccessTokens((accessToken, tr) -> {
      if (null != accessToken) {
        Log.d(TAG, "onCreate: already logged in");
        startCocoNetActivity();
        return;
      }

      Log.d(TAG, "onCreate: login failed", tr);
    });
  }

  private static void init(File dir, MutableLiveData<Pair<String, String>> authListener) {
    if (null != CocoClient.getInstance()) {
      return;
    }

    Log.d(TAG, "init: started");

    new CocoClient.Configurator()
        .withCreator(new CreatorEx())
        .withPlatform(new PlatformInterface() {
          @Override
          public String getCwdPath() {
            return dir.getAbsolutePath();
          }

          @Override
          public String getClientId() {
            return "your_client_id_here";
          }

          @Override
          public String getAppAccessList() {
            return "{\"appCapabilities\": [0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30]}";
          }

          @Override
          public void authCallback(String authorizationEndpoint, String tokenEndpoint) {
            authListener.postValue(new Pair<>(authorizationEndpoint, tokenEndpoint));
          }
        }).configure();
  }

  private void startCocoNetActivity() {
    startActivity(new Intent(this, CocoNetworksActivity.class));
    finish();
  }
}
