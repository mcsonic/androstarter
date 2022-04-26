package com.study.starter.ui.home;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.study.starter.R;
import com.study.starter.databinding.FragmentHomeBinding;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class HomeFragment extends Fragment {

    private Context context;
    private Button button1;
    private HomeViewModel homeViewModel;
    private FragmentHomeBinding binding;
    private MutableLiveData<String> newStr = new MutableLiveData<>();
    private final static String orgText = "original text";
    private final static String newText = "newly changed text";
    private final static int delayTime  = 2000;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        button1 = (Button) getView().findViewById(R.id.button1);
        button1.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                TextView textView = binding.textHome;
                if (textView.getText() == orgText) {
                    testThread(newText);
                }
                else {
                    testThread(orgText);
                }
            }
        }) ;
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        context = container.getContext();
        homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        newStr.setValue(orgText);
        homeViewModel.setText(newStr);

        final TextView textView = binding.textHome;
        homeViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
        return root;
    }

    private void testThread(String newText) {
        Executor mainExecutor = ContextCompat.getMainExecutor(context);
        ScheduledExecutorService backgroundExecutor = Executors.newSingleThreadScheduledExecutor();

        backgroundExecutor.schedule(new Runnable() {
            @Override
            public void run() {

                // Update UI on the main thread
                mainExecutor.execute(new Runnable() {
                    @Override
                    public void run() {
                        final TextView textView = binding.textHome;
                        textView.setText(newText);
                    }
                });
            }
        }, delayTime, TimeUnit.MILLISECONDS);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}