package client.github.ar.com.br.githubclient.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import client.github.ar.com.br.githubclient.R;
import client.github.ar.com.br.githubclient.models.Repo;

public class RepoItemAdapter extends RecyclerView.Adapter<RepoItemAdapter.MyViewHolder> {

    private List<Repo> items;

    public RepoItemAdapter(List<Repo> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View rootView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.repo_item, viewGroup, false);
        RepoItemAdapter.MyViewHolder viewHolder = new RepoItemAdapter.MyViewHolder(rootView);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {
        final Repo item = this.items.get(i);

        TextView descriptionTextView = myViewHolder.descriptionTextView;

        descriptionTextView.setText(item.getName());
    }

    @Override
    public int getItemCount() {
        return this.items.size();
    }

    public List<Repo> getList() { return this.items; }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView descriptionTextView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            this.descriptionTextView = itemView.findViewById(R.id.description_text_view);
        }
    }
}
