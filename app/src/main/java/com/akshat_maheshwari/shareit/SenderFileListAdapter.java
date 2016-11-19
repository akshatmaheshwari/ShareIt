package com.akshat_maheshwari.shareit;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.net.URLConnection;
import java.util.ArrayList;

/**
 * Created by Akshat Maheshwari on 17-11-2016.
 */

public class SenderFileListAdapter extends ArrayAdapter<File> {
    Context context;
    ArrayList<SenderFileProgress> senderFileProgressArrayList;

    public SenderFileListAdapter(Context context, int resource, ArrayList<File> objects) {
        super(context, resource, objects);
        this.context = context;
        this.senderFileProgressArrayList = new ArrayList<>();
        for (File f: objects) {
            this.senderFileProgressArrayList.add(new SenderFileProgress(f));
        }
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;

        if (view == null) {
            LayoutInflater layoutInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = layoutInflater.inflate(R.layout.sender_file_progress_list_item, null);
        }

        TextView tvFileName = (TextView) view.findViewById(R.id.tvFileName);
        TextView tvFileSentPercentage = (TextView) view.findViewById(R.id.tvFileSentPercentage);
        TextView tvTimeTaken = (TextView) view.findViewById(R.id.tvTimeTaken);
        ImageView ivFileType = (ImageView) view.findViewById(R.id.ivFileType);

        SenderFileProgress senderFileProgress = senderFileProgressArrayList.get(position);
        if (senderFileProgress != null) {
            tvFileName.setText(senderFileProgress.getFile().getName());
            System.out.println(senderFileProgress.getFileSize());
            tvFileSentPercentage.setText((senderFileProgress.getBytesSent() / senderFileProgress.getFileSize() * 100 + "% sent"));
            if (senderFileProgress.getTimeTaken() != 0) {
                tvTimeTaken.setText(String.format("%.3f", senderFileProgress.getTimeTaken() / Math.pow(10, 9)) + " sec");
            }
            if (senderFileProgress.getFile().isDirectory()) {
                ivFileType.setImageResource(R.drawable.ic_folder);
            } else {
                String extension = URLConnection.guessContentTypeFromName(senderFileProgress.getFile().getName());
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
        }

        return view;
    }
}
