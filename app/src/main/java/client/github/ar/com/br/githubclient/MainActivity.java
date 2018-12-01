package client.github.ar.com.br.githubclient;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.io.IOException;
import java.util.List;

import client.github.ar.com.br.githubclient.adapters.MyItemDecoration;
import client.github.ar.com.br.githubclient.adapters.RepoItemAdapter;
import client.github.ar.com.br.githubclient.models.Repo;
import client.github.ar.com.br.githubclient.services.GithubService;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    private GithubService service;
    private RecyclerView listView;
    private RecyclerView.LayoutManager mLayoutManager;
    private SwipeRefreshLayout refresher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.initGUI();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.github.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        this.service = retrofit.create(GithubService.class);

        this.configureActions();

    }

    private void configureActions() {

        this.refresher.setOnRefreshListener(this);
        this.refresher.post(new Runnable() {
            @Override
            public void run() {
                new AsyncGithubTask().execute();
            }
        });

    }

    private void initGUI() {
        this.listView = this.findViewById(R.id.repo_list);
        this.refresher = this.findViewById(R.id.refresher_repo_list);

        refresher.setOnRefreshListener(this);

        this.listView.setHasFixedSize(true);
        this.mLayoutManager = new LinearLayoutManager(this);
        this.listView.setLayoutManager(this.mLayoutManager);
        this.listView.addItemDecoration(new MyItemDecoration(this, LinearLayoutManager.VERTICAL));
    }

    @Override
    public void onRefresh() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                new AsyncGithubTask().execute();
            }
        });
    }

    private class AsyncGithubTask extends AsyncTask<Void, Void, List<Repo>> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            refresher.setRefreshing(true);
        }

        @Override
        protected List<Repo> doInBackground(Void... voids) {

            Call<List<Repo>> call = service.listRepositories();

            try {
                List<Repo> body = call.execute().body();

                return body;
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(List<Repo> repos) {
            super.onPostExecute(repos);

            RepoItemAdapter adapter = new RepoItemAdapter(repos);

            listView.setAdapter(adapter);

            refresher.setRefreshing(false);
        }
    }
}
