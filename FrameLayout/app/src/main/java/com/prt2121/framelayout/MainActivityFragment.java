package com.prt2121.framelayout;

import android.graphics.SurfaceTexture;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;

import java.io.IOException;

public class MainActivityFragment extends Fragment {

    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        enableLabsMediaIntro(view);
        return view;
    }

    private MediaPlayer mediaPlayer = new MediaPlayer();

    private void enableLabsMediaIntro(View rootView) {
        View videoViewParent = rootView.findViewById(R.id.mediaContainer);
        videoViewParent.setVisibility(View.VISIBLE);
        TextureView textureView = (TextureView) videoViewParent.findViewById(R.id.mediaView);
        textureView.setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {
            @Override
            public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
                final String path = "android.resource://" + getActivity().getPackageName() + "/" + R.raw.bkg;
                Surface surf = new Surface(surface);
                try {
                    mediaPlayer.setDataSource(getActivity(), Uri.parse(path));
                    mediaPlayer.setLooping(true);
                    mediaPlayer.setSurface(surf);
                    mediaPlayer.prepareAsync();
                    // Play video when the media source is ready for playback.
                    mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                        @Override
                        public void onPrepared(MediaPlayer mediaPlayer) {
                            mediaPlayer.start();
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

            }

            @Override
            public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
                return false;
            }

            @Override
            public void onSurfaceTextureUpdated(SurfaceTexture surface) {

            }
        });
    }
}
