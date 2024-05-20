package com.flyjingfish.user;

import androidx.appcompat.app.AppCompatActivity;

import com.flyjingfish.module_communication_annotation.Route;
import com.flyjingfish.module_communication_annotation.RouteParams;

@Route(path = "user/UserActivity2")
public class UserActivity2 extends AppCompatActivity {
    @RouteParams(name = "age")
    private int age;
}
