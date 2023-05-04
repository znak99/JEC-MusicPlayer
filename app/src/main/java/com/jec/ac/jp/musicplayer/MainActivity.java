package com.jec.ac.jp.musicplayer;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private boolean isPlaying = false;
    private MediaPlayer mediaPlayer;
    private ImageButton playButton;
    private TextView currentSelectedMedia;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        playButton = findViewById(R.id.xmlPlayButton);
        playButton.setOnClickListener(new PlayButtonAction());

        currentSelectedMedia = findViewById(R.id.xmlPlayingMusicInfo);
        currentSelectedMedia.setText("SDカードから音楽を選択してください");
    }

    private void showAlert(String title, String msg, String btnTxt, DialogInterface.OnClickListener action) {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

        builder.setTitle(title);
        builder.setMessage(msg);
        builder.setPositiveButton(btnTxt, action);

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    final class PlayButtonAction implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            if (mediaPlayer == null) {
                showAlert("", "音楽を選択してください", "確認", null);
                return;
            }
        }
    }
}