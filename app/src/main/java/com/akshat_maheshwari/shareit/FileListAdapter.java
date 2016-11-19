package com.akshat_maheshwari.shareit;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
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
            final CheckBox cbToBeSent = (CheckBox) v.findViewById(R.id.cbToBeSent);

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
            if (context instanceof DisplayFilesActivity) {
                if (((DisplayFilesActivity) context).filesToBeSentSet.contains(file)) {
                    cbToBeSent.setChecked(true);
                } else {
                    cbToBeSent.setChecked(false);
                }
            }

            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (file.isDirectory()) {
                        files = ((DisplayFilesActivity) context).getVisibleFiles(file.getAbsolutePath());
                        ((DisplayFilesActivity) context).currentDirectoryPath = file.getAbsolutePath();
                        notifyDataSetChanged();
                    }
                    ((DisplayFilesActivity) context).tvCurrentFolder.setText(((DisplayFilesActivity) context).currentDirectoryPath);
                }
            });

            cbToBeSent.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if (context instanceof DisplayFilesActivity && compoundButton.isPressed()/* && !file.isDirectory()*/) {
                        if (b) {
                            ((DisplayFilesActivity) context).filesToBeSentSet.add(file);
                        } else {
                            if (((DisplayFilesActivity) context).filesToBeSentSet.contains(file)) {
                                ((DisplayFilesActivity) context).filesToBeSentSet.remove(file);
                            }
                        }
                        if (((DisplayFilesActivity) context).filesToBeSentSet.size() > 0) {
                            ((DisplayFilesActivity) context).bSend.setEnabled(true);
                        } else {
                            ((DisplayFilesActivity) context).bSend.setEnabled(false);
                        }
                        ((DisplayFilesActivity) context).bSend.setText("Send (" + ((DisplayFilesActivity) context).filesToBeSentSet.size() + ")");
                    }
                }
            });
        }

        return v;
    }

    @Override
    public int getCount() {
        return files.length;
    }
}
