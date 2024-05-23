package com.flyjingfish.user;

import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.flyjingfish.module_communication_annotation.Route;
import com.flyjingfish.module_communication_annotation.RouteParams;
import com.flyjingfish.user.databinding.ActivityDetailBinding;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
    @RouteParams(name = "userIds")
    private int[] userIds;
    @RouteParams(name = "userList")
    private TestBean[] userList;
    @RouteParams(name = "userIdList")
    private ArrayList<String> userIdList;
    @RouteParams(name = "userTestBean2List")
    private ArrayList<TestBean2> userTestBean2List;
    @RouteParams(name = "testBean2Array")
    private TestBean2[] testBean2Array;
    @RouteParams(name = "stringArray")
    private String[] stringArray;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityDetailBinding binding = ActivityDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        age = getIntent().getIntExtra("age",0);
        name = getIntent().getStringExtra("name");
        aChar = getIntent().getCharExtra("aChar",'s');
        user = (User) getIntent().getSerializableExtra("user");
        userIds = getIntent().getIntArrayExtra("userIds");
        userList = (TestBean[]) getIntent().getSerializableExtra("userList");
        userIdList = getIntent().getStringArrayListExtra("userIdList");
        userTestBean2List = getIntent().getParcelableArrayListExtra("userTestBean2List");
        Parcelable[] bean2Array = getIntent().getParcelableArrayExtra("testBean2Array");
        if (bean2Array != null){
            testBean2Array = new TestBean2[bean2Array.length];
            for (int i = 0; i < bean2Array.length; i++) {
                testBean2Array[i] = (TestBean2) bean2Array[i];
            }
        }

        stringArray = getIntent().getStringArrayExtra("stringArray");

        Gson gson = new Gson();


        String logText = "age="+age+",name="+name+",aChar="+aChar+",user="+user
                +",userIds="+ gson.toJson(userIds)+",userList="+gson.toJson(userList)+",userIdList="
                +userIdList+",userTestBean2List="+userTestBean2List+",testBean2Array="+gson.toJson(testBean2Array)
                +",stringArray="+gson.toJson(stringArray);
        binding.tvText.setText(logText);
        Log.e("DetailActivity",logText);

    }
}
