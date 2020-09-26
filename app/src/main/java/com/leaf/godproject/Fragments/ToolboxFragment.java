package com.leaf.godproject.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.DefaultCompany.godtest1222.UnityPlayerActivity;
import com.leaf.godproject.AlarmActivity;
import com.leaf.godproject.DictionaryActivity;
import com.leaf.godproject.R;
import com.leaf.godproject.findvet.MainActivity3;

public class ToolboxFragment extends Fragment {

    private CardView cv1,cv2,cv3,cv4,cv5;
    View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_toolbox2, container, false);
        initview();

        return view;
    }

    private void initview() {
        cv1=(CardView)view.findViewById(R.id.CV1);
//        cv1.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) { startActivity(new Intent(getContext(), MapsActivityCurrentPlace.class)); }
//        });
//        cv1.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) { startActivity(new Intent(getContext(),
//                    FindvetActivity4.class)); }
//        });
        cv1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { startActivity(new Intent(getContext(),
                    MainActivity3.class)); }
        });
        cv2=(CardView)view.findViewById(R.id.CV2);
        cv2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { startActivity(new Intent(getContext(), AlarmActivity.class)); }
        });
        cv3=(CardView)view.findViewById(R.id.CV3);
        cv3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { startActivity(new Intent(getContext(), DictionaryActivity.class)); }
        });
        cv4=(CardView)view.findViewById(R.id.CV4);
        cv4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { startActivity(new Intent(getContext(), UnityPlayerActivity.class)); }
        });
        cv5=(CardView)view.findViewById(R.id.CV5);
        cv5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                ((FragmentActivity)getContext()).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new SearchFragment()).commit();
//                FragmentManager fragmentmanager=((FragmentActivity)getContext()).getSupportFragmentManager();
//                fragmentmanager.beginTransaction()
//                        .replace(R.id.fragment_container, new SearchFragment())
//                        .addToBackStack(fragmentmanager.getClass().getName())
//                        .commit();
            }
        });
    }

}
