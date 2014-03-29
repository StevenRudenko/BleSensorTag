package sample.ble.sensortag.gl;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ConfigurationInfo;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import rajawali.Object3D;
import sample.ble.sensortag.R;

public abstract class GlActivity extends Activity {

    private final ModelLoader loader = new ModelLoader();
    private GLSurfaceView gLView;
    private ModelRenderer renderer = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getContentViewId());

        gLView = (GLSurfaceView) findViewById(R.id.gl);

        ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        ConfigurationInfo info = am.getDeviceConfigurationInfo();
        if (info.reqGlEsVersion < 0x20000)
            throw new Error("OpenGL ES 2.0 is not supported by this device");

        renderer = new ModelRenderer(getBaseContext(), loader);
        gLView.setRenderer(renderer);
        renderer.setSurfaceView(gLView);
    }

    public abstract int getContentViewId();

    public Object3D getModel() {
        return renderer.getModel();
    }

    @Override
    protected void onResume() {
        super.onResume();
        gLView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        gLView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        renderer.onSurfaceDestroyed();
        loader.cancel();
    }
}
