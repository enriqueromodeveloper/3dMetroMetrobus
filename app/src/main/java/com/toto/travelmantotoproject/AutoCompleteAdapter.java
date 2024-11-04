package com.toto.travelmantotoproject;

import com.toto.travelmantotoproject.model.Station;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

public class AutoCompleteAdapter extends ArrayAdapter<Station> {

    private Context context;
    private List<Station> stations;
    private List<Station> suggestions;
    public AutoCompleteAdapter(@NonNull Context context, int resource, List<Station> stations) {
        super(context, resource, stations);
        this.context = context;
        this.stations = stations;
        suggestions = new ArrayList<>();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.autocompletetext_row, parent, false);
        }
        Station station = stations.get(position);
        if (station != null) {
            TextView lblName = (TextView) view.findViewById(R.id.stationNameTextView);
            lblName.setText(station.getName());
        }
        return view;
    }

    @Override
    public Filter getFilter() {
        return nameFilter;
    }

    /**
     * Custom Filter implementation for custom suggestions we provide.
     */
    Filter nameFilter = new Filter() {
        @Override
        public CharSequence convertResultToString(Object resultValue) {
            String str = ((Station) resultValue).getName();
            return str;
        }

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            if (constraint != null) {
                suggestions.clear();
                for (Station station : stations) {
                    if (station.getName().toLowerCase().contains(constraint.toString().toLowerCase())) {
                        suggestions.add(station);
                    }
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = suggestions;
                filterResults.count = suggestions.size();
                return filterResults;
            } else {
                return new FilterResults();
            }
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            List<Station> filterList = (ArrayList<Station>) results.values;
            if (results != null && results.count > 0) {
                clear();
                for (Station station : filterList) {
                    add(station);
                    notifyDataSetChanged();
                }
            }
        }
    };

}
