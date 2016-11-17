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

public class ReceiverFileListAdapter extends ArrayAdapter<String> {
    Context context;
    ArrayList<ReceiverFileProgress> receiverFileProgressArrayList;

    public ReceiverFileListAdapter(Context context, int resource, ArrayList<String> objects) {
        super(context, resource, objects);
        this.context = context;
        this.receiverFileProgressArrayList = new ArrayList<>();
        for (String f: objects) {
            this.receiverFileProgressArrayList.add(new ReceiverFileProgress(f));
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

        ReceiverFileProgress receiverFileProgress = receiverFileProgressArrayList.get(position);
        if (receiverFileProgress != null) {
            tvFileName.setText(receiverFileProgress.getFileName());
            if (receiverFileProgress.getFile() != null) {
                tvFileSentPercentage.setText((receiverFileProgress.getBytesSent() / receiverFileProgress.getFile().length()) * 100 + "% sent");
            }
            if (receiverFileProgress.getTimeTaken() != 0) {
                tvTimeTaken.setText(String.format("%.3f", receiverFileProgress.getTimeTaken() / Math.pow(10, 9)) + " sec");
            }

        }

        return view;
    }
}
