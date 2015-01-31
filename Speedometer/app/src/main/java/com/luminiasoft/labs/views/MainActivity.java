/*
 * Copyright (c) 2015.
 *        bilthon https://github.com/bilthon/Android-Speedometer
 *        Prat
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.luminiasoft.labs.views;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MainActivity extends Activity {

    private static final String TAG = "MainActivity";

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public void onStart() {
        super.onStart();
        final Speedometer speedometer = (Speedometer) findViewById(R.id.Speedometer);
        Button increaseSpeed = (Button) findViewById(R.id.IncreaseSpeed);
        Button decreaseSpeed = (Button) findViewById(R.id.DecreaseSpeed);
        increaseSpeed.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View view) {
                speedometer.onSpeedChanged(speedometer.getCurrentSpeed() + 8);
            }

        });
        decreaseSpeed.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                speedometer.onSpeedChanged(speedometer.getCurrentSpeed() - 8);
            }
        });
    }
}
