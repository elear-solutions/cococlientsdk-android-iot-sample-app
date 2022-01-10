package buzz.getcoco.iot.sample.adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import buzz.getcoco.iot.Network;
import buzz.getcoco.iot.sample.databinding.RecyclerItemNetworkBinding;
import java.util.ArrayList;
import java.util.List;

public class NetworkListAdapter extends RecyclerView.Adapter<NetworkListAdapter.NetworkViewHolder> {

  private static final String TAG = "NetworkListAdapter";

  private final List<Network> networks = new ArrayList<>();
  private final ItemClickListener clickListener;

  public NetworkListAdapter(@NonNull ItemClickListener clickListener) {
    this.clickListener = clickListener;
  }

  @NonNull
  @Override
  public NetworkViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    RecyclerItemNetworkBinding binding = RecyclerItemNetworkBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);

    return new NetworkViewHolder(binding);
  }

  @Override
  public void onBindViewHolder(@NonNull NetworkViewHolder holder, int position) {
    Network network = networks.get(position);
    Log.d(TAG, "onBindViewHolder: Network: " + network);

    holder.binding.tvNetworkName.setText(network.getName());
    holder.binding.btnConnect.setOnClickListener(v -> clickListener.onConnectClicked(network));
  }

  @Override
  public int getItemCount() {
    return networks.size();
  }

  public void setItemList(@NonNull List<Network> networks) {
    this.networks.clear();
    this.networks.addAll(networks);

    notifyDataSetChanged();
  }

  protected static class NetworkViewHolder extends RecyclerView.ViewHolder {
    private final RecyclerItemNetworkBinding binding;

    public NetworkViewHolder(@NonNull RecyclerItemNetworkBinding binding) {
      super(binding.getRoot());

      this.binding = binding;
    }
  }

  public interface ItemClickListener {
   void onConnectClicked(@NonNull Network network);
  }
}
