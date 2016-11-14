package com.akshat_maheshwari.shareit;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.net.URLConnection;

/**
 * Created by Akshat Maheshwari on 13-11-2016.
 */

public class FileListAdapter extends ArrayAdapter<File> {
    Context context;
    File[] files;

    public FileListAdapter(Context context, int resource, File[] objects) {
        super(context, resource, objects);
        this.context = context;
        this.files = objects;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;

        if (v == null) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.file_list_item, null);
        }

        final File file = files[position];

        if (file != null) {
            TextView tvFileName = (TextView) v.findViewById(R.id.tvFileName);
            ImageView ivFileType = (ImageView) v.findViewById(R.id.ivFileType);

            tvFileName.setText(file.getName());
            if (file.isDirectory()) {
                ivFileType.setImageResource(R.drawable.ic_folder);
            } else {
                String extension = URLConnection.guessContentTypeFromName(file.getName());
                if (extension == null) {
                    ivFileType.setImageResource(R.drawable.ic_style);
                } else {
                    if (extension.startsWith("image")) {
                        ivFileType.setImageResource(R.drawable.ic_image);
                    } else if (extension.startsWith("audio")) {
                        ivFileType.setImageResource(R.drawable.ic_audiotrack);
                    } else if (extension.startsWith("video")) {
                        ivFileType.setImageResource(R.drawable.ic_videocam);
                    } else {
                        ivFileType.setImageResource(R.drawable.ic_style);
                    }
                }
            }

            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (file.isDirectory()) {
                        Intent intent = new Intent(context, DisplayFilesActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putString("directoryPath", file.getAbsolutePath());
                        intent.putExtras(bundle);
                        context.startActivity(intent);
                    } else {
                    }
                }
            });
        }

        return v;
    }
}
