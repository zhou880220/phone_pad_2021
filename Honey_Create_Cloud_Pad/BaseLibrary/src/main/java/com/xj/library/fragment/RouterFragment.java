package com.xj.library.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.SparseArray;


import androidx.fragment.app.Fragment;

import java.util.Random;

/**
 * Created by jun xu on 19-3-18.
 */
public class RouterFragment extends Fragment {

    private SparseArray<ActivityResultHelper.Callback> mCallbacks = new SparseArray<>();
    private Random mCodeGenerator = new Random();

    public RouterFragment() {
        // Required empty public constructor
    }

    public static RouterFragment newInstance() {
        return new RouterFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    public void startActivityForResult(Intent intent, ActivityResultHelper.Callback callback) {
        int requestCode = makeRequestCode();
        mCallbacks.put(requestCode, callback);
        startActivityForResult(intent, requestCode);
    }

    private int makeRequestCode() {
        int requestCode;
        int tryCount = 0;
        do {
            requestCode = mCodeGenerator.nextInt(0x0000FFFF);
            tryCount++;
        } while (mCallbacks.indexOfKey(requestCode) >= 0 && tryCount < 10);
        return requestCode;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ActivityResultHelper.Callback callback = mCallbacks.get(requestCode);
        mCallbacks.remove(requestCode);
        if (callback != null) {
            callback.onActivityResult(resultCode, data);
        }
    }
}
