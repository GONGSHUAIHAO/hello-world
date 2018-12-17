package com.example.gqqqqq.lorastop;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.gqqqqq.lorastop.ui.MapActivity;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button btn_map;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
    }

    private void initView() {
        btn_map = (Button)findViewById(R.id.btn_map);
        btn_map.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_map:
                startActivity(new Intent(this,MapActivity.class));
                break;
        }
    }
}
