package com.flyjingfish.user

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.flyjingfish.module_communication_annotation.Route
import com.flyjingfish.module_communication_annotation.RouteParams
import com.flyjingfish.user.databinding.ActivityUserBinding

@Route("user/UserFragment")
class UserFragment : Fragment() {
    @delegate:RouteParams("params1")
    val params1 :String ? by lazy(LazyThreadSafetyMode.NONE) {
        arguments?.getString("params1")
    }

    @delegate:RouteParams("params2",nullable = true)
    val params2 :User ? by lazy(LazyThreadSafetyMode.NONE) {
        val result = arguments?.getSerializable("params2")
        if (result != null){
            result as User
        }else{
            null
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = ActivityUserBinding.inflate(inflater,container,false)
        Log.e("UserFragment","params1=$params1,params2=$params2")
        return binding.root
    }

}