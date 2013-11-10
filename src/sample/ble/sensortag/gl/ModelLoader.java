package sample.ble.sensortag.gl;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import rajawali.Object3D;
import rajawali.materials.Material;
import rajawali.materials.methods.DiffuseMethod;
import rajawali.materials.methods.SpecularMethod;
import rajawali.materials.textures.TextureManager;
import rajawali.parser.LoaderOBJ;
import rajawali.parser.ParsingException;
import sample.ble.sensortag.R;

import java.util.HashMap;

/**
 * Created by steven on 10/15/13.
 */
public class ModelLoader {
    private static final String TAG = ModelLoader.class.getSimpleName();

    public interface OnModelLoadingListener {
        public void onModeLoaded(Object3D model);
    }

    private static final String NAME_LID = "lid";
    private static final String NAME_PANEL = "panel";
    private static final String NAME_JACKET = "jacket";
    private static final String NAME_CASING = "casing";
    private static final String NAME_CUBE = "cube";
    private static final HashMap<String, Material> MATERIALS;

    static {
        final Material jacket = new Material();
        {
            final SpecularMethod.Phong method = new SpecularMethod.Phong();
            method.setSpecularColor(getColor(0.1f, 0.1f, 0.1f));
            method.setShininess(50f);

            jacket.enableLighting(true);
            jacket.setDiffuseMethod(new DiffuseMethod.Lambert());
            jacket.setColor(getColor(.5f, 0f, 0f));
            jacket.setSpecularMethod(method);
        }

        final Material plane = new Material();
        {
            final SpecularMethod.Phong method = new SpecularMethod.Phong();
            method.setSpecularColor(getColor(0f, 0f, 0f));
            method.setShininess(50f);

            plane.enableLighting(true);
            plane.setDiffuseMethod(new DiffuseMethod.Lambert());
            plane.setColor(getColor(.03883f, .281f, .14f));
            plane.setSpecularMethod(method);
        }

        final Material casing = new Material();
        {
            final SpecularMethod.Phong method = new SpecularMethod.Phong();
            method.setSpecularColor(getColor(0.05f, 0.05f, 0.05f));
            method.setShininess(50f);

            casing.enableLighting(true);
            casing.setDiffuseMethod(new DiffuseMethod.Lambert());
            casing.setColor(getColor(.0f, 0f, 0f));
            casing.setSpecularMethod(method);
        }

        final Material lid = new Material();
        {
            final SpecularMethod.Phong method = new SpecularMethod.Phong();
            method.setSpecularColor(getColor(1f, 1f, 1f));
            method.setShininess(200f);

            lid.enableLighting(true);
            lid.setDiffuseMethod(new DiffuseMethod.Lambert(1f));
            lid.setColor(getColor(.3f, .1f, .1f, .1f));
            lid.setSpecularMethod(method);
        }

        final Material cube = new Material();
        {
            final SpecularMethod.Phong method = new SpecularMethod.Phong();
            method.setSpecularColor(getColor(0f, 0f, 0f));
            method.setShininess(50f);

            cube.enableLighting(true);
            cube.setDiffuseMethod(new DiffuseMethod.Lambert());
            cube.setColor(getColor(0f, 0f, 0f));
            cube.setSpecularMethod(method);
        }

        MATERIALS = new HashMap<String, Material>();
        MATERIALS.put(NAME_PANEL, plane);
        MATERIALS.put(NAME_JACKET, jacket);
        MATERIALS.put(NAME_CASING, casing);
        MATERIALS.put(NAME_LID, lid);
        MATERIALS.put(NAME_CUBE, cube);
    }

    private Thread loaderThread = null;

    public void cancel() {
        if (loaderThread != null) {
            loaderThread.interrupt();
            loaderThread = null;
        }
    }

    public void loadModel(Context context, final OnModelLoadingListener listener) {
        if (loaderThread != null)
            return;

        loaderThread = new Thread(new ModelLoaderRunnable(context, listener));
        loaderThread.setName(TAG);
        loaderThread.start();
    }

    private static int getColor(float r, float g, float b) {
        return Color.rgb((int) (255f * r), (int) (255f * g), (int) (255f * b));
    }

    private static int getColor(float r, float g, float b, float a) {
        return Color.argb((int) (255f * a), (int) (255f * r), (int) (255f * g), (int) (255f * b));
    }

    private static class ModelLoaderRunnable implements Runnable {
        private final Context context;
        private final OnModelLoadingListener listener;

        private ModelLoaderRunnable(Context context, OnModelLoadingListener listener) {
            this.context = context;
            this.listener = listener;
        }

        @Override
        public void run() {
            try {
                LoaderOBJ objParser = new LoaderOBJ(context.getResources(),
                        TextureManager.getInstance(), R.raw.sensortag);
                objParser.parse();
                final Object3D model = objParser.getParsedObject();
                // setting dumb material
                model.setMaterial(new Material());
                // setting materials for object parts
                model.getChildByName(NAME_PANEL).setMaterial(MATERIALS.get(NAME_PANEL));
                model.getChildByName(NAME_CUBE).setMaterial(MATERIALS.get(NAME_CUBE));
                model.getChildByName(NAME_CASING).setMaterial(MATERIALS.get(NAME_CASING));
                model.getChildByName(NAME_JACKET).setMaterial(MATERIALS.get(NAME_JACKET));

                final Object3D lid = model.getChildByName(NAME_LID);
                lid.setMaterial(MATERIALS.get(NAME_LID));
                lid.setColor(0x66ffffff);
                lid.setTransparent(true);

                if (listener != null)
                    listener.onModeLoaded(model);
            } catch (ParsingException e) {
                Log.e(TAG, "Fail to load model", e);
            }
        }
    }
}
