package br.com.senai.colabtrack.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import br.com.senai.colabtrack.domain.Monitor;

/**
 * Created by kevin on 10/10/17.
 */

public class MonitorSpinnerAdapter extends ArrayAdapter<Monitor>{

    private Context context;
    private Monitor[] monitors;

    public MonitorSpinnerAdapter(Context context, int resourceId, Monitor[] monitors){
        super(context, resourceId, monitors);
        this.context = context;
        this.monitors = monitors;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        TextView label = new TextView(context);
        label.setTextColor(Color.BLACK);
        label.setText(monitors[position].getNome());
        label.setPadding(40, 40, 40, 40);
        return label;
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        TextView label = new TextView(context);
        label.setTextColor(Color.BLACK);
        label.setText(monitors[position].getNome());
        label.setPadding(40, 40, 40, 40);
        return label;
    }

}

