package com.flyjingfish.user;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.flyjingfish.module_communication_annotation.Route;
import com.flyjingfish.module_communication_annotation.RouteParams;
import com.flyjingfish.user.databinding.ActivityDetailBinding;

@Route(path = "/user/DetailActivity")
public class DetailActivity extends AppCompatActivity {
    @RouteParams(name = "age")
    private int age;
    @RouteParams(name = "name")
    private String name;
    @RouteParams(name = "aChar")
    private char aChar = 's';
    @RouteParams(name = "user")
    private User user;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityDetailBinding binding = ActivityDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        age = getIntent().getIntExtra("age",0);
        name = getIntent().getStringExtra("name");
        aChar = getIntent().getCharExtra("aChar",'s');
        user = (User) getIntent().getSerializableExtra("user");

        String logText = "age="+age+",name="+name+",aChar="+aChar+",user="+user;
        binding.tvText.setText(logText);
        Log.e("DetailActivity",logText);

    }
}
