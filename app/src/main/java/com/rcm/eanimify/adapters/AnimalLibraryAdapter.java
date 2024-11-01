package com.rcm.eanimify.adapters;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Filterable;
import android.widget.Filter;
import android.widget.TextView;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.RecyclerView;

import com.rcm.eanimify.R;
import com.rcm.eanimify.animalDetails.AnimalDetailsActivity;
import com.rcm.eanimify.ui.animalLibrary.AnimalLibraryFragment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AnimalLibraryAdapter extends RecyclerView.Adapter<AnimalLibraryAdapter.ViewHolder> implements Filterable{

    private List<String> dataList;
    private Context context;
    private FragmentManager fragmentManager;
    private List<String> originalAnimalList;
    private List<String> animalList;

    public AnimalLibraryAdapter(List<String> dataList, Context context, FragmentManager childFragmentManager) {
        this.dataList = dataList;
        this.context = context; // Store the context
        this.fragmentManager = childFragmentManager;
        this.originalAnimalList = new ArrayList<>(dataList);
        this.animalList = dataList;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        public TextView textView;

        public ViewHolder(View view){
            super(view);
            textView = view.findViewById(R.id.textView);
        }
    }

    public void setDataList(List<String> dataList) {
        this.dataList = dataList;
        this.originalAnimalList = new ArrayList<>(dataList);
        this.animalList = dataList;
//        Collections.sort(this.dataList);
        notifyDataSetChanged();
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_layout, parent, false);
        return new ViewHolder(view);
    }
    public interface OnAnimalClickListener {
        void onAnimalClick(String animalName);
    }

    private OnAnimalClickListener listener; // Add listener variable

    public void setOnAnimalClickListener(OnAnimalClickListener listener) {
        this.listener = listener;
    }
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String animalName = dataList.get(position);
        if (isHeader(animalName)) {
            SpannableString spannableString = new SpannableString(animalName);
            spannableString.setSpan(new StyleSpan(Typeface.BOLD), 0, animalName.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            spannableString.setSpan(new UnderlineSpan(), 0, animalName.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            holder.textView.setText(spannableString); // Set styled text for headers
            holder.textView.setTypeface(null, Typeface.BOLD);
            holder.itemView.setOnClickListener(null);
            holder.itemView.setClickable(false);
            holder.itemView.setFocusable(false);
        } else {
            holder.textView.setText(animalName); // Set plain text for animal names
            holder.textView.setTypeface(null, Typeface.NORMAL);
            holder.itemView.setOnClickListener(v -> {
                Intent intent = new Intent(context, AnimalDetailsActivity.class);
                intent.putExtra("animalName", animalName);
                context.startActivity(intent);
                Log.d("AnimalLibraryAdapter", "Clicked animal: " + animalName);
            });
        }
//        holder.textView.setText(animalName);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(holder.itemView.getContext());
        int fontSize = sharedPreferences.getInt("font_size", 16); // Default to 16sp
        holder.textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize);
    }
    @Override
    public int getItemCount() {
        return dataList.size();
    }
    private Filter animalFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();
            ArrayList<String> filteredList = new ArrayList<>();

            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(originalAnimalList); // originalAnimalList is your full list
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();
                for (String animal : originalAnimalList) {
                    if (animal.toLowerCase().contains(filterPattern)) {
                        filteredList.add(animal);
                    }
                }
            }

            results.values = filteredList;
            results.count = filteredList.size();
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            animalList.clear(); // animalList is the list displayed in the RecyclerView
            animalList.addAll((ArrayList<String>) results.values);
            notifyDataSetChanged();
        }
    };
    private boolean isHeader(String item) {
        // You might need to adjust this logic based on how you store Endanger Levels
        return item.equals("CRITICALLY ENDANGERED (CR)") || item.equals("ENDANGERED (EN)") || item.equals("VULNERABLE (YU) ") || item.equals("OTHER THREATENED SPECIES (OTS) ");
    }
    @Override
    public Filter getFilter() {
        return animalFilter;
    }

}
