package com.hado.facebookemotion;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    Button btnLike;
    ReactionView reactionView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView() {
        btnLike = (Button) findViewById(R.id.btn_like);
        reactionView = (ReactionView) findViewById(R.id.view_reaction);
        reactionView.setVisibility(View.INVISIBLE);

        btnLike.setOnClickListener(view -> reactionView.show());
    }
}
