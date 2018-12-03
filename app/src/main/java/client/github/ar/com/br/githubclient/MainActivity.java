package client.github.ar.com.br.githubclient;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;

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
    private LinearLayoutManager mLayoutManager;
    private SwipeRefreshLayout refresher;
    private String query;
    private int page = 1;

    private List<Repo> publicRepos = new ArrayList<>();
    private List<Repo> byUsers = new ArrayList<>();

    private boolean loading = true;
    int pastVisibleItems, visibleItemCount, totalItemCount;

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
        this.refresher.post(() -> new AsyncGithubTask(MainActivity.this, query, page).execute());

        this.listView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0) {
                    visibleItemCount = mLayoutManager.getChildCount();
                    totalItemCount = mLayoutManager.getItemCount();
                    pastVisibleItems = mLayoutManager.findFirstVisibleItemPosition();

                    if (loading && query != null && !query.equals("")) {
                        if ((visibleItemCount + pastVisibleItems) >= totalItemCount) {
                            loading = false;

                            page = page + 1;

                            new AsyncGithubTask(MainActivity.this, query, page).execute();

                            loading = true;
                        }
                    }
                }
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

                MainActivity.this.query = query;

                new AsyncGithubTask(MainActivity.this, query, page).execute();

                return true;
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
        runOnUiThread(() -> new AsyncGithubTask(MainActivity.this, query, page).execute());
    }

    private class AsyncGithubTask extends AsyncTask<Void, Void, List<Repo>> {

        private Activity context;
        private String username;
        private Integer page;

        public AsyncGithubTask(
                Activity context,
                String username,
                Integer page
        ) {
            this.context = context;
            this.username = username;
            this.page = page;
        }


        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            refresher.setRefreshing(true);
        }

        @Override
        protected List<Repo> doInBackground(Void... voids) {

            try {

                Call<List<Repo>> call = null;

                List<Repo> result = new ArrayList<>();

                if (this.username != null) {
                    call = service.listReposByUser(this.username, this.page, 10);

                    result = call.execute().body();

                    if (result != null && result.size() > 0) {
                        byUsers.addAll(result);
                    }

                } else {
                    call = service.listRepositories();

                    result = call.execute().body();

                    if (result != null && result.size() > 0) {
                        publicRepos.addAll(result);
                    }

                }

                return result;

            } catch (IOException e) {
                e.printStackTrace();
            }

            return new ArrayList<>();

        }

        @Override
        protected void onPostExecute(List<Repo> repos) {
            super.onPostExecute(repos);

            if (query != null && !query.equals("")) {
                this.handleListByUser();
            } else {
                this.handlePublicList();
            }

            refresher.setRefreshing(false);

        }

        @SuppressLint("CheckResult")
        private void handlePublicList() {
            RepoItemAdapter adapter = new RepoItemAdapter(publicRepos, context);

            adapter.getItemClicked().subscribe(t -> navigate(t.getId()));

            listView.setAdapter(adapter);
        }

        @SuppressLint("CheckResult")
        private void handleListByUser() {
            RepoItemAdapter adapter = new RepoItemAdapter(byUsers, MainActivity.this);

            adapter.getItemClicked().subscribe(t -> navigate(t.getId()));

            listView.setAdapter(adapter);
        }
    }
}
