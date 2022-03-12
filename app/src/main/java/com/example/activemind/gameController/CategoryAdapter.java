package com.example.activemind.gameController;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.activemind.R;
import com.example.activemind.games.NumberMemoryActivity;
import com.example.activemind.games.SequenceMemoryActivity;
import com.example.activemind.games.WordMemoryActivity;

import java.util.ArrayList;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {

    Context context;
    ArrayList<CategoryModel> categoryModels;

    public CategoryAdapter(Context context, ArrayList<CategoryModel> categoryModels){
        this.context = context;
        this.categoryModels = categoryModels;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_category, null);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        CategoryModel model = categoryModels.get(position);

        holder.textView.setText(model.getCategoryName());
        holder.imageView.setImageResource(model.getCategoryImage());

        holder.itemView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if(model.getCategoryId() == "Number Memory Game"){
                    Intent intent = new Intent(context, NumberMemoryActivity.class);
                    intent.putExtra("catId",model.getCategoryId());
                    context.startActivity(intent);
                }
                else if(model.getCategoryId() == "Sequence Memory Game"){
                    Intent intent = new Intent(context, SequenceMemoryActivity.class);
                    intent.putExtra("catId",model.getCategoryId());
                    context.startActivity(intent);
                }
                else if(model.getCategoryId() == "Word Memory Game"){
                    Intent intent = new Intent(context, WordMemoryActivity.class);
                    intent.putExtra("catId",model.getCategoryId());
                    context.startActivity(intent);
                }
                else{
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return categoryModels.size();
    }

    public class CategoryViewHolder extends RecyclerView.ViewHolder{
        ImageView imageView;
        TextView textView;

        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById((R.id.image));
            textView = itemView.findViewById(R.id.category);
        }
    }
}
