package com.bee_eater.dltodlde;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class DivesSelection extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dives_selection);
    }

    public void onButtonClicked(View v){
        Intent intent=new Intent();
        intent.putExtra("MESSAGE","The lost child returns!");
        setResult(2,intent);
        finish();//finishing activity
    }
}