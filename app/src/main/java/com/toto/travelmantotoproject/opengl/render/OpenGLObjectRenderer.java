package com.toto.travelmantotoproject.opengl.render;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES30;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;

import com.toto.travelmantotoproject.OpenGLObject;

import java.io.IOException;
import java.io.InputStream;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class OpenGLObjectRenderer implements GLSurfaceView.Renderer {

    private Context context;
    private OpenGLObject openGLObject;
    private String objFile;
    // Texture handle
    private int mBaseMapTexId;
    private int mBaseMapLoc;

    private int mProgramObject;


    public OpenGLObjectRenderer(Context context, String objFile) {
        this.context = context;
        this.objFile = objFile;
    }

    @Override
    public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {

        mBaseMapTexId = loadTextureFromAsset ( "basemap.png" );


        GLES30.glClearColor(0f, 0f, 0f, 0.0f);

       this.openGLObject = new OpenGLObject(context,objFile);


        mBaseMapLoc = GLES30.glGetUniformLocation ( mProgramObject, "s_baseMap" );
    }

    @Override
    public void onSurfaceChanged(GL10 gl10, int i, int i1) {

        GLES30.glViewport(0,0, i, i1);
    }

    @Override
    public void onDrawFrame(GL10 gl10) {

        GLES30.glClearColor(0f, 0f, 0f, 0.0f);

        // Clear the color buffer  set above by glClearColor.
        GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT | GLES30.GL_DEPTH_BUFFER_BIT);


        //need this otherwise, it will over right stuff and the cube will look wrong!
        GLES30.glEnable(GLES30.GL_DEPTH_TEST);

        GLES30.glActiveTexture ( GLES30.GL_TEXTURE0 );
        GLES30.glBindTexture ( GLES30.GL_TEXTURE_2D, mBaseMapTexId );


        // Bind the base map
        GLES30.glActiveTexture ( GLES30.GL_TEXTURE0 );
        GLES30.glBindTexture ( GLES30.GL_TEXTURE_2D, mBaseMapTexId );

        // Set the base map sampler to texture unit to 0
        GLES30.glUniform1i ( mBaseMapLoc, 0 );

        this.openGLObject.draw();

    }



    ///
    //  Load texture from asset
    //
    private int loadTextureFromAsset ( String fileName )
    {
        int[] textureId = new int[1];
        Bitmap bitmap = null;
        InputStream is = null;

        try
        {
            is = context.getAssets().open ( fileName );
        }
        catch ( IOException ioe )
        {
            is = null;
        }

        if ( is == null )
        {
            return 0;
        }

        bitmap = BitmapFactory.decodeStream ( is );

        GLES30.glGenTextures ( 1, textureId, 0 );
        GLES30.glBindTexture ( GLES30.GL_TEXTURE_2D, textureId[0] );

        GLUtils.texImage2D ( GLES30.GL_TEXTURE_2D, 0, bitmap, 0 );

        GLES30.glTexParameteri ( GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MIN_FILTER, GLES30.GL_LINEAR );
        GLES30.glTexParameteri ( GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MAG_FILTER, GLES30.GL_LINEAR );
        GLES30.glTexParameteri ( GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_S, GLES30.GL_CLAMP_TO_EDGE );
        GLES30.glTexParameteri ( GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_T, GLES30.GL_CLAMP_TO_EDGE );

        return textureId[0];
    }
}
