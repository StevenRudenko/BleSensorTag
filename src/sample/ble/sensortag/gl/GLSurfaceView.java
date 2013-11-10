package sample.ble.sensortag.gl;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLDisplay;

/**
 * Created by steven on 9/5/13.
 */
public class GLSurfaceView extends android.opengl.GLSurfaceView {
    private static final String TAG = GLSurfaceView.class.getSimpleName();

    public GLSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);

        setEGLContextClientVersion(2);
        applyMultisampleConfig();
    }

    @Override
    public void onResume() {
        setRenderMode(android.opengl.GLSurfaceView.RENDERMODE_WHEN_DIRTY);
        super.onResume();
    }

    protected void applyMultisampleConfig() {
        final int EGL_COVERAGE_BUFFERS_NV = 0x30E0;
        final int EGL_COVERAGE_SAMPLES_NV = 0x30E1;

        setEGLConfigChooser(new android.opengl.GLSurfaceView.EGLConfigChooser() {
            public EGLConfig chooseConfig(EGL10 egl, EGLDisplay display) {
                int[] configSpec = new int[]{
                        EGL10.EGL_RED_SIZE, 5,
                        EGL10.EGL_GREEN_SIZE, 6,
                        EGL10.EGL_BLUE_SIZE, 5,
                        EGL10.EGL_DEPTH_SIZE, 16,
                        EGL10.EGL_RENDERABLE_TYPE, 4,
                        EGL10.EGL_SAMPLE_BUFFERS, 1,
                        EGL10.EGL_SAMPLES, 2,
                        EGL10.EGL_NONE
                };

                int[] result = new int[1];
                if (!egl.eglChooseConfig(display, configSpec, null, 0, result)) {
                    Log.e(TAG, "Multisampling configuration 1 failed.");
                }

                if (result[0] <= 0) {
                    // no multisampling, check for coverage multisampling
                    configSpec = new int[]{
                            EGL10.EGL_RED_SIZE, 5,
                            EGL10.EGL_GREEN_SIZE, 6,
                            EGL10.EGL_BLUE_SIZE, 5,
                            EGL10.EGL_DEPTH_SIZE, 16,
                            EGL10.EGL_RENDERABLE_TYPE, 4,
                            EGL_COVERAGE_BUFFERS_NV, 1,
                            EGL_COVERAGE_SAMPLES_NV, 2,
                            EGL10.EGL_NONE
                    };

                    if (!egl.eglChooseConfig(display, configSpec, null, 0, result)) {
                        Log.e(TAG, "Multisampling configuration 2 failed. Multisampling is not possible on your device.");
                    }

                    if (result[0] <= 0) {
                        configSpec = new int[]{
                                EGL10.EGL_RED_SIZE, 5,
                                EGL10.EGL_GREEN_SIZE, 6,
                                EGL10.EGL_BLUE_SIZE, 5,
                                EGL10.EGL_DEPTH_SIZE, 16,
                                EGL10.EGL_RENDERABLE_TYPE, 4,
                                EGL10.EGL_NONE
                        };

                        if (!egl.eglChooseConfig(display, configSpec, null, 0, result)) {
                            Log.e(TAG, "Multisampling configuration 3 failed. Multisampling is not possible on your device.");
                        }

                        if (result[0] <= 0) {
                            throw new RuntimeException("Couldn't create OpenGL config.");
                        }
                    }
                }
                EGLConfig[] configs = new EGLConfig[result[0]];
                if (!egl.eglChooseConfig(display, configSpec, configs, result[0], result)) {
                    throw new RuntimeException("Couldn't create OpenGL config.");
                }

                int index = -1;
                int[] value = new int[1];
                for (int i = 0; i < configs.length; ++i) {
                    egl.eglGetConfigAttrib(display, configs[i], EGL10.EGL_RED_SIZE, value);
                    if (value[0] == 5) {
                        index = i;
                        break;
                    }
                }

                EGLConfig config = configs.length > 0 ? configs[index] : null;
                if (config == null) {
                    throw new RuntimeException("No config chosen");
                }

                return config;
            }
        });
    }

    private void applyFallbackConfig() {
        /* commenting this out as far it causing some problems with
         * GL context creation on some devices (read: Galaxy Nexus)
        setEGLConfigChooser(new GLSurfaceView.EGLConfigChooser() {
            public EGLConfig chooseConfig(EGL10 egl, EGLDisplay display) {
                // Ensure that we get a 16bit framebuffer. Otherwise, we'll fall
                // back to Pixelflinger on some device (read: Samsung I7500)
                int[] attributes = new int[]{EGL10.EGL_DEPTH_SIZE, 16, EGL10.EGL_NONE};
                EGLConfig[] configs = new EGLConfig[1];
                int[] result = new int[1];
                egl.eglChooseConfig(display, attributes, configs, 1, result);
                return configs[0];
            }
        });
        */
    }
}
