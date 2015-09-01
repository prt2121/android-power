package vandy.mooc.model.mediator;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import retrofit.RestAdapter.LogLevel;
import retrofit.client.OkClient;
import retrofit.client.Response;
import retrofit.mime.TypedFile;
import rx.Observable;
import vandy.mooc.model.mediator.webdata.SecuredRestBuilder;
import vandy.mooc.model.mediator.webdata.UnsafeHttpsClient;
import vandy.mooc.model.mediator.webdata.Video;
import vandy.mooc.model.mediator.webdata.VideoStatus;
import vandy.mooc.model.mediator.webdata.VideoStatus.VideoState;
import vandy.mooc.model.mediator.webdata.VideoSvcApi;
import vandy.mooc.utils.Constants;
import vandy.mooc.utils.VideoMediaStoreUtils;
import vandy.mooc.view.SettingsActivity;

/**
 * Mediates communication between the Video Service and the local
 * storage on the Android device.  The methods in this class block, so
 * they should be called from a background thread (e.g., via an
 * AsyncTask).
 */
public class VideoDataMediator {

    public static final String TAG = VideoDataMediator.class.getSimpleName();

    private final String TEST_URL = "https://10.0.2.2:8443";

    /**
     * Status code to indicate that file is successfully
     * uploaded.
     */
    public static final String STATUS_UPLOAD_SUCCESSFUL =
            "Upload succeeded";

    /**
     * Status code to indicate that file upload failed
     * due to large video size.
     */
    public static final String STATUS_UPLOAD_ERROR_FILE_TOO_LARGE =
            "Upload failed: File too big";

    /**
     * Status code to indicate that file upload failed.
     */
    public static final String STATUS_UPLOAD_ERROR =
            "Upload failed";

    /**
     * Defines methods that communicate with the Video Service.
     */
    private VideoSvcApi mVideoServiceProxy;

    /**
     * Constructor that initializes the VideoDataMediator.
     */
    public VideoDataMediator(Context context) {

        SharedPreferences prefs =
                PreferenceManager.getDefaultSharedPreferences(context);

        String serverProtocol = prefs
                .getString(SettingsActivity.KEY_PREFERENCE_PROTOCOL,
                        "https");
        String serverIp = prefs
                .getString(SettingsActivity.KEY_PREFERENCE_IP_ADDRESS,
                        "10.0.2.2");
        String serverPort = prefs
                .getString(SettingsActivity.KEY_PREFERENCE_PORT,
                        "8443");
        String userName = prefs
                .getString(SettingsActivity.KEY_PREFERENCE_USER_NAME,
                        "admin");
        String password = prefs
                .getString(SettingsActivity.KEY_PREFERENCE_PASSWORD,
                        "pass");

        String serverUrl = serverProtocol
                + "://"
                + serverIp
                + ":"
                + serverPort;

//        String serverUrl = TEST_URL;

        Log.d(TAG, "serverUrl " + serverUrl);
        Log.d(TAG, "userName " + userName);
        Log.d(TAG, "password " + password);

        // Initialize the VideoServiceProxy.
        mVideoServiceProxy =
                new SecuredRestBuilder()
                        .setLoginEndpoint(serverUrl + VideoSvcApi.TOKEN_PATH)
                        .setEndpoint(serverUrl)
                        .setUsername(userName)
                        .setPassword(password)
                        .setClientId(Constants.CLIENT_ID)
                        .setClient(new OkClient(UnsafeHttpsClient.getUnsafeOkHttpClient()))
                        .setLogLevel(LogLevel.FULL)
                        .build()
                        .create(VideoSvcApi.class);

    }

    /**
     * Constructor that initializes the VideoDataMediator.
     */
    public VideoDataMediator(Context context, String username, String password) {

        SharedPreferences prefs =
                PreferenceManager.getDefaultSharedPreferences(context);

        String serverProtocol = prefs
                .getString(SettingsActivity.KEY_PREFERENCE_PROTOCOL,
                        "https");
        String serverIp = prefs
                .getString(SettingsActivity.KEY_PREFERENCE_IP_ADDRESS,
                        "10.0.2.2");
        String serverPort = prefs
                .getString(SettingsActivity.KEY_PREFERENCE_PORT,
                        "8443");

        String serverUrl = serverProtocol
                + "://"
                + serverIp
                + ":"
                + serverPort;

//        String serverUrl = TEST_URL;

        Log.d(TAG, "serverUrl " + serverUrl);
        Log.d(TAG, "userName " + username);
        Log.d(TAG, "password " + password);

        // Initialize the VideoServiceProxy.
        mVideoServiceProxy =
                new SecuredRestBuilder()
                        .setLoginEndpoint(serverUrl + VideoSvcApi.TOKEN_PATH)
                        .setEndpoint(serverUrl)
                        .setUsername(username)
                        .setPassword(password)
                        .setClientId(Constants.CLIENT_ID)
                        .setClient(new OkClient(UnsafeHttpsClient.getUnsafeOkHttpClient()))
                        .setLogLevel(LogLevel.FULL)
                        .build()
                        .create(VideoSvcApi.class);
    }


    /**
     * Uploads the Video having the given uri.
     *
     * @param videoUri uri of the Video to be uploaded.
     * @return A String indicating the status of the video upload operation.
     */
    public String uploadVideo(Context context,
            Uri videoUri) {
        // Get the path of video file from videoUri.
        String filePath =
                VideoMediaStoreUtils.getPath(context, videoUri);

        // Get the Video from Android Video Content Provider having
        // the given filePath.
        Video androidVideo =
                VideoMediaStoreUtils.getVideo(context, filePath);

        // Check if any such Video exists in Android Video Content
        // Provider.
        if (androidVideo != null) {
            // Prepare to Upload the Video data.

            // Create an instance of the file to upload.
            File videoFile = new File(filePath);

            // Check if the file size is less than the size of the
            // video that can be uploaded to the server.
            if (videoFile.length() < Constants.MAX_SIZE_MEGA_BYTE) {

                try {
                    // Add the metadata of the Video to the Video Service
                    // and get the resulting Video that contains
                    // additional meta-data (e.g., Id and ContentType)
                    // generated by the Video Service.
                    Video receivedVideo =
                            mVideoServiceProxy.addVideo(androidVideo);

                    // Check if the Server returns any Video metadata.
                    if (receivedVideo != null) {

                        // Finally, upload the Video data to the server
                        // and get the status of the uploaded video data.
                        VideoStatus status =
                                mVideoServiceProxy.setVideoData
                                        (receivedVideo.getId(),
                                                new TypedFile("video/mpeg", videoFile));

                        // Check if the Status of the Video or not.
                        if (status.getState() == VideoState.READY) {
                            // Video successfully uploaded.
                            return STATUS_UPLOAD_SUCCESSFUL;
                        }
                    }
                } catch (Exception e) {
                    // Error occured while uploading the video.
                    return STATUS_UPLOAD_ERROR;
                }
            } else
            // Video can't be uploaded due to large video size.
            {
                return STATUS_UPLOAD_ERROR_FILE_TOO_LARGE;
            }
        }

        // Error occured while uploading the video.
        return STATUS_UPLOAD_ERROR;
    }

    /**
     * Get the List of Videos from Video Service.
     *
     * @return the List of Videos from Server or null if there is
     * failure in getting the Videos.
     */
    public List<Video> getVideoList() {
        try {
            return (ArrayList<Video>)
                    mVideoServiceProxy.getVideoList();
        } catch (Exception e) {
            return null;
        }
    }

    public Observable<Response> like(long id) {
        return mVideoServiceProxy.likeVideo(id, "");
    }

    public Observable<Response> unlike(long id) {
        return mVideoServiceProxy.unlikeVideo(id, "");
    }

    /**
     * Uploads the Video meta data having the given uri.
     *
     * @param videoUri uri of the Video to be uploaded.
     * @return A String indicating the status of the upload operation.
     */
    public String uploadVideoMetaData(Context context,
            Uri videoUri) {
        // Get the path of video file from videoUri.
        String filePath =
                VideoMediaStoreUtils.getPath(context, videoUri);

        if (filePath == null) {
            filePath = VideoMediaStoreUtils.getRealPathFromURI(context, videoUri);
        }

        if (filePath == null) {
            filePath = videoUri.getPath();
        }

        Log.d(TAG, "filePath " + filePath);
        Log.d(TAG, "getPath " + videoUri.getPath());

        // Get the Video from Android Video Content Provider having
        // the given filePath.
        Video androidVideo =
                VideoMediaStoreUtils.getVideo(context, filePath);

        // Check if any such Video exists in Android Video Content
        // Provider.
        if (androidVideo != null && filePath != null) {
            // Prepare to Upload the Video data.

            // Create an instance of the file to upload.
            File videoFile = new File(filePath);

            // Check if the file size is less than the size of the
            // video that can be uploaded to the server.
            if (videoFile.length() < Constants.MAX_SIZE_MEGA_BYTE) {

                try {
                    // Add the metadata of the Video to the Video Service
                    // and get the resulting Video that contains
                    // additional meta-data (e.g., Id and ContentType)
                    // generated by the Video Service.
                    Video receivedVideo =
                            mVideoServiceProxy.addVideo(androidVideo);

                    // Check if the Server returns any Video metadata.
                    if (receivedVideo != null) {
                        return STATUS_UPLOAD_SUCCESSFUL;
                    }
                } catch (Exception e) {
                    // Error occured while uploading the video.
                    return STATUS_UPLOAD_ERROR;
                }
            } else {
                // Video can't be uploaded due to large video size.
                return STATUS_UPLOAD_ERROR_FILE_TOO_LARGE;
            }
        }

        // Error occured while uploading the video.
        return STATUS_UPLOAD_ERROR;
    }


}
