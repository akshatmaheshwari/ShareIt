package com.akshat_maheshwari.shareit;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by Akshat Maheshwari on 17-11-2016.
 */

public class SenderFileListAdapter extends ArrayAdapter<File> {
    Context context;
    ArrayList<FileProgress> senderFileProgressArrayList;

    public SenderFileListAdapter(Context context, int resource, ArrayList<File> objects) {
        super(context, resource, objects);
        this.context = context;
        this.senderFileProgressArrayList = new ArrayList<>();
        for (File f: objects) {
            this.senderFileProgressArrayList.add(new FileProgress(f));
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

        FileProgress senderFileProgress = senderFileProgressArrayList.get(position);
        if (senderFileProgress != null) {
            tvFileName.setText(senderFileProgress.getFile().getName());
            tvFileSentPercentage.setText((senderFileProgress.getBytesSent() / senderFileProgress.getFile().length()) * 100 + "% sent");
            if (senderFileProgress.getTimeTaken() != 0) {
                tvTimeTaken.setText(String.format("%.3f", senderFileProgress.getTimeTaken() / Math.pow(10, 9)) + " sec");
            }
        }

        return view;
    }
}
