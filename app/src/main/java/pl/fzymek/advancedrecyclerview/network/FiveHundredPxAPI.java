package pl.fzymek.advancedrecyclerview.network;

import pl.fzymek.advancedrecyclerview.config.API;
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
	@GET("/search/images?fields=detail_set,display_set")
	Observable<Result> getImages(@Query("phrase") String phrase);

}
