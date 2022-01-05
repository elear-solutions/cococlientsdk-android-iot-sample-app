package buzz.getcoco.iot.sample.adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.recyclerview.widget.RecyclerView;
import buzz.getcoco.iot.android.ResourceEx;
import buzz.getcoco.iot.sample.databinding.RecyclerItemResourceBinding;
import buzz.getcoco.iot.sample.viewholders.ResourceItemViewHolder;
import java.util.ArrayList;
import java.util.List;

public class ResourceTileAdapter extends RecyclerView.Adapter<ResourceItemViewHolder> {

  private static final String TAG = "ResourceTileAdapter";

  @NonNull
  private final LifecycleOwner owner;

  private List<ResourceEx> resourceList = new ArrayList<>();

  public ResourceTileAdapter(@NonNull LiveData<List<ResourceEx>> resourcesObservable, @NonNull LifecycleOwner owner) {
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
    holder.setResource(resourceList.get(position));
  }

  @Override
  public int getItemCount() {
    return resourceList.size();
  }
}
