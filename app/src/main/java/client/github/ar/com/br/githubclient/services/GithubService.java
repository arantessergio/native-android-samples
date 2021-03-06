package client.github.ar.com.br.githubclient.services;

import java.util.List;

import client.github.ar.com.br.githubclient.models.Repo;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface GithubService {

    @GET("users/{user}/repos")
    Call<List<Repo>> listReposByUser(@Path("user") String user, @Query("page") int page, @Query("per_page") int perPage);

    @GET("repositories")
    Call<List<Repo>> listRepositories();

    @GET("repositories/{id}")
    Call<Repo> getRepo(@Path("id") Integer id);

}
