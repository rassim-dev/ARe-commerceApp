package com.example.fitkit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;

public class ProductListActivity extends AppCompatActivity {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference prodRef = db.collection("sports_equipment");

    ProductListAdapter adapter;

    private static final String TAG = "P_LIST";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_list);

//        HashMap<String, String> hm = new HashMap<String, String>();
//        hm.put("Image1", "img.com");
//        Product p = new Product("Test", "test", 10, 0, "model",
//                hm);
//        Log.d("PR_List", p.toString());
//
//        products = new ArrayList<Product>();
//        products.add(p);

//        prodRecyclerView = findViewById(R.id.prodRecyclerView);
//        adapter = new ProductListAdapter(products);
//        prodRecyclerView.setAdapter(adapter);
//        prodRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));

        setUpRecyclerView();
    }

    private void setUpRecyclerView() {
        //Query query = prodRef.orderBy("name");
        Query query = prodRef.orderBy(FieldPath.documentId());

        Log.d("PList", "after query");
        FirestoreRecyclerOptions<Product> options = new FirestoreRecyclerOptions.Builder<Product>()
                .setQuery(query, Product.class)
                .build();

        adapter = new ProductListAdapter(options);
        RecyclerView prodRecyclerView = findViewById(R.id.prodRecyclerView);
        prodRecyclerView.setHasFixedSize(true);
        // prodRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        prodRecyclerView.setLayoutManager(new WrapContentGridLayoutManager(this, 2));
        prodRecyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(new ProductListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int position) {
                Product product = documentSnapshot.toObject(Product.class);
                Log.d("P_ADAPT", product.toString());

                //adapter.stopListening();
                Intent intentToView = new Intent(ProductListActivity.this, ProductDisplay.class);
                intentToView.putExtra("product", product);
                startActivity(intentToView);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG,"onStart");
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop");
        adapter.stopListening();
    }
}