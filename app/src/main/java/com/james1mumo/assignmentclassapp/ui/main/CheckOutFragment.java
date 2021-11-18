package com.james1mumo.assignmentclassapp.ui.main;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.androidstudy.daraja.Daraja;
import com.androidstudy.daraja.DarajaListener;
import com.androidstudy.daraja.model.AccessToken;
import com.androidstudy.daraja.model.LNMExpress;
import com.androidstudy.daraja.model.LNMResult;
import com.androidstudy.daraja.util.TransactionType;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.james1mumo.assignmentclassapp.HomeActivity;
import com.james1mumo.assignmentclassapp.R;
import com.shashank.sony.fancytoastlib.FancyToast;

import java.util.HashMap;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CheckOutFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CheckOutFragment extends Fragment {
    Daraja daraja;
    String CONSUMER_KEY = "V20h0w0WUJUpZBLJFufR5XuScg6czdAQ";
    String CONSUMER_SECRET = "2k7901XCA24exJ6K";
    private ProgressDialog progressDialog;
    EditText editTextMobile;
    Button buttonPay;
    String uid;

    private TextView textViewPrice;

    String amount;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public CheckOutFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CheckOutFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CheckOutFragment newInstance(String param1, String param2) {
        CheckOutFragment fragment = new CheckOutFragment();
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
            if (getArguments().containsKey("amount")) {
                amount = getArguments().getString("amount");
            }else {
//                FancyToast.makeText(getContext(), "Make Some Purchases First", FancyToast.LENGTH_LONG, FancyToast.ERROR, false).show();

            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_check_out, container, false);

        progressDialog = new ProgressDialog(getContext());

        editTextMobile = view.findViewById(R.id.editTextMobile);
        buttonPay = view.findViewById(R.id.buttonPay);

        textViewPrice = view.findViewById(R.id.textViewPrice);


        //for Mpesa
        daraja = Daraja.with(CONSUMER_KEY, CONSUMER_SECRET, new DarajaListener<AccessToken>() {
            @Override
            public void onResult(@NonNull AccessToken accessToken) {
                Log.i(getActivity().getClass().getSimpleName(), accessToken.getAccess_token());
            }

            @Override
            public void onError(String error) {
                Log.e(getActivity().getClass().getSimpleName(), error);
            }
        });

        buttonPay.setOnClickListener(v -> {
            String mobile = editTextMobile.getText().toString();

            if (mobile.isEmpty()){
                FancyToast.makeText(getContext(), "Enter Mobile number First", FancyToast.LENGTH_LONG, FancyToast.ERROR, false).show();
            }else {
                int amount=Integer.parseInt(textViewPrice.getText().toString().split("Ksh. ")[1]); String.valueOf(amount);

                if (amount==0){
                    FancyToast.makeText(getContext(), "Make some purchases first", FancyToast.LENGTH_LONG, FancyToast.ERROR, false).show();
                }else {
                    makeMpesaPayment(mobile,String.valueOf(amount));

                }

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
                    textViewPrice.setText("Total Mpesa Amount To be deducted is Ksh. "+currentTotal);
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

    private void makeMpesaPayment(String mobile, String amount){
        progressDialog.setTitle("Please Wait...");
        progressDialog.setMessage("Processing MPesa Request");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        LNMExpress lnmExpress = new LNMExpress(
                "174379",
                "bfb279f9aa9bdbcf158e97dd71a467cd2e0c893059b10f78e6b72ada1ed2c919",  //https://developer.safaricom.co.ke/test_credentials
                TransactionType.CustomerPayBillOnline,
                amount,
                mobile,
                "174379",
                mobile,
                "http://mycallbackurl.com/checkout.php",
                "Group 9 Assignment App",
                "Group 9 Assignment App"
        );

        //For both Sandbox and Production Mode
        daraja.requestMPESAExpress(lnmExpress,
                new DarajaListener<LNMResult>() {
                    @Override
                    public void onResult(@NonNull LNMResult lnmResult) {
                        Log.i(getActivity().getClass().getSimpleName(), lnmResult.ResponseDescription);
                        FancyToast.makeText(getContext(), lnmResult.ResponseDescription, FancyToast.LENGTH_LONG, FancyToast.SUCCESS, false).show();
                        progressDialog.dismiss();

                        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("TotalToPay").child(uid).child("Mpesa Payments");
                        HashMap<String ,String > hashMap = new HashMap<>();
                        hashMap.put("mobile", mobile);
                        hashMap.put("amount", amount);

                        databaseReference.push().setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                getActivity().startActivity(new Intent(getActivity(), HomeActivity.class));
                                getActivity().finish();
                            }
                        });


                    }

                    @Override
                    public void onError(String error) {
                        Log.i(getActivity().getClass().getSimpleName(), error);
                        FancyToast.makeText(getActivity(), error.toString(), FancyToast.LENGTH_LONG, FancyToast.ERROR, false).show();
                        progressDialog.dismiss();
                    }
                }
        );
    }

}