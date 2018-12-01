package client.github.ar.com.br.githubclient.adapters;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

import client.github.ar.com.br.githubclient.R;
import client.github.ar.com.br.githubclient.models.Repo;
import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;

public class RepoItemAdapter extends RecyclerView.Adapter<RepoItemAdapter.MyViewHolder> {

    private final PublishSubject<Repo> onClickSubject = PublishSubject.create();

    private List<Repo> items;
    private Activity context;

    public RepoItemAdapter(List<Repo> items, Activity context) {
        this.items = items;
        this.context = context;
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
        ImageView imageView = myViewHolder.imageView;

        descriptionTextView.setText(item.getFull_name());

        Glide.with(context).load(item.getOwner().getAvatar_url()).into(imageView);

        myViewHolder.itemView.setOnClickListener(v -> {
            onClickSubject.onNext(item);
        });
    }

    @Override
    public int getItemCount() {
        return this.items.size();
    }

    public List<Repo> getList() { return this.items; }

    public Observable<Repo> getItemClicked() {
        return onClickSubject.hide();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView descriptionTextView;
        private ImageView imageView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            this.imageView = itemView.findViewById(R.id.avatar_image_view);
            this.descriptionTextView = itemView.findViewById(R.id.description_text_view);
        }
    }
}
