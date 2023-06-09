package com.jec.ac.jp.musicplayer;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private int pausedTime = 0;
    private boolean isPlaying = false;
    private boolean isPreparing = false;
    private boolean isSeeking = false;

    private MediaPlayer mediaPlayer;
    private ImageButton playButton, sdCardButton, stopButton;
    private TextView currentSelectedMedia, playingTime;
    private FrameLayout visualizerFrameLayout;
    private SeekBar progressBar;
    private ImageView visuallizerImage1, visuallizerImage2, visuallizerImage3;
    private ObjectAnimator visualizerAnimator1, visualizerAnimator2, visualizerAnimator3;

    private static final int EXTERNAL_STORAGE = 1;
    private static final int UPDATE_PROGRESS = 1;
    private static final int PROGRESS_UPDATE_INTERVAL = 500;
    public static int REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (Build.VERSION.SDK_INT >= 23) {
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                    ActivityCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{
                                android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                android.Manifest.permission.READ_EXTERNAL_STORAGE
                        }, EXTERNAL_STORAGE);
            }
        }

        visualizerFrameLayout = findViewById(R.id.xmlVisualizerFrame);

        currentSelectedMedia = findViewById(R.id.xmlPlayingMusicInfo);
        currentSelectedMedia.setText("SDカードから音楽を選択してください");

        sdCardButton = findViewById(R.id.xmlShowSDCardButton);
        sdCardButton.setOnClickListener(new ButtonActions());

        playButton = findViewById(R.id.xmlPlayButton);
        playButton.setOnClickListener(new ButtonActions());

        stopButton = findViewById(R.id.xmlStopButton);
        stopButton.setOnClickListener(new ButtonActions());

        progressBar = findViewById(R.id.xmlProgressBar);

        progressBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    if (!isPreparing) {
                        mediaPlayer.seekTo(progress);
                    }
                    updatePlayingTime(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                isSeeking = true;
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                isSeeking = false;
            }
        });

        playingTime = findViewById(R.id.xmlPlayTimer);

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

    private void updatePlayingTime(int milliseconds) {
        int seconds = (milliseconds / 1000) % 60;
        int minutes = (milliseconds / (1000 * 60)) % 60;
        String time = String.format("%02d:%02d", minutes, seconds);
        playingTime.setText(time);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isPlaying) {
            progressUpdateHandler.sendEmptyMessage(UPDATE_PROGRESS);
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            // SeekBar 업데이트 중인 경우에만 사용자 조작에 따라 위치 업데이트
            if (isPlaying && !isSeeking) {
                progressUpdateHandler.sendEmptyMessage(UPDATE_PROGRESS);
            }
        } else {
            // 화면 포커스가 없는 경우에는 SeekBar 업데이트 중지
            progressUpdateHandler.removeMessages(UPDATE_PROGRESS);
        }
    }

    private Handler progressUpdateHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message message) {
            if (message.what == UPDATE_PROGRESS) {
                if (mediaPlayer != null) {
                    int currentPosition = mediaPlayer.getCurrentPosition();
                    progressBar.setProgress(currentPosition);
                    updatePlayingTime(currentPosition);
                }
                progressUpdateHandler.sendEmptyMessageDelayed(UPDATE_PROGRESS, PROGRESS_UPDATE_INTERVAL);
            }
            return true;
        }
    });

    @Override
    protected void onPause() {
        super.onPause();
        // 업데이트 중지
        progressUpdateHandler.removeMessages(UPDATE_PROGRESS);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        playButton.setImageResource(R.drawable.play);

        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            String path = data.getStringExtra("SELECTED_FILE");

            mediaPlayer = new MediaPlayer();

            try {
                mediaPlayer.setDataSource(MainActivity.this, Uri.parse(path));
                mediaPlayer.setOnCompletionListener(mediaPlayer -> {
                    mediaPlayer.pause();
                    pausedTime = 0;

                    isPlaying = false;
                    playButton.setImageResource(R.drawable.play);
                    stopAnim();
                });
                mediaPlayer.setOnPreparedListener(mediaPlayer -> {
                    Toast.makeText(this, "メディアを初期化しました。", Toast.LENGTH_SHORT).show();
                    String currentSelectedMusic = "";

                    if (path.contains("/")) {
                        String[] pAry = path.split("/");
                        currentSelectedMusic = pAry[pAry.length - 1];
                    }

                    currentSelectedMedia.setText("Now playing：" + currentSelectedMusic);
                    isPreparing = false;
                    pausedTime = 0;
                    stopAnim();
                    progressBar.setMax(mediaPlayer.getDuration());
                });
                mediaPlayer.prepareAsync();
                isPreparing = true;

                currentSelectedMedia.setText("メディアを初期化しています……");
            } catch (Exception e) {
                e.printStackTrace();
            }
            progressUpdateHandler.sendEmptyMessage(UPDATE_PROGRESS);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (grantResults.length <= 0) {
            return;
        }
        switch (requestCode) {
            case EXTERNAL_STORAGE: {
                if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    showAlert("Inaccessible", "権限がないとアプリを起動できません\n\nメディアへのアクセスを許可した後\nまた起動してください", "終了", (dialogInterface, i) -> {
                        Intent intent = new Intent();
                        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", getPackageName(), null);
                        intent.setData(uri);
                        startActivity(intent);
                        finish();
                    });
                }
            }
        }
        return ;
    }

    final class ButtonActions implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            int viewId = view.getId();
            if (viewId == R.id.xmlShowSDCardButton) {
                // SDCard
                if (mediaPlayer != null) {
                    mediaPlayer.pause();
                    pausedTime = mediaPlayer.getCurrentPosition();
                    isPlaying = false;
                    pauseAnim();
                }
                Intent intent = new Intent(MainActivity.this, SDListActivity.class);
                startActivityForResult(intent, REQUEST_CODE);
            } else if (viewId == R.id.xmlPlayButton) {
                // Play
                Log.i("MainActivity", "Play 버튼 눌림");
                if (isPreparing) {
                    showAlert("メディア初期化中", "メディアを初期化しています。", "確認", null);
                    return;
                }

                if (mediaPlayer == null) {
                    showAlert("", "音楽を選択してください", "確認", null);
                    return;
                }

                if (isPlaying) {
                    mediaPlayer.pause();
                    pausedTime = mediaPlayer.getCurrentPosition();

                    isPlaying = false;
                    playButton.setImageResource(R.drawable.play);
                    progressUpdateHandler.removeMessages(UPDATE_PROGRESS);
                    pauseAnim();
                } else {
                    mediaPlayer.seekTo(pausedTime);
                    mediaPlayer.start();

                    isPlaying = true;
                    playButton.setImageResource(R.drawable.pause);
                    progressUpdateHandler.sendEmptyMessage(UPDATE_PROGRESS);
                    startAnim();
                }

            } else if (viewId == R.id.xmlStopButton) {
                // Stop
                if (mediaPlayer == null) {
                    return;
                }
                mediaPlayer.stop();
                mediaPlayer.release();
                mediaPlayer = null;
                pausedTime = 0;
                isPlaying = false;
                playButton.setImageResource(R.drawable.play);
                currentSelectedMedia.setText("SDカードから音楽を選択してください");
                progressUpdateHandler.removeMessages(UPDATE_PROGRESS);
                stopAnim();
            }
        }
    }
}