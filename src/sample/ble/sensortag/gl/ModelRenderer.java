package sample.ble.sensortag.gl;

import android.content.Context;
import android.graphics.Color;

import rajawali.Object3D;
import rajawali.lights.PointLight;
import rajawali.renderer.RajawaliRenderer;

public class ModelRenderer extends RajawaliRenderer implements ModelLoader.OnModelLoadingListener {
    private Object3D model;

    public ModelRenderer(Context context, ModelLoader demoModelManager) {
        super(context);
        setFrameRate(60);

        getCurrentScene().setBackgroundColor(Color.WHITE);
        demoModelManager.loadModel(context, this);
    }

    public Object3D getModel() {
        return model;
    }

    protected void initScene(Object3D model) {
        if (model == null)
            return;

        this.model = model;

        final PointLight light = new PointLight();
        light.setPosition(0, 2, -10);
        light.setPower(10);
        light.setLookAt(model.getPosition());

        getCurrentScene().addLight(light);
        getCurrentScene().addChild(model);

        getCurrentCamera().setZ(-30);
        getCurrentCamera().setLookAt(model.getPosition());
    }

    @Override
    public void onModeLoaded(Object3D model) {
        initScene(model);
    }
}