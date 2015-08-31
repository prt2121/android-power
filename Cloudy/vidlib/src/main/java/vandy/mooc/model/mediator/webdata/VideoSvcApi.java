package vandy.mooc.model.mediator.webdata;

import java.util.Collection;

import retrofit.client.Response;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.Multipart;
import retrofit.http.POST;
import retrofit.http.Part;
import retrofit.http.Path;
import retrofit.http.Streaming;
import retrofit.mime.TypedFile;
import rx.Observable;

/**
 * This interface defines an API for a VideoSvc. The
 * interface is used to provide a contract for client/server
 * interactions. The interface is annotated with Retrofit
 * annotations so that clients can automatically convert the
 *
 * @author jules
 */
public interface VideoSvcApi {

    String DATA_PARAMETER = "data";

    String ID_PARAMETER = "id";

    String TOKEN_PATH = "/oauth/token";

    // The path where we expect the VideoSvc to live
    String VIDEO_SVC_PATH = "/video";

    // The path where we expect the VideoSvc to live
    String VIDEO_DATA_PATH = VIDEO_SVC_PATH + "/{" + VideoSvcApi.ID_PARAMETER + "}/data";


    @GET(VIDEO_SVC_PATH)
    Collection<Video> getVideoList();

    @GET(VIDEO_SVC_PATH + "/{id}")
    Video getVideoById(@Path("id") long id);

    @POST(VIDEO_SVC_PATH)
    Video addVideo(@Body Video v);

    @POST(VIDEO_SVC_PATH + "/{id}/rating/{rating}")
    AverageVideoRating rateVideo(@Path("id") long id, @Path("rating") int rating);

    @GET(VIDEO_SVC_PATH + "/{id}/rating")
    AverageVideoRating getVideoRating(@Path("id") long id);

    @Multipart
    @POST(VIDEO_DATA_PATH)
    VideoStatus setVideoData(@Path(ID_PARAMETER) long id, @Part(DATA_PARAMETER) TypedFile videoData);

    /**
     * This method uses Retrofit's @Streaming annotation to indicate that the
     * method is going to access a large stream of data (e.g., the mpeg video
     * data on the server). The client can access this stream of data by obtaining
     * an InputStream from the Response as shown below:
     *
     * VideoSvcApi client = ... // use retrofit to create the client
     * Response response = client.getData(someVideoId);
     * InputStream videoDataStream = response.getBody().in();
     */
    @Streaming
    @GET(VIDEO_DATA_PATH)
    Response getVideoData(@Path(ID_PARAMETER) long id);

    @POST(VIDEO_SVC_PATH + "/{id}/like")
    Observable<Response> likeVideo(@Path("id") long id, @Body String emptyString);

    @POST(VIDEO_SVC_PATH + "/{id}/unlike")
    Observable<Response> unlikeVideo(@Path("id") long id, @Body String emptyString);

}
