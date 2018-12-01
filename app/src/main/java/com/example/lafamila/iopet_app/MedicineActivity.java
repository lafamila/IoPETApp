package com.example.lafamila.iopet_app;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import com.example.lafamila.iopet_app.util.Util;

public class MedicineActivity extends AppCompatActivity {
    Button save;
    EditText name;
    CheckBox check1, check2, check3;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medicine);

        save = (Button)findViewById(R.id.btn_saveMedicine);
        name = (EditText)findViewById(R.id.edit_medicineName);
        check1 = (CheckBox)findViewById(R.id.check_1);
        check2 = (CheckBox)findViewById(R.id.check_2);
        check3 = (CheckBox)findViewById(R.id.check_3);

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.putExtra("name", name.getText().toString());
                intent.putExtra("morning", check1.isChecked());
                intent.putExtra("lunch", check2.isChecked());
                intent.putExtra("dinner", check3.isChecked());
                setResult(Util.ADD_MEDICINE, intent);
                finish();
            }
        });
    }
}
