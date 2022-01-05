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

    currentResourceObservable.observe(lifecycleOwner, resourceEx -> bind(resourceEx, binding, lifecycleOwner));

    binding.btOnOff.setOnClickListener(v -> {
      CapabilityOnOff capabilityOnOff = resource.getCapability(Capability.CapabilityId.ON_OFF_CONTROL);

      if (null == capabilityOnOff) {
        return;
      }

      Command<CapabilityOnOff.CommandId> command = binding.btOnOff.isChecked() ? new CapabilityOnOff.On() : new CapabilityOnOff.Off();

      capabilityOnOff.sendResourceCommand(command, (commandResponse, tr) -> {
        Log.d(TAG, "ResourceItemViewHolder: response: " + commandResponse, tr);

        Handler handler = new Handler(Looper.getMainLooper());
        if (null != tr) {
          handler.post(() -> bind(resource, binding, lifecycleOwner));
          return;
        }

        handler.post(() -> Toast
            .makeText(binding.getRoot().getContext(), (Command.State.SUCCESS == commandResponse.getState()) ? "Command Success" : "Command Failed", Toast.LENGTH_SHORT)
            .show());
      });
    });
  }

  public void setResource(ResourceEx resource) {
    currentResourceObservable.postValue(this.resource = resource);
  }

  private void bind(ResourceEx resource, RecyclerItemResourceBinding binding, LifecycleOwner owner) {
    AttributeEx temperatureAttr, onOffAttr;

    if (isControlResource(resource) &&
        null != (onOffAttr = resource.getAttribute(CapabilityOnOff.AttributeId.ON_FLAG))) {

      onOffAttr.getCurrentValueObservable().observe(owner, currentValue -> {
        if (currentValue instanceof Boolean) {
          setControlValues(binding, (Boolean) currentValue);
        }

        int visibility = currentValue instanceof Boolean ? View.VISIBLE : View.GONE;

        binding.tvPowerDescription.setVisibility(visibility);
        binding.tvPowerValue.setVisibility(visibility);
        binding.btOnOff.setVisibility(visibility);
      });
    } else {
      binding.tvPowerDescription.setVisibility(View.GONE);
      binding.tvPowerValue.setVisibility(View.GONE);
      binding.btOnOff.setVisibility(View.GONE);
    }

    if (isTemperatureResource(resource)
        && (null != (temperatureAttr = resource.getAttribute(CapabilityTemperatureSensing.AttributeId.CURRENT_TEMP_CELSIUS)))) {

      temperatureAttr.getCurrentValueObservable().observe(owner, currentValue -> {
        if (currentValue instanceof Double) {
          setTemperatureValues(binding, (Double) currentValue);
        }

        int visibility = currentValue instanceof Double ? View.VISIBLE : View.GONE;

        binding.tvTempDescription.setVisibility(visibility);
        binding.tvTempValue.setVisibility(visibility);
      });
    } else {
      binding.tvTempDescription.setVisibility(View.GONE);
      binding.tvTempValue.setVisibility(View.GONE);
    }
  }

  private static boolean isControlResource(ResourceEx resource) {
    return null != resource.getCapability(Capability.CapabilityId.ON_OFF_CONTROL);
  }

  private static boolean isTemperatureResource(ResourceEx resource) {
    return null != resource.getCapability(Capability.CapabilityId.TEMPERATURE_MEASUREMENT);
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
