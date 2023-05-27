package com.example.detector;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class result_fragment extends DialogFragment {
    Button res;
    TextView restext;
    String result;
    public final static String FINAL_TEXT="FINAL_TEXT";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v=inflater.inflate(R.layout.result_dialouge,container,false);
        result="";
        res=v.findViewById(R.id.btnid);
        restext=v.findViewById(R.id.result);
        Bundle bundle=getArguments();
        if(bundle!=null) {
            result = bundle.getString(FINAL_TEXT);
            restext.setText(result);
        }else {
            restext.setText("Somthing went wrong");
        }
        res.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        return v;
    }
}
