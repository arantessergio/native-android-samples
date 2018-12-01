package client.github.ar.com.br.githubclient.pages;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.io.IOException;
import java.text.SimpleDateFormat;

import client.github.ar.com.br.githubclient.R;
import client.github.ar.com.br.githubclient.models.Owner;
import client.github.ar.com.br.githubclient.models.Repo;
import client.github.ar.com.br.githubclient.services.GithubService;
import client.github.ar.com.br.githubclient.services.RetrofitGenerator;
import retrofit2.Call;

public class Details extends AppCompatActivity {

    private ImageView imageView;

    private Integer repoId;
    private GithubService service;
    private TextView ownerNameTextView;
    private TextView repoIdTextView;
    private TextView repoNameTextView;
    private TextView repoDescriptionTextView;
    private TextView repoUpdatedAtTextView;
    private TextView repoLicenseName;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.details);
        this.initGUI();

        Intent intent = getIntent();

        this.repoId = intent.getIntExtra("repoId", 0);

        this.service = RetrofitGenerator.getInstance();

        new RepoAsyncTask().execute();

    }

    private void initGUI() {
        this.imageView = findViewById(R.id.avatarDetailsImageView);
        this.ownerNameTextView = findViewById(R.id.detailsOwnerName);
        this.repoIdTextView= findViewById(R.id.detailsRepoId);
        this.repoNameTextView = findViewById(R.id.detailsRepoName);
        this.repoDescriptionTextView = findViewById(R.id.detailsRepoDescription);
        this.repoUpdatedAtTextView= findViewById(R.id.detailsRepoUpdatedAt);
        this.repoLicenseName = findViewById(R.id.detailsRepoLicenseName);
    }

    private void applyInformations(Repo repo) {
        this.repoNameTextView.setText(repo.getFull_name());

        this.repoIdTextView.setText("Id: " + repo.getId().toString());
        this.repoDescriptionTextView.setText(repo.getDescription());

        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/YYYY");

        String format = formatter.format(repo.getUpdated_at());

        this.repoUpdatedAtTextView.setText("Última atualização: " + format);

        String license = repo.getLicense_name();

        this.repoLicenseName.setText("Licença: " + (license != null ? license : "NA"));

        Owner owner = repo.getOwner();

        if (owner != null) {
            this.ownerNameTextView.setText(owner.getLogin());
            Glide.with(this).load(repo.getOwner().getAvatar_url()).into(this.imageView);
        }

    }

    private class RepoAsyncTask extends AsyncTask<Void, Void, Repo> {

        private ProgressDialog dialog;


        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            this.dialog = new ProgressDialog(Details.this);
            this.dialog.setMessage("Carregando...");
            this.dialog.show();
        }

        @Override
        protected Repo doInBackground(Void... voids) {

            try {
                Call<Repo> repo = service.getRepo(repoId);

                return repo.execute().body();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Repo repo) {
            super.onPostExecute(repo);

            applyInformations(repo);

            this.dialog.dismiss();
        }
    }
}
