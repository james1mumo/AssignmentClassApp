package com.james1mumo.assignmentclassapp;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.james1mumo.assignmentclassapp.ui.main.SectionsPagerAdapter;
import com.james1mumo.assignmentclassapp.databinding.ActivityHomeBinding;

public class HomeActivity extends AppCompatActivity {
    private int totalAmountToPay = 0;
    private ActivityHomeBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());
        ViewPager viewPager = binding.viewPager;
        viewPager.setAdapter(sectionsPagerAdapter);
        TabLayout tabs = binding.tabs;
        tabs.setupWithViewPager(viewPager);
        FloatingActionButton fab = binding.fab;

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        String uid = "testUid";
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        if (firebaseAuth.getCurrentUser() != null){
            uid = firebaseAuth.getUid();
        }

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("TotalToPay")
                .child(uid);
        databaseReference.child("total").setValue(0);

    }

    public void setTotalAmountToPay(int amount){
        this.totalAmountToPay = amount;
    }

    public int getTotalAmountToPay() {
        return totalAmountToPay;
    }
}