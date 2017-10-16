package nim.shs1330.netease.com.tasksys.dynamic_hook.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by shs1330 on 2017/10/16.
 * 傀儡Activity用于绕过AMS的验证{@link ActivityManagerService}
 */

public class StubActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
}
