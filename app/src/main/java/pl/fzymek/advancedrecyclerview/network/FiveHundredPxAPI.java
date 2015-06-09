package pl.fzymek.advancedrecyclerview.network;

import java.util.List;

import pl.fzymek.advancedrecyclerview.config.API;
import pl.fzymek.advancedrecyclerview.model.Image;
import pl.fzymek.advancedrecyclerview.model.Result;
import retrofit.http.GET;
import retrofit.http.Headers;
import retrofit.http.Query;
import rx.Observable;

/**
 * Created by Filip Zymek on 2015-06-08.
 */
public interface FiveHundredPxAPI {

	@Headers("Api-Key: " + API.CONSUMER_KEY)
	@GET("/search/images?phrase=panda&fields=detail_set,display_set")
	Observable<Result> getImages();

}
