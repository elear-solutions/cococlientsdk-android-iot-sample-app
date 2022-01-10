package buzz.getcoco.iot.sample.viewholders;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

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

    LiveData<List<AttributeEx>> attributeListObservable = Transformations.switchMap(currentResourceObservable, ResourceEx::getAttributeListObservable);

    Transformations
        .switchMap(attributeListObservable, attrList -> {

          AttributeEx onOffAttr = getAttribute(attrList, CapabilityOnOff.AttributeId.ON_FLAG);
          return (null == onOffAttr) ? new MutableLiveData<>(null) : onOffAttr.getCurrentValueObservable();
        })
        .observe(lifecycleOwner, currentValue -> {

          if (currentValue instanceof Boolean) {
            boolean onFlag = (boolean) currentValue;

            binding.tvPowerValue.setText(onFlag ? R.string.on : R.string.off);
            binding.btOnOff.setChecked(onFlag);
          }

          int visibility = currentValue instanceof Boolean ? View.VISIBLE : View.GONE;

          binding.tvPowerDescription.setVisibility(visibility);
          binding.tvPowerValue.setVisibility(visibility);
          binding.btOnOff.setVisibility(visibility);
        });

    Transformations
        .switchMap(attributeListObservable, attrList -> {

          AttributeEx tempAttr = getAttribute(attrList, CapabilityTemperatureSensing.AttributeId.CURRENT_TEMP_CELSIUS);
          return (null == tempAttr) ? new MutableLiveData<>(null) : tempAttr.getCurrentValueObservable();
        })
        .observe(lifecycleOwner, currentValue -> {

          if (currentValue instanceof Number) {
            binding.tvTempValue.setText(binding.getRoot().getContext().getString(R.string.num_c, ((Number)currentValue).intValue()));
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

  private static AttributeEx getAttribute(List<AttributeEx> attributes, Capability.AttributeId attributeId) {

    for (AttributeEx attribute: attributes) {
      if (attribute.getId().equals(attributeId)) {
        return attribute;
      }
    }
    return null;
  }
}
