package com.getcoco.iotsampleapp.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.getcoco.iotsampleapp.R;
import com.getcoco.iotsampleapp.databinding.RecyclerItemNetworkBinding;

public class NetworkListAdapter extends RecyclerView.Adapter<NetworkListAdapter.NetworkViewHolder> {

  @NonNull
  @Override
  public NetworkViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    RecyclerItemNetworkBinding binding = RecyclerItemNetworkBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);

    return new NetworkViewHolder(binding);
  }

  @Override
  public void onBindViewHolder(@NonNull NetworkViewHolder holder, int position) {

  }

  @Override
  public int getItemCount() {
    return 0;
  }

  protected static class NetworkViewHolder extends RecyclerView.ViewHolder {
    final RecyclerItemNetworkBinding binding;

    public NetworkViewHolder(RecyclerItemNetworkBinding binding) {
      super(binding.getRoot());
      this.binding = binding;
    }
  }
}
