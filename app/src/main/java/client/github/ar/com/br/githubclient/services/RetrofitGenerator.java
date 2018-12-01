package client.github.ar.com.br.githubclient.services;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitGenerator {

    private static GithubService service;

    public static GithubService getInstance() {
        if (service != null) {
            return service;
        }

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.github.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        return retrofit.create(GithubService.class);
    }

}
