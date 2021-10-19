package com.getcoco.iotsampleapp.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.recyclerview.widget.RecyclerView;

import com.getcoco.iotsampleapp.databinding.RecyclerItemNetworkBinding;

import java.util.ArrayList;
import java.util.List;

import buzz.getcoco.api.android.NetworkEx;

public class NetworkListAdapter extends RecyclerView.Adapter<NetworkListAdapter.NetworkViewHolder> {

  private final List<NetworkEx> networks = new ArrayList<>();

  private final LifecycleOwner lifecycleOwner;
  private final ItemClickListener clickListener;

  public NetworkListAdapter(@NonNull LifecycleOwner lifecycleOwner, @NonNull ItemClickListener clickListener) {
    this.lifecycleOwner = lifecycleOwner;
    this.clickListener = clickListener;
  }

  @NonNull
  @Override
  public NetworkViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    RecyclerItemNetworkBinding binding = RecyclerItemNetworkBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);

    return new NetworkViewHolder(binding, lifecycleOwner, clickListener);
  }

  @Override
  public void onBindViewHolder(@NonNull NetworkViewHolder holder, int position) {
    holder.currentNetworkObservable.postValue(holder.currentNetwork = networks.get(position));
  }

  @Override
  public int getItemCount() {
    return networks.size();
  }

  public void setItemList(@NonNull List<NetworkEx> networks) {
    this.networks.clear();
    this.networks.addAll(networks);

    notifyDataSetChanged();
  }

  protected static class NetworkViewHolder extends RecyclerView.ViewHolder {
    private NetworkEx currentNetwork;
    private final MutableLiveData<NetworkEx> currentNetworkObservable = new MutableLiveData<>();

    public NetworkViewHolder(@NonNull RecyclerItemNetworkBinding binding,
                             @NonNull LifecycleOwner lifecycleOwner, @NonNull ItemClickListener clickListener) {
      super(binding.getRoot());

      Transformations
              .switchMap(currentNetworkObservable, network -> network.getNetworkNameObservable())
              .observe(lifecycleOwner, binding.tvNetworkName::setText);

      binding.btnConnect.setOnClickListener(v -> {
        clickListener.onConnectClicked(currentNetwork);
      });
    }
  }

  public interface ItemClickListener {
   void onConnectClicked(@NonNull NetworkEx network);
  }
}

