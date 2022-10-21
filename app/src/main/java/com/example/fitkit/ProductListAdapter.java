package com.example.fitkit;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;

public class ProductListAdapter extends FirestoreRecyclerAdapter<Product, ProductListAdapter.ViewHolder> {

    Context context;
    private OnItemClickListener listener;

    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query
     */
    public ProductListAdapter(@NonNull FirestoreRecyclerOptions<Product> options) {
        super(options);
    }

    // Create new views (invoked by the layout manager)
    @NonNull
    @Override
    public ProductListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.product_list_item, parent, false);
        context = parent.getContext();
        return new ViewHolder(view);
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull Product product) {
        // Add something for the image
        holder.prodName.setText(product.getName());
        holder.prodPrice.setText(String.format("%.2f KD", product.getPrice()));
        Glide.with(context)
                .load(product.getImg_links().get("img1"))
                .into(holder.prodImage);
    }


//    private ArrayList<Product> products;
//
//    // Initialize dataset of the adapter
//    public ProductListAdapter(ArrayList<Product> products) {
//        this.products = products;
//    }
//    // Replace the contents of a view (invoked by the layout manager)
//    @Override
//    public void onBindViewHolder(@NonNull ProductListAdapter.ViewHolder holder, int position) {
//        // Add something for the image
//        holder.prodName.setText(products.get(position).getName());
//        holder.prodPrice.setText(products.get(position).getPrice() + " KD");
//    }

//    // Return the size of the dataset (invoked by the layout manager)
//    @Override
//    public int getItemCount() {
//        return products.size();
//    }


    // Reference of type of view used in the RecyclerView
    public class ViewHolder extends RecyclerView.ViewHolder{
        ImageView prodImage;
        TextView prodName;
        TextView prodPrice;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            prodImage = itemView.findViewById(R.id.prodImage);
            prodName = itemView.findViewById(R.id.prodName);
            prodPrice = itemView.findViewById(R.id.prodPrice);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION && listener != null){
                        listener.onItemClick(getSnapshots().getSnapshot(position), position);
                    }
                }
            });
//            itemView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    int position = getAdapterPosition();
//                    if (position != RecyclerView.NO_POSITION){
//                        DocumentSnapshot documentSnapshot = getSnapshots().getSnapshot(position);
//                        Product product = documentSnapshot.toObject(Product.class);
//
//                        Log.d("P_ADAPT", product.toString());
//                        Intent intentToView = new Intent(context, ProductDisplay.class);
//                        intentToView.putExtra("product", product);
//                        context.startActivity(intentToView);
//                    }
//                }
//            });
        }
    }

    public interface OnItemClickListener {
        void onItemClick(DocumentSnapshot documentSnapshot, int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }
}
