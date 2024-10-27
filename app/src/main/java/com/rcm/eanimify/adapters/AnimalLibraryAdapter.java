package com.rcm.eanimify.adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.rcm.eanimify.R;
import com.rcm.eanimify.animalDetails.AnimalDetailsActivity;
import com.rcm.eanimify.ui.animalLibrary.AnimalLibraryFragment;

import java.util.Collections;
import java.util.List;

public class AnimalLibraryAdapter extends RecyclerView.Adapter<AnimalLibraryAdapter.ViewHolder> {

    private List<String> dataList;
    private Context context;
    private FragmentManager fragmentManager;

    public AnimalLibraryAdapter(List<String> dataList, Context context, FragmentManager childFragmentManager) {
        this.dataList = dataList;
        this.context = context; // Store the context
        this.fragmentManager = childFragmentManager;
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
        Collections.sort(this.dataList);
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
        holder.textView.setText(animalName);

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, AnimalDetailsActivity.class);
            intent.putExtra("animalName", animalName);
            context.startActivity(intent);

            Log.d("AnimalLibraryAdapter", "Clicked animal: " + animalName); // Log the clicked animal
        });
    }
    @Override
    public int getItemCount() {
        return dataList.size();
    }


}
