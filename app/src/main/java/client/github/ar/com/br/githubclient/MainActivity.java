package client.github.ar.com.br.githubclient;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import client.github.ar.com.br.githubclient.adapters.MyItemDecoration;
import client.github.ar.com.br.githubclient.adapters.RepoItemAdapter;
import client.github.ar.com.br.githubclient.models.Repo;
import client.github.ar.com.br.githubclient.pages.Details;
import client.github.ar.com.br.githubclient.services.GithubService;
import client.github.ar.com.br.githubclient.services.RetrofitGenerator;
import retrofit2.Call;

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

        this.service = RetrofitGenerator.getInstance();

        this.configureActions();

    }

    private void configureActions() {

        this.refresher.setOnRefreshListener(this);
        this.refresher.post(() -> new AsyncGithubTask(MainActivity.this, null).execute());

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

    private void navigate(Integer id) {
        Intent intent = new Intent(this, Details.class);

        intent.putExtra("repoId", id);

        startActivity(intent);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.getMenuInflater().inflate(R.menu.main, menu);

        MenuItem item = menu.findItem(R.id.action_search);
        MenuItemCompat.setOnActionExpandListener(item, new MenuItemCompat.OnActionExpandListener() {

            @Override
            public boolean onMenuItemActionExpand(MenuItem menuItem) {
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem menuItem) {
                return true;
            }
        });

        SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {

                new AsyncGithubTask(MainActivity.this, query).execute();

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onRefresh() {
        runOnUiThread(() -> new AsyncGithubTask(MainActivity.this, null).execute());
    }

    private class AsyncGithubTask extends AsyncTask<Void, Void, List<Repo>> {

        private Activity context;
        private String username;

        public AsyncGithubTask(
                Activity context,
                String username
        ) {
            this.context = context;
            this.username = username;
        }


        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            refresher.setRefreshing(true);
        }

        @Override
        protected List<Repo> doInBackground(Void... voids) {

            try {
                List<Repo> list;

                Call<List<Repo>> call = null;

                if (this.username != null) {
                    call = service.listReposByUser(this.username);
                } else {
                    call = service.listRepositories();

                }
                list = call.execute().body();

                return list;

            } catch (IOException e) {
                e.printStackTrace();
            }

            return new ArrayList<>();

        }

        @SuppressLint("CheckResult")
        @Override
        protected void onPostExecute(List<Repo> repos) {
            super.onPostExecute(repos);

            if (repos != null) {
                RepoItemAdapter adapter = new RepoItemAdapter(repos, context);

                listView.setAdapter(adapter);

                adapter.getItemClicked().subscribe(t -> navigate(t.getId()));

            } else {

                listView.setVisibility(View.GONE);

                Snackbar.make(
                        context.findViewById(R.id.contentMain),
                        "Nenhum repositório encontrado para este usuário!",
                        Snackbar.LENGTH_LONG
                ).show();
            }
            refresher.setRefreshing(false);

        }
    }
}
