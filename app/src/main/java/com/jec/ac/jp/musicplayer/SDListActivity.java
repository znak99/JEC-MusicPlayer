package com.jec.ac.jp.musicplayer;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.io.File;

public class SDListActivity extends AppCompatActivity {
    private RowModelAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sdlist);

        ImageButton backButton = findViewById(R.id.xmlDismissButton);
        backButton.setOnClickListener(v -> finish());

        adapter = new RowModelAdapter(this);

        File path = Environment.getExternalStorageDirectory();
        final File[] files = path.listFiles();
        if (files != null) {
            for (int i = 0; i < files.length; i++) {
                adapter.add(new RowModel(files[i]));
            }
        }

        ListView list = (ListView) findViewById(R.id.xmlListView);
        list.setAdapter(adapter);
        list.setOnItemClickListener((parent, view, pos, id) -> {
            ListView listView = (ListView) parent;
            RowModel item = (RowModel) listView.getItemAtPosition(pos);
            //TODO 선택시 처리
        });
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

                }
                if (name != null) {
                    name.setText(row.getFileName());
                }
                if (size != null) {
                    size.setText(String.valueOf(row.getFileSize()));
                }
            }
            return convertView;
        }
    }
}