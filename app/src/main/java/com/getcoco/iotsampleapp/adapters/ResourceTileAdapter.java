package com.getcoco.iotsampleapp.adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.recyclerview.widget.RecyclerView;

import com.getcoco.iotsampleapp.Globals;
import com.getcoco.iotsampleapp.R;
import com.getcoco.iotsampleapp.databinding.RecyclerItemResourceBinding;

import java.util.ArrayList;
import java.util.List;

import buzz.getcoco.api.Capability;
import buzz.getcoco.api.CapabilityOnOff;
import buzz.getcoco.api.CapabilityTemperatureSensing;
import buzz.getcoco.api.Command;
import buzz.getcoco.api.android.AttributeEx;
import buzz.getcoco.api.android.NetworkEx;
import buzz.getcoco.api.android.ResourceEx;
import buzz.getcoco.api.android.ZoneEx;

public class ResourceTileAdapter extends RecyclerView.Adapter<ResourceTileAdapter.ResourceItemViewHolder> {

    private static final int DEFAULT_ZONE  = 1;

    @NonNull
    private final LifecycleOwner owner;
    private final List<ResourceEx> resourceList = new ArrayList<>();

    private final MutableLiveData<NetworkEx> currentNetworkObservable = new MutableLiveData<>();

    public ResourceTileAdapter(@NonNull NetworkEx network, @NonNull LifecycleOwner owner) {
        this.owner = owner;

        Transformations
                .switchMap(currentNetworkObservable, networkEx -> {
                    ZoneEx defaultZone = networkEx.getZone(DEFAULT_ZONE);

                    return (null == defaultZone) ? new MutableLiveData<>() : defaultZone.getResourcesObservable();
                })
                .observe(owner, resources -> {
                    resourceList.clear();

                    resources.addAll(Globals.downCast(resources));
                    notifyDataSetChanged();
                });

        setNetwork(network);
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

    public void setNetwork(@NonNull NetworkEx network) {
        currentNetworkObservable.postValue(network);
    }

    protected static class ResourceItemViewHolder extends RecyclerView.ViewHolder {
        private static final String TAG = "ResourceItemVH";
        private ResourceEx resource;
        private final MutableLiveData<ResourceEx> currentResourceObservable = new MutableLiveData<>();

        public ResourceItemViewHolder(@NonNull RecyclerItemResourceBinding binding, @NonNull LifecycleOwner lifecycleOwner) {
            super(binding.getRoot());

            Transformations
                    .switchMap(currentResourceObservable, ResourceEx::getNameObservable)
                    .observe(lifecycleOwner, binding.tvResourceName::setText);

            Transformations
                    .switchMap(currentResourceObservable, resource -> {
                        AttributeEx attribute = isControlResource(resource) ? resource.getAttribute(CapabilityOnOff.AttributeId.ON_FLAG) : resource.getAttribute(CapabilityTemperatureSensing.AttributeId.CURRENT_TEMP_CELSIUS);

                        if (null != attribute)
                            return attribute.getCurrentValueObservable();
                        else
                            return new MutableLiveData<>();
                    })
                    .observe(lifecycleOwner, currentValue -> {
                        int visibility = View.GONE;

                        if (currentValue instanceof Boolean) {
                            boolean onFlag = (boolean) currentValue;
                            visibility = View.VISIBLE;

                            binding.tvValue.setText(onFlag ? R.string.on : R.string.off);
                            binding.btOnOff.setChecked(onFlag);
                        } else if (currentValue instanceof Integer) {
                            int temperature = (int) currentValue;
                            binding.tvValue.setText(binding.getRoot().getContext().getString(R.string.num_c, temperature));
                        }

                        binding.btOnOff.setVisibility(visibility);
                    });

            binding.btOnOff.setOnCheckedChangeListener((buttonView, isChecked) -> {
                CapabilityOnOff capabilityOnOff = resource.getCapability(Capability.CapabilityId.ON_OFF_CONTROL);

                if (null == capabilityOnOff)
                    return;

                Command<CapabilityOnOff.CommandId> command = isChecked ? new CapabilityOnOff.On() : new CapabilityOnOff.Off();

                capabilityOnOff.sendResourceCommand(command, (commandResponse, throwable) -> {
                    if (Command.State.SUCCESS != commandResponse.getState()) {
                        Log.d(TAG, "ResourceItemViewHolder: command failed");
                    }
                });
            });
        }
    }

    private static boolean isControlResource(ResourceEx resource) {
        return null != resource.getCapability(Capability.CapabilityId.ON_OFF_CONTROL);
    }
}

