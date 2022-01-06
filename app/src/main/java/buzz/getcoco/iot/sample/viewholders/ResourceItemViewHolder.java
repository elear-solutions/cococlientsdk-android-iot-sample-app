package buzz.getcoco.iot.sample.viewholders;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.recyclerview.widget.RecyclerView;
import buzz.getcoco.iot.Capability;
import buzz.getcoco.iot.CapabilityOnOff;
import buzz.getcoco.iot.CapabilityTemperatureSensing;
import buzz.getcoco.iot.Command;
import buzz.getcoco.iot.android.AttributeEx;
import buzz.getcoco.iot.android.ResourceEx;
import buzz.getcoco.iot.sample.R;
import buzz.getcoco.iot.sample.databinding.RecyclerItemResourceBinding;

public class ResourceItemViewHolder extends RecyclerView.ViewHolder {
  private static final String TAG = "ResourceItemVH";

  private final MutableLiveData<ResourceEx> currentResourceObservable = new MutableLiveData<>();

  private ResourceEx resource;

  public ResourceItemViewHolder(@NonNull RecyclerItemResourceBinding binding, @NonNull LifecycleOwner lifecycleOwner) {
    super(binding.getRoot());

    Transformations
        .switchMap(currentResourceObservable, ResourceEx::getNameObservable)
        .observe(lifecycleOwner, binding.tvResourceName::setText);

    Transformations
        .switchMap(currentResourceObservable, resource -> {

          AttributeEx onOffAttr = resource.getAttribute(CapabilityOnOff.AttributeId.ON_FLAG);
          return (null == onOffAttr) ? new MutableLiveData<>(null) : onOffAttr.getCurrentValueObservable();
        }).observe(lifecycleOwner, currentValue -> {

      if (currentValue instanceof Boolean) {
        setControlValues(binding, (Boolean) currentValue);
      }

      int visibility = currentValue instanceof Boolean ? View.VISIBLE : View.GONE;

      binding.tvPowerDescription.setVisibility(visibility);
      binding.tvPowerValue.setVisibility(visibility);
      binding.btOnOff.setVisibility(visibility);
    });

    Transformations
        .switchMap(currentResourceObservable, resource -> {

          AttributeEx tempAttr = resource.getAttribute(CapabilityTemperatureSensing.AttributeId.CURRENT_TEMP_CELSIUS);
          return (null == tempAttr) ? new MutableLiveData<>(null) : tempAttr.getCurrentValueObservable();
        }).observe(lifecycleOwner, currentValue -> {

      if (currentValue instanceof Number) {
        setTemperatureValues(binding, (double) currentValue);
      }

      int visibility = currentValue instanceof Number ? View.VISIBLE : View.GONE;

      binding.tvTempDescription.setVisibility(visibility);
      binding.tvTempValue.setVisibility(visibility);
    });

    binding.btOnOff.setOnClickListener(v -> {
      CapabilityOnOff capabilityOnOff = resource.getCapability(Capability.CapabilityId.ON_OFF_CONTROL);

      if (null == capabilityOnOff) {
        return;
      }

      Command<CapabilityOnOff.CommandId> command = binding.btOnOff.isChecked() ? new CapabilityOnOff.On() : new CapabilityOnOff.Off();

      capabilityOnOff.sendResourceCommand(command, (commandResponse, tr) -> {
        Log.d(TAG, "ResourceItemViewHolder: response: " + commandResponse, tr);

        new Handler(Looper.getMainLooper()).post(() -> Toast
            .makeText(binding.getRoot().getContext(), (Command.State.SUCCESS == commandResponse.getState()) ? "Command Success" : "Command Failed", Toast.LENGTH_SHORT)
            .show());
      });
    });
  }

  public void setResource(ResourceEx resource) {
    currentResourceObservable.postValue(this.resource = resource);
  }

  private static void setTemperatureValues(RecyclerItemResourceBinding binding, Double temperature) {
    binding.tvTempDescription.setText(R.string.temperature);
    binding.tvTempValue.setText(binding.getRoot().getContext().getString(R.string.num_c, temperature.intValue()));
  }

  private static void setControlValues(RecyclerItemResourceBinding binding, boolean onFlag) {
    binding.tvPowerDescription.setText(R.string.power_supply);
    binding.tvPowerValue.setText(onFlag ? R.string.on : R.string.off);
    binding.btOnOff.setChecked(onFlag);
  }
}
