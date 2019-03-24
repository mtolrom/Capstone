package org.mekonecampus.mekonecapstone;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class AddActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        findViewById(R.id.rejectBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //mSwipeView.doSwipe(false);
                Intent intent = new Intent(v.getContext(), AddActivity.class);
                startActivity(intent);
            }
        });

        findViewById(R.id.acceptBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //mSwipeView.doSwipe(true);
                Intent intent = new Intent(v.getContext(), MainActivity.class);
                startActivity(intent);
            }
        });

        findViewById(R.id.undoBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //mSwipeView.undoLastSwipe();
                //Intent intent = new Intent(v.getContext(), MainActivity.class);
                //startActivity(intent);
            }
        });
    }
}
