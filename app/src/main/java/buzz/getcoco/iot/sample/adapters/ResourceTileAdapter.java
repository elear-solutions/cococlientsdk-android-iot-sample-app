package buzz.getcoco.iot.sample.adapters;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import java.util.ArrayList;
import java.util.List;

public class ResourceTileAdapter extends RecyclerView.Adapter<ResourceTileAdapter.ResourceItemViewHolder> {

  private static final String TAG = "ResourceTileAdapter";

  @NonNull
  private final LifecycleOwner owner;

  private List<ResourceEx> resourceList = new ArrayList<>();

  public ResourceTileAdapter(@NonNull MutableLiveData<List<ResourceEx>> resourcesObservable, @NonNull LifecycleOwner owner) {
    this.owner = owner;

    resourcesObservable.observe(owner, this::setResources);
  }

  private void setResources(List<ResourceEx> resources) {
    Log.d(TAG, "setResources: " + resources);

    this.resourceList = resources;
    notifyDataSetChanged();
  }

  @NonNull
  @Override
  public ResourceItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    RecyclerItemResourceBinding binding = RecyclerItemResourceBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);

    return new ResourceItemViewHolder(binding, owner);
  }

  @Override
  public void onBindViewHolder(@NonNull ResourceItemViewHolder holder, int position) {
    holder.currentResourceObservable.postValue(holder.resource = resourceList.get(position));
  }

  @Override
  public int getItemCount() {
    return resourceList.size();
  }

  protected static class ResourceItemViewHolder extends RecyclerView.ViewHolder {
    private static final String TAG = "ResourceItemVH";

    private final MutableLiveData<ResourceEx> currentResourceObservable = new MutableLiveData<>();

    private ResourceEx resource;

    public ResourceItemViewHolder(@NonNull RecyclerItemResourceBinding binding, @NonNull LifecycleOwner lifecycleOwner) {
      super(binding.getRoot());

      Transformations
          .switchMap(currentResourceObservable, ResourceEx::getNameObservable)
          .observe(lifecycleOwner, binding.tvResourceName::setText);

      currentResourceObservable.observe(lifecycleOwner, resourceEx -> bind(resourceEx, binding, lifecycleOwner));

      binding.btOnOff.setOnCheckedChangeListener((buttonView, isChecked) -> {
        CapabilityOnOff capabilityOnOff = resource.getCapability(Capability.CapabilityId.ON_OFF_CONTROL);

        if (null == capabilityOnOff) {
          return;
        }

        Command<CapabilityOnOff.CommandId> command = isChecked ? new CapabilityOnOff.On() : new CapabilityOnOff.Off();

        capabilityOnOff.sendResourceCommand(command, (commandResponse, tr) -> {
          Log.d(TAG, "ResourceItemViewHolder: response: " + commandResponse, tr);

          Handler handler = new Handler(Looper.getMainLooper());
          if (null != tr) {
            handler.post(() -> bind(resource, binding, lifecycleOwner));
            return;
          }

          String message = (Command.State.SUCCESS == commandResponse.getState()) ? "Command Success" : "Command Failed";

          handler.post(() -> Toast
              .makeText(binding.getRoot().getContext(), message, Toast.LENGTH_SHORT)
              .show());
        });
      });

      binding.ivBell.setOnClickListener(v -> Toast.makeText(binding.getRoot().getContext(), "TODO Notify", Toast.LENGTH_SHORT).show());
    }

    private void bind(ResourceEx resource, RecyclerItemResourceBinding binding, LifecycleOwner owner) {
      if (isControlResource(resource)) {
        AttributeEx attribute = resource.getAttribute(CapabilityOnOff.AttributeId.ON_FLAG);

        if (null == attribute) {
          Log.d(TAG, "rebind: no on flag attribute");
          return;
        }

        attribute.getCurrentValueObservable().observe(owner, currentValue -> {
          if (currentValue instanceof Boolean) {
            setControlValues(binding, (Boolean) currentValue);
          }
        });
      } else {
        binding.tvPowerDescription.setVisibility(View.GONE);
        binding.tvPowerValue.setVisibility(View.GONE);
      }

      if (isTemperatureResource(resource)) {
        AttributeEx attribute = resource.getAttribute(CapabilityTemperatureSensing.AttributeId.CURRENT_TEMP_CELSIUS);

        if (null == attribute) {
          Log.d(TAG, "rebind: no current temperature attribute");
          return;
        }

        attribute.getCurrentValueObservable().observe(owner, currentValue -> {
          if (currentValue instanceof Double) {
            setTemperatureValues(binding, (Double) currentValue);
          }
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
      binding.btOnOff.setVisibility(View.VISIBLE);
      binding.btOnOff.setChecked(onFlag);
    }
  }
}
