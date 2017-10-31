package nim.shs1330.netease.com.tasksys;


import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by shs1330 on 2017/10/27.
 */

public class StubFragment extends Fragment {
    private Fragment remoteFragment;

    public void setRemoteFragment(Fragment o) {
        remoteFragment = o;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        remoteFragment.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (remoteFragment != null) {
            return remoteFragment.onCreateView(inflater, null, savedInstanceState);
        } else
            return null;
    }

    @Override
    public void onDestroy() {
        remoteFragment.onDestroy();
        super.onDestroy();
    }
}
