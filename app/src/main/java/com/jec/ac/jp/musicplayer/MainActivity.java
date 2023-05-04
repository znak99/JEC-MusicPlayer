package com.jec.ac.jp.musicplayer;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private int pausedTime = 0;
    private boolean isPlaying = false;
    private MediaPlayer mediaPlayer;
    private ImageButton playButton, sdCardButton, stopButton;
    private TextView currentSelectedMedia;
    private FrameLayout visualizerFrameLayout;

    private ImageView visuallizerImage1, visuallizerImage2, visuallizerImage3;
    private ObjectAnimator visualizerAnimator1, visualizerAnimator2, visualizerAnimator3;

    public static int REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        visualizerFrameLayout = findViewById(R.id.xmlVisualizerFrame);

        currentSelectedMedia = findViewById(R.id.xmlPlayingMusicInfo);
        currentSelectedMedia.setText("SDカードから音楽を選択してください");

        sdCardButton = findViewById(R.id.xmlShowSDCardButton);
        sdCardButton.setOnClickListener(new ButtonActions());

        playButton = findViewById(R.id.xmlPlayButton);
        playButton.setOnClickListener(new ButtonActions());

        stopButton = findViewById(R.id.xmlStopButton);
        stopButton.setOnClickListener(new ButtonActions());

        visuallizerImage1 = createImageView(250, 250);
        visuallizerImage2 = createImageView(300, 300);
        visuallizerImage3 = createImageView(350, 350);

        visualizerAnimator1 = visualizerAnim(visuallizerImage1, 0);
        visualizerAnimator2 = visualizerAnim(visuallizerImage2, 500);
        visualizerAnimator3 = visualizerAnim(visuallizerImage3, 1000);
    }

    private ImageView createImageView(int width, int height) {
        ImageView view = new ImageView(MainActivity.this);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                width,height, Gravity.CENTER);

        view.setBackground(getDrawable(R.drawable.visualizer_circle));
        view.setAlpha(0f);
        view.setLayoutParams(params);

        visualizerFrameLayout.addView(view, 0);

        return view;
    }

    private ObjectAnimator visualizerAnim(ImageView view, long delay) {

        ObjectAnimator visualizerAnimation = ObjectAnimator.ofPropertyValuesHolder(
                view,
                PropertyValuesHolder.ofFloat("alpha", 0f, 1f, 0f)
        );
        visualizerAnimation.setDuration(1500);
        visualizerAnimation.setRepeatCount(-1);
        visualizerAnimation.setStartDelay(delay);
        visualizerAnimation.setRepeatMode(ValueAnimator.RESTART);

        return visualizerAnimation;
    }

    private void pauseAnim() {
        if (visualizerAnimator1 == null || visualizerAnimator2 == null || visualizerAnimator3 == null)
            return;
        visuallizerImage1.setVisibility(View.INVISIBLE);
        visuallizerImage2.setVisibility(View.INVISIBLE);
        visuallizerImage3.setVisibility(View.INVISIBLE);
        visualizerAnimator1.cancel();
        visualizerAnimator2.cancel();
        visualizerAnimator3.cancel();
    }

    private void stopAnim() {
        visualizerAnimator1.end();
        visualizerAnimator2.end();
        visualizerAnimator3.end();
    }

    private void startAnim() {
        if (visualizerAnimator1.isStarted() || visualizerAnimator2.isStarted() || visualizerAnimator3.isStarted())
            return;
        visuallizerImage1.setVisibility(View.VISIBLE);
        visuallizerImage2.setVisibility(View.VISIBLE);
        visuallizerImage3.setVisibility(View.VISIBLE);
        visualizerAnimator1.start();
        visualizerAnimator2.start();
        visualizerAnimator3.start();
    }

    private void showAlert(String title, String msg, String btnTxt, DialogInterface.OnClickListener action) {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

        builder.setTitle(title);
        builder.setMessage(msg);
        builder.setPositiveButton(btnTxt, action);

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    final class ButtonActions implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            int viewId = view.getId();
            if (viewId == R.id.xmlShowSDCardButton) {
                // SDCard
                stopAnim();
//                if (mediaPlayer != null) {
//                    mediaPlayer.pause();
//                    pausedTime = mediaPlayer.getCurrentPosition();
//                    isPlaying = false;
//                    visualizerAnimator.cancel();
//                }
//                Intent intent = new Intent(MainActivity.this, SDListActivity.class);
//                startActivity(intent);
            } else if (viewId == R.id.xmlPlayButton) {
                // Play
                startAnim();
//                if (mediaPlayer == null) {
//                    showAlert("", "音楽を選択してください", "確認", null);
//                    return;
//                }
            } else if (viewId == R.id.xmlStopButton) {
                // Stop
                if (mediaPlayer == null || !isPlaying) {
                    return;
                }
                mediaPlayer.stop();
                mediaPlayer.release();
                mediaPlayer = null;
                pausedTime = 0;
                isPlaying = false;
            }
        }
    }
}