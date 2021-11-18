package com.james1mumo.assignmentclassapp.ui.main;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.james1mumo.assignmentclassapp.HomeActivity;
import com.james1mumo.assignmentclassapp.R;
import com.shashank.sony.fancytoastlib.FancyToast;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PurchaseFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PurchaseFragment extends Fragment {
    ListView listView;
    private ProgressDialog progressDialog;
    public TextView textViewTotalPrice;
    public Button buttonCheckOut;
    String uid;


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public PurchaseFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PurchaseFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PurchaseFragment newInstance(String param1, String param2) {
        PurchaseFragment fragment = new PurchaseFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_purchase, container, false);

        progressDialog = new ProgressDialog(getActivity());
        listView = view.findViewById(R.id.listView);
        textViewTotalPrice = view.findViewById(R.id.textViewTotalPrice);
        buttonCheckOut = view.findViewById(R.id.buttonCheckOut);

        buttonCheckOut.setOnClickListener(v -> {
            FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(android.R.id.content, new CheckOutFragment(), "frag1");

            fragmentTransaction.commit();
        });

        progressDialog.setTitle("Please Wait...");
        progressDialog.setMessage("Fetching Items");
        progressDialog.setCanceledOnTouchOutside(false);
//        progressDialog.show();

        ArrayList<String> names = new ArrayList<>();
        ArrayList<String> prices = new ArrayList<>();
        ArrayList<String > images = new ArrayList<>();


        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Items");

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot request : snapshot.getChildren()){
                    names.add(request.child("name").getValue().toString());
                    prices.add(request.child("price").getValue().toString());
                    images.add(request.child("image").getValue().toString());
                }

                //create an adapter
                MyListAdapter myListAdapter = new MyListAdapter(getActivity(),getParentFragment(), names, prices, images);
                listView.setAdapter(myListAdapter);

                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressDialog.dismiss();

            }
        });

        uid = "testUid";
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        if (firebaseAuth.getCurrentUser() != null){
            uid = firebaseAuth.getUid();
        }
        DatabaseReference databaseReferenceTotal = FirebaseDatabase.getInstance().getReference("TotalToPay").child(uid);
        databaseReferenceTotal.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                try {
                    int currentTotal = Integer.parseInt(snapshot.child("total").getValue().toString());
                    textViewTotalPrice.setText("Total: Ksh. "+currentTotal);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return view;
    }

}

class MyListAdapter extends ArrayAdapter<String> {

    private final Activity context;
    private final Fragment fragment;
    private final ArrayList<String> names;
    private final ArrayList<String> prices;
    private final ArrayList<String> images;
    String uid;
    DatabaseReference databaseReference;

    public MyListAdapter(Activity context,Fragment fragment, ArrayList<String> names, ArrayList<String> prices,
                         ArrayList<String> images) {
        super(context, R.layout.mylist, names);
        // TODO Auto-generated constructor stub

        this.context=context;
        this.fragment=fragment;
        this.names = names;
        this.prices = prices;
        this.images = images;

    }

    @SuppressLint("SetTextI18n")
    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater=context.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.mylist, null,true);

        TextView textViewName = rowView.findViewById(R.id.textViewName);
        TextView textViewQuantity = rowView.findViewById(R.id.textViewQuantity);
        TextView textViewPrice = rowView.findViewById(R.id.textViewPrice);
        TextView textViewTotalPrice = rowView.findViewById(R.id.textViewTotalPrice);
        ImageView imageView = rowView.findViewById(R.id.imageView);
        Button buttonLess = rowView.findViewById(R.id.buttonLess);
        Button buttonAdd = rowView.findViewById(R.id.buttonAdd);

        textViewName.setText(names.get(position));
        textViewPrice.setText("Ksh. "+prices.get(position));
        textViewTotalPrice.setText("Total Price To Pay = Ksh. 0");
        Glide.with(context).load(images.get(position)).placeholder(android.R.drawable.ic_menu_gallery).into(imageView);

        uid = "testUid";
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        if (firebaseAuth.getCurrentUser() != null){
            uid = firebaseAuth.getUid();
        }
        databaseReference = FirebaseDatabase.getInstance().getReference("TotalToPay")
                .child(uid);

        buttonLess.setOnClickListener(v -> {
            int quantity = Integer.parseInt(textViewQuantity.getText().toString());
            if (quantity == 0){
                FancyToast.makeText(context.getApplicationContext(), "You can't order less than 0 items", FancyToast.LENGTH_LONG, FancyToast.ERROR, false).show();
            }else {
                textViewQuantity.setText(String.valueOf(quantity - 1));
                int itemPrice = Integer.parseInt(textViewPrice.getText().toString().split("Ksh. ")[1]);
                int itemTotalPrice = Integer.parseInt(textViewTotalPrice.getText().toString().split("Ksh. ")[1]);

                textViewTotalPrice.setText("Total Price To Pay = Ksh. "+(itemTotalPrice-itemPrice));
                ((HomeActivity) context).setTotalAmountToPay(((HomeActivity) context).getTotalAmountToPay()-itemPrice);

                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        int currentTotal = Integer.parseInt(snapshot.child("total").getValue().toString());

                        databaseReference.child("total").setValue(currentTotal-itemPrice);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });

        buttonAdd.setOnClickListener(v -> {
            int quantity = Integer.parseInt(textViewQuantity.getText().toString());
            textViewQuantity.setText(String.valueOf(quantity +1));
            int itemPrice = Integer.parseInt(textViewPrice.getText().toString().split("Ksh. ")[1]);
            int itemTotalPrice = Integer.parseInt(textViewTotalPrice.getText().toString().split("Ksh. ")[1]);

            textViewTotalPrice.setText("Total Price To Pay = Ksh. "+(itemTotalPrice+itemPrice));

            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    int currentTotal = Integer.parseInt(snapshot.child("total").getValue().toString());

                    databaseReference.child("total").setValue(currentTotal+itemPrice);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });




        });



        return rowView;

    };
}