package vandy.mooc.view.ui;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.util.ArrayList;
import java.util.List;

import retrofit.client.Response;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import vandy.mooc.R;
import vandy.mooc.model.mediator.VideoDataMediator;
import vandy.mooc.model.mediator.webdata.Video;

/**
 * Show the view for each Video's meta-data in a ListView.
 */
public class VideoAdapter
        extends BaseAdapter {

    public static final String TAG = VideoAdapter.class.getSimpleName();

    /**
     * Allows access to application-specific resources and classes.
     */
    private final Context mContext;

    private boolean mNotifyChanged = false;

    /**
     * ArrayList to hold list of Videos that is shown in ListView.
     */
    private List<Video> videoList =
            new ArrayList<>();

    /**
     * Construtor that stores the Application Context.
     */
    public VideoAdapter(Context context) {
        super();
        mContext = context;
    }

    /**
     * Method used by the ListView to "get" the "view" for each row of
     * data in the ListView.
     *
     * @param position The position of the item within the adapter's data
     *                 set of the item whose view we want. convertView The
     *                 old view to reuse, if possible. Note: You should
     *                 check that this view is non-null and of an
     *                 appropriate type before using. If it is not possible
     *                 to convert this view to display the correct data,
     *                 this method can create a new view. Heterogeneous
     *                 lists can specify their number of view types, so
     *                 that this View is always of the right type (see
     *                 getViewTypeCount() and getItemViewType(int)).
     * @param parent   The parent that this view will eventually be
     *                 attached to
     * @return A View corresponding to the data at the specified
     * position.
     */
    public View getView(final int position,
            View convertView,
            ViewGroup parent) {
        Video video = videoList.get(position);

        if (convertView == null) {
            LayoutInflater mInflater = (LayoutInflater)
                    mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView =
                    mInflater.inflate(R.layout.video_list_item, null);
        }

        TextView titleText =
                (TextView) convertView.findViewById(R.id.tvVideoTitle);
        titleText.setText(video.getName());
        TextView likeNumber =
                (TextView) convertView.findViewById(R.id.tvVideoLikeNumber);
        likeNumber.setText(String.valueOf(video.getLikes()));
        final ToggleButton likeButton = ((ToggleButton) convertView.findViewById(R.id.likeButton));
        // whatever
        if (!mNotifyChanged) {
            likeButton.setChecked(video.getLikes() > 0);
        }
        likeButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Video v = videoList.get(position);
                final VideoDataMediator mediator = new VideoDataMediator(mContext);
                if (isChecked) {
                    mediator.like(v.getId())
                            .map(new Func1<Response, List<Video>>() {
                                @Override
                                public List<Video> call(Response response) {
                                    return mediator.getVideoList();
                                }
                            })
                            .subscribeOn(Schedulers.newThread())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new Action1<List<Video>>() {
                                @Override
                                public void call(List<Video> videos) {
                                    setVideos(videos);
                                }
                            }, new Action1<Throwable>() {
                                @Override
                                public void call(Throwable throwable) {
                                    Log.e(TAG, "" + throwable.getLocalizedMessage());
                                }
                            });
                } else {
                    mediator.unlike(v.getId())
                            .map(new Func1<Response, List<Video>>() {
                                @Override
                                public List<Video> call(Response response) {
                                    return mediator.getVideoList();
                                }
                            })
                            .subscribeOn(Schedulers.newThread())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new Action1<List<Video>>() {
                                @Override
                                public void call(List<Video> videos) {
                                    setVideos(videos);
                                }
                            }, new Action1<Throwable>() {
                                @Override
                                public void call(Throwable throwable) {
                                    Log.e(TAG, "" + throwable.getLocalizedMessage());
                                }
                            });
                }
            }
        });
        return convertView;
    }

    /**
     * Adds a Video to the Adapter and notify the change.
     */
    public void add(Video video) {
        videoList.add(video);
        notifyDataSetChanged();
    }

    /**
     * Removes a Video from the Adapter and notify the change.
     */
    public void remove(Video video) {
        videoList.remove(video);
        notifyDataSetChanged();
    }

    /**
     * Get the List of Videos from Adapter.
     */
    public List<Video> getVideos() {
        return videoList;
    }

    /**
     * Set the Adapter to list of Videos.
     */
    public void setVideos(List<Video> videos) {
        this.videoList = videos;
        notifyDataSetChanged();
    }

    /**
     * Get the no of videos in adapter.
     */
    public int getCount() {
        return videoList.size();
    }

    /**
     * Get video from a given position.
     */
    public Video getItem(int position) {
        return videoList.get(position);
    }

    /**
     * Get Id of video from a given position.
     */
    public long getItemId(int position) {
        return position;
    }
}
