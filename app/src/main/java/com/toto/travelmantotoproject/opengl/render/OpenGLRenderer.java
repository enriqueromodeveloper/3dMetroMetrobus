package com.toto.travelmantotoproject.opengl.render;

import android.content.Context;
import android.graphics.Point;
import android.opengl.GLES20;
import android.opengl.GLES30;
import android.opengl.GLSurfaceView.Renderer;
import android.opengl.Matrix;
import android.util.Log;

import com.google.android.gms.maps.Projection;
import com.toto.travelmantotoproject.model.Station;
import com.toto.travelmantotoproject.model.StationLogoModel;
import com.toto.travelmantotoproject.model.TransportingLine;
import com.toto.travelmantotoproject.model.TransportingSystem;
import java.util.Arrays;
import java.util.List;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class OpenGLRenderer implements Renderer {

    private static String TAG = "OpenGLRenderer";
    private final float[] projectionMatrix = new float[16];
    private final float[] viewMatrix = new float[16];
    private List<TransportingSystem> transportingList;
    private Context context;
    private Projection projection;
    private float mAngle = 0;
    private float zoom;
    private int mWidth;
    private int mHeight;


    public OpenGLRenderer(Context context, TransportingSystem... transportingSystems) {
        this.context = context;
        transportingList = Arrays.asList(transportingSystems);
    }


    @Override
    public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {

        GLES30.glClearColor(0f, 0f, 0f, 0.0f);

        for (TransportingSystem transportingSystem : transportingList) {
            for (TransportingLine transportingLine : transportingSystem.getLines()) {
                for (Station station : transportingLine.getStations()) {
                    station.setStationLogoModel(new StationLogoModel(context, station.getName(),
                            station.getLatLng(),
                            context.getResources().getIdentifier(station.getName(), "drawable",
                                    context.getPackageName())));
                    station.setMiniStationLogoModel(new StationLogoModel(context, station.getName(),
                            station.getLatLng(),
                            context.getResources().getIdentifier(station.getName(), "drawable",
                                    context.getPackageName()), 0.03f));

                    station.setTinyStationLogoModel(new StationLogoModel(context, station.getName(),
                            station.getLatLng(),
                            context.getResources().getIdentifier(station.getName(), "drawable",
                                    context.getPackageName()), 0.01f));
                }
            }

        }


    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        mWidth = width;
        mHeight = height;
        GLES30.glViewport(0, 0, width, height);
        float ratio = (float) width / height;
        Matrix.orthoM(projectionMatrix, 0, -ratio, ratio, -1, 1, -2, 2);
    }

    @Override
    public void onDrawFrame(GL10 gl) {

        GLES20.glClearColor(0f, 0f, 0f, 0.0f);

        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);
        Matrix.setLookAtM(viewMatrix, 0, 0, 0, 1, 0f, 0f,
                0f, 0f, 1.0f, 0.0f);


        if (projection != null) {
            for (TransportingSystem transportingSystem : transportingList) {
                if (transportingSystem.isEnable()) {
                    for (TransportingLine transportingLine : transportingSystem.getLines()) {
                        for (Station station : transportingLine.getStations()) {


                            Point screenPosition = projection.toScreenLocation(station.getLatLng());

                            float[] oglPos = getWorldCoords(screenPosition.x, screenPosition.y,
                                    viewMatrix, projectionMatrix);
                            float[] vPMatrix = new float[16];
                            Matrix.multiplyMM(vPMatrix, 0, projectionMatrix, 0,
                                    viewMatrix, 0);
                            Matrix.translateM(vPMatrix, 0, oglPos[0], oglPos[1], 0f);

                            float[] mRotationMatrix = new float[16];
                            Matrix.setIdentityM(mRotationMatrix, 0);

                            //mangle is how fast, x,y,z which directions it rotates.
                            Matrix.rotateM(mRotationMatrix, 0, mAngle, 0.4f, 1.0f, 0.6f);

                            Matrix.multiplyMM(vPMatrix, 0, vPMatrix, 0,
                                    mRotationMatrix, 0);
                            if (zoom > 11) {
                                if (zoom >= 11 && zoom < 13.5) {
                                    station.getTinyStationLogoModel().draw(vPMatrix);
                                } else if (zoom >= 13.5 && zoom < 17) {
                                    station.getMiniStationLogoModel().draw(vPMatrix);
                                } else if (zoom >= 17) {
                                    station.getStationLogoModel().draw(vPMatrix);
                                }

                            }
                        }
                    }
                }
            }

            mAngle += .4;
        }


    }

    public float[] getWorldCoords(int x, int y, float[] modelview, float[] projection) {
        // Initialize auxiliary variables.
        float[] worldPos = new float[2];

        // SCREEN height & width (ej: 320 x 480)
        float screenW = mWidth;
        float screenH = mHeight;

        // Auxiliary matrix and vectors
        // to deal with ogl.
        float[] invertedMatrix, transformMatrix,
                normalizedInPoint, outPoint;
        invertedMatrix = new float[16];
        transformMatrix = new float[16];
        normalizedInPoint = new float[4];
        outPoint = new float[4];

        // Invert y coordinate, as android uses
        // top-left, and ogl bottom-left.
        int oglTouchY = (int) (screenH - y);

       /* Transform the screen point to clip
       space in ogl (-1,1) */
        normalizedInPoint[0] =
                (float) ((x) * 2.0f / screenW - 1.0);
        normalizedInPoint[1] =
                (float) ((oglTouchY) * 2.0f / screenH - 1.0);
        normalizedInPoint[2] = -1.0f;
        normalizedInPoint[3] = 1.0f;

       /* Obtain the transform matrix and
       then the inverse. */

        Matrix.multiplyMM(
                transformMatrix, 0,
                projection, 0,
                modelview, 0);
        Matrix.invertM(invertedMatrix, 0,
                transformMatrix, 0);

       /* Apply the inverse to the point
       in clip space */
        Matrix.multiplyMV(
                outPoint, 0,
                invertedMatrix, 0,
                normalizedInPoint, 0);

        if (outPoint[3] == 0.0) {
            // Avoid /0 error.
            Log.e("World coords", "ERROR!");
            return worldPos;
        }

        // Divide by the 3rd component to find
        // out the real position.
        worldPos[0] = outPoint[0] / outPoint[3];
        worldPos[1] = outPoint[1] / outPoint[3];

        return worldPos;
    }

    public static int LoadShader(int type, String shaderSrc) {
        int shader;
        int[] compiled = new int[1];

        // Create the shader object
        shader = GLES30.glCreateShader(type);

        if (shader == 0) {
            return 0;
        }

        // Load the shader source
        GLES30.glShaderSource(shader, shaderSrc);

        // Compile the shader
        GLES30.glCompileShader(shader);

        // Check the compile status
        GLES30.glGetShaderiv(shader, GLES30.GL_COMPILE_STATUS, compiled, 0);

        if (compiled[0] == 0) {
            Log.e(TAG, "Erorr!!!!");
            Log.e(TAG, GLES30.glGetShaderInfoLog(shader));
            GLES30.glDeleteShader(shader);
            return 0;
        }

        return shader;
    }

    public void setProjection(Projection projection) {
        this.projection = projection;
    }

    public void setZoom(float zoom) {
        this.zoom = zoom;
    }
}
