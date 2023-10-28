package com.pizza.tools.app;

import android.os.Bundle;
import android.os.Environment;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.pizza.tools.log.LogTool;

/**
 * @author Kyle
 * 2023/8/28 15:49
 *
 */
public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        String path = getExternalFilesDirs(Environment.DIRECTORY_MUSIC)[0].getAbsolutePath();
        String sdcard = path.substring(0, path.indexOf("/Android"));
        LogTool.i(TAG, sdcard);
    }
}
