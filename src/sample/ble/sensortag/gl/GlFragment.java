package sample.ble.sensortag.gl;

import android.app.ActivityManager;
import android.app.Fragment;
import android.content.Context;
import android.content.pm.ConfigurationInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import rajawali.Object3D;
import sample.ble.sensortag.R;

public abstract class GlFragment extends Fragment implements ModelLoader.OnModelLoadingListener {

    private final ModelLoader loader = new ModelLoader();
    private GLSurfaceView gLView;
    private ModelRenderer renderer = null;
    private View loading;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    public abstract int getContentViewId();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final Context context = getActivity();
        final View v = inflater.inflate(getContentViewId(), container, false);

        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        ConfigurationInfo info = am.getDeviceConfigurationInfo();
        if (info.reqGlEsVersion < 0x20000)
            throw new Error("OpenGL ES 2.0 is not supported by this device");

        loading = v.findViewById(R.id.loading);
        gLView = (GLSurfaceView) v.findViewById(R.id.gl);
        renderer = new ModelRenderer(context);
        renderer.setSurfaceView(gLView);
        gLView.setRenderer(renderer);

        loader.loadModel(getActivity(), this);
        return v;
    }

    public Object3D getModel() {
        return renderer.getModel();
    }

    @Override
    public void onModeLoaded(final Object3D model) {
        if (isDetached())
            return;
        if (getActivity() == null)
            return;

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                renderer.setModel(model);
                loading.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        gLView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        gLView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        renderer.onSurfaceDestroyed();
        loader.cancel();
    }
}
