package kr.co.goms.gomszoomview;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import kr.co.goms.gomszoomview.util.GomsLog;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        GomsLog.d("aa", "aa");
    }
}
