package com.jec.ac.jp.musicplayer;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.io.File;

public class SDListActivity extends AppCompatActivity {

    private TextView pathTxt;
    private RowModelAdapter adapter;
    private String currentPath;
    private String initPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sdlist);

        ImageButton backButton = findViewById(R.id.xmlDismissButton);
        backButton.setOnClickListener(v -> finish());

        pathTxt = findViewById(R.id.xmlPathTxt);

        adapter = new RowModelAdapter(this);

        File path = Environment.getExternalStorageDirectory();
        final File[] files = path.listFiles();
        if (files != null) {
            for (int i = 0; i < files.length; i++) {
                adapter.add(new RowModel(files[i]));
            }
        }

        initPath = path.getAbsolutePath();
        pathTxt.setText("PATH: " + path.getAbsolutePath());


        ListView list = findViewById(R.id.xmlListView);
        list.setAdapter(adapter);
        list.setOnItemClickListener((parent, view, pos, id) -> {
            ListView listView = (ListView) parent;
            RowModel item = (RowModel) listView.getItemAtPosition(pos);

            if (item.isMediaFile()) {
                Intent intent = getIntent();
                intent.putExtra("SELECTED_FILE", item.getFile().getAbsoluteFile().toString());
                setResult(RESULT_OK, intent);
                finish();
            } else if (item.isDirectory()) {
                String newPath = item.getFile().getAbsolutePath();
                displayFiles(newPath);
            } else if (item.getFile() == null) {
                String[] dirs = currentPath.split("/");
                StringBuilder newPath = new StringBuilder();
                for (int i = 0; i < dirs.length - 1; i++) {
                    newPath.append(dirs[i]).append("/");
                }
                displayFiles(newPath.toString());
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(SDListActivity.this);

                builder.setTitle("Unexecutable");
                builder.setMessage("音源ファイル以外のファイルは使用できません");
                builder.setPositiveButton("確認", null);

                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
    }

    private void displayFiles(String path) {
        adapter.clear();

        File directory = new File(path);
        currentPath = directory.getAbsolutePath(); // 현재 디렉토리 경로를 업데이트합니다.

        if (!currentPath.equals(initPath)) {
            adapter.add(new RowModel(null));
        }
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                adapter.add(new RowModel(file));
            }
        }

        TextView pathTxt = findViewById(R.id.xmlPathTxt);
        pathTxt.setText("PATH: " + currentPath); // 표시 중인 디렉토리 경로를 업데이트합니다.
    }


    final class RowModelAdapter extends ArrayAdapter<RowModel> {
        public RowModelAdapter(@NonNull Context context) {
            super(context, R.layout.listview_row);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            RowModel row = getItem(position);

            if (convertView == null) {
                LayoutInflater inflater = getLayoutInflater();
                convertView = inflater.inflate(R.layout.listview_row, null);
            }

            if (row != null) {
                ImageView icon = convertView.findViewById(R.id.xmlFileIcon);
                TextView name = convertView.findViewById(R.id.xmlFileName);
                TextView size = convertView.findViewById(R.id.xmlFileSize);

                if (icon != null) {
                    icon.setImageResource(row.getIcon());
                }
                if (name != null) {
                    name.setText(row.getFileName());
                }
                if (size != null) {
                    if (row.getFileSize() == 0) {
                        size.setText("");
                    } else {
                        size.setText(String.valueOf(row.getFileSize()));
                    }
                }
            }
            return convertView;
        }
    }
}