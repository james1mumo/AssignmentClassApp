package com.james1mumo.assignmentclassapp.ui.main;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.james1mumo.assignmentclassapp.MainActivity;
import com.james1mumo.assignmentclassapp.R;
import com.shashank.sony.fancytoastlib.FancyToast;

import java.util.HashMap;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RegisterFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RegisterFragment extends Fragment {
    private Button buttonRegister;
    private EditText editTextFirstName, editTextSecondName, editTextEmail, editTextIdNumber, editTextAddress,
            editTextPassword, editTextPasswordConfirm;
    private RadioButton radioButtonFemale, radioButtonMale;
    private RadioGroup genderRadioGroup;
    private TextView textViewError;
    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public RegisterFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment RegisterFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static RegisterFragment newInstance(String param1, String param2) {
        RegisterFragment fragment = new RegisterFragment();
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
        View view = inflater.inflate(R.layout.fragment_register, container, false);

        editTextFirstName = view.findViewById(R.id.editTextFirstName);
        editTextSecondName = view.findViewById(R.id.editTextSecondName);
        editTextEmail = view.findViewById(R.id.editTextEmail);
        editTextIdNumber = view.findViewById(R.id.editTextIdNumber);
        editTextAddress = view.findViewById(R.id.editTextAddress);
        editTextPassword = view.findViewById(R.id.editTextPassword);
        editTextPasswordConfirm = view.findViewById(R.id.editTextPasswordConfirm);
        buttonRegister = view.findViewById(R.id.buttonSignUp);
        textViewError = view.findViewById(R.id.textViewError);

        radioButtonFemale = view.findViewById(R.id.radioButtonFemale);
        radioButtonMale = view.findViewById(R.id.radioButtonMale);
        genderRadioGroup=view.findViewById(R.id.genderRadioGroup);

        firebaseAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(getActivity());

        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                register();
            }
        });

        return view;
    }

    private void register() {
        String firstName = editTextFirstName.getText().toString();
        String secondName = editTextSecondName.getText().toString();
        String email = editTextEmail.getText().toString();
        String idNumber = editTextIdNumber.getText().toString();
        String address = editTextAddress.getText().toString();
        String password = editTextPassword.getText().toString();
        String passwordConfirm = editTextPasswordConfirm.getText().toString();

        if(TextUtils.isEmpty(firstName)){
            editTextFirstName.setError("Required");
            editTextFirstName.requestFocus();
        }else if(TextUtils.isEmpty(secondName)){
                editTextSecondName.setError("Required");
                editTextSecondName.requestFocus();
        } else if(TextUtils.isEmpty(email)){
            editTextEmail.setError("Required");
            editTextEmail.requestFocus();
        }else if (!Patterns.EMAIL_ADDRESS.matcher(editTextEmail.getText().toString()).matches()){
            FancyToast.makeText(getActivity(), "Invalid Email", FancyToast.LENGTH_LONG, FancyToast.ERROR, true).show();
            editTextEmail.requestFocus();
        } else if (!(radioButtonFemale.isChecked() || radioButtonMale.isChecked())){
            FancyToast.makeText(getActivity(), "Choose Your Gender first", FancyToast.LENGTH_LONG, FancyToast.ERROR, true).show();
        }else if(TextUtils.isEmpty(idNumber)){
            editTextIdNumber.setError("Required");
            editTextIdNumber.requestFocus();
        } else if(TextUtils.isEmpty(address)){
            editTextAddress.setError("Required");
            editTextAddress.requestFocus();
        }else if(TextUtils.isEmpty(password)){
            editTextPassword.setError("Required");
            editTextPassword.requestFocus();
        }else if(TextUtils.isEmpty(passwordConfirm)){
            editTextPasswordConfirm.setError("Required");
            editTextPasswordConfirm.requestFocus();
        }else if (!password.equals(passwordConfirm)){
            FancyToast.makeText(getActivity(), "Passwords do not match", FancyToast.LENGTH_LONG, FancyToast.ERROR, true).show();
            editTextPasswordConfirm.setError("not matching");
            editTextPasswordConfirm.requestFocus();
        }else if (password.length() <6){
            FancyToast.makeText(getActivity(), "Short password/nUse more than 6 characters", FancyToast.LENGTH_LONG, FancyToast.ERROR, true).show();
            editTextPassword.requestFocus();
        }

        else {
            progressDialog.setTitle("Please Wait...");
            progressDialog.setMessage("Creating Account");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();

            editTextFirstName.setText("");
            editTextSecondName.setText("");
            editTextEmail.setText("");
            editTextIdNumber.setText("");
            editTextAddress.setText("");
            editTextPassword.setText("");
            editTextPasswordConfirm.setText("");




            firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
                if (task.isSuccessful()){
                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");
                    String uid = firebaseAuth.getUid();
                    String gender = "";
                    if (radioButtonFemale.isChecked()) gender = "Female";
                    else if (radioButtonMale.isChecked()) gender = "Male";

                    HashMap<String ,String > hashMap = new HashMap<>();
                    hashMap.put("firstName", firstName);
                    hashMap.put("secondName", secondName);
                    hashMap.put("email", email);
                    hashMap.put("gender", gender);
                    hashMap.put("idNumber", idNumber);
                    hashMap.put("address", address);

                    if (uid != null){
                        databaseReference.child(uid).setValue(hashMap).addOnCompleteListener(task1 -> {
                            progressDialog.dismiss();
                            FancyToast.makeText(getActivity(), "Account Created Successfully", FancyToast.LENGTH_LONG, FancyToast.SUCCESS, true).show();
                            startActivity(new Intent(getActivity(), MainActivity.class));
                        });
                    }

                }
            }).addOnFailureListener(e -> {
                progressDialog.dismiss();
                FancyToast.makeText(getActivity(), "Email address is taken by another user", FancyToast.LENGTH_LONG, FancyToast.ERROR, true).show();
//                    textViewError.setText(e.toString());
//                    textViewError.requestFocus();
            });
        }
    }
}