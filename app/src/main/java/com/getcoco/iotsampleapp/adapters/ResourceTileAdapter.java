package com.getcoco.iotsampleapp.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.getcoco.iotsampleapp.databinding.RecyclerItemResourceBinding;

public class ResourceTileAdapter extends RecyclerView.Adapter<ResourceTileAdapter.ResourceItemViewHolder> {
  @NonNull
  @Override
  public ResourceItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    RecyclerItemResourceBinding binding = RecyclerItemResourceBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);

    return new ResourceItemViewHolder(binding);
  }

  @Override
  public void onBindViewHolder(@NonNull ResourceItemViewHolder holder, int position) {

  }

  @Override
  public int getItemCount() {
    return 0;
  }

  protected static class ResourceItemViewHolder extends RecyclerView.ViewHolder {
    final RecyclerItemResourceBinding binding;

    public ResourceItemViewHolder(RecyclerItemResourceBinding binding) {
      super(binding.getRoot());
      this.binding = binding;
    }
  }
}

