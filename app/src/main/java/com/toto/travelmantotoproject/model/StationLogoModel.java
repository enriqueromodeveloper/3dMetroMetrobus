package com.toto.travelmantotoproject.model;




import android.content.Context;
import android.graphics.Bitmap;
import android.opengl.GLES20;

import com.google.android.gms.maps.model.LatLng;
import com.toto.travelmantotoproject.R;
import com.toto.travelmantotoproject.common.ShaderHelper;
import com.toto.travelmantotoproject.common.TextureHelper;
import com.toto.travelmantotoproject.opengl.render.OpenGLRenderer;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.Objects;

public class StationLogoModel {
    private int mProgramObject;
    private int mMVPMatrixHandle;
    private int glHCoordinate, glVTexture, mPositionHandle;
    private final FloatBuffer mCubePositions;
    private final FloatBuffer mCubeNormals;
    private final FloatBuffer mCubeTextureCoordinates;
    private final FloatBuffer mCubeTextureCoordinatesForPlane;
    private Bitmap mBitmap;
    private String name;
    /**
     * How many bytes per float.
     */
    private final int mBytesPerFloat = 4;
    /**
     * This is a handle to our cube shading program.
     */
    private int mProgramHandle;

    /**
     * These are handles to our texture data.
     */
    private int mLogoHandle;

    /**
     * Size of the position data in elements.
     */
    private final int mPositionDataSize = 3;
    /**
     * Temporary place to save the min and mag filter, in case the activity was restarted.
     */
    private int mQueuedMinFilter;
    private int mQueuedMagFilter;
    //initial size of the cube.  set here, so it is easier to change later.
    /** This will be used to pass in the texture. */
    private int mTextureUniformHandle;

    /** This will be used to pass in model texture coordinate information. */
    private int mTextureCoordinateHandle;

    /** Size of the texture coordinate data in elements. */
    private final int mTextureCoordinateDataSize = 2;
    private float size = 0.1f;

     private  float[] cubePositionData =
            {
                    // Front face
                    -size, size, size,
                    -size, -size, size,
                    size, size, size,
                    -size, -size, size,
                    size, -size, size,
                    size, size, size,

                    // Right face
                    size, size, size,
                    size, -size, size,
                    size, size, -size,
                    size, -size, size,
                    size, -size, -size,
                    size, size, -size,

                    // Back face
                    size, size, -size,
                    size, -size, -size,
                    -size, size, -size,
                    size, -size, -size,
                    -size, -size, -size,
                    -size, size, -size,

                    // Left face
                    -size, size, -size,
                    -size, -size, -size,
                    -size, size, size,
                    -size, -size, -size,
                    -size, -size, size,
                    -size, size, size,

                    // Top face
                    -size, size, -size,
                    -size, size, size,
                    size, size, -size,
                    -size, size, size,
                    size, size, size,
                    size, size, -size,

                    // Bottom face
                    size, -size, -size,
                    size, -size, size,
                    -size, -size, -size,
                    size, -size, size,
                    -size, -size, size,
                    -size, -size, -size,
            };

    final float[] cubeNormalData =
            {
                    // Front face
                    0.0f, 0.0f, 1.0f,
                    0.0f, 0.0f, 1.0f,
                    0.0f, 0.0f, 1.0f,
                    0.0f, 0.0f, 1.0f,
                    0.0f, 0.0f, 1.0f,
                    0.0f, 0.0f, 1.0f,

                    // Right face
                    1.0f, 0.0f, 0.0f,
                    1.0f, 0.0f, 0.0f,
                    1.0f, 0.0f, 0.0f,
                    1.0f, 0.0f, 0.0f,
                    1.0f, 0.0f, 0.0f,
                    1.0f, 0.0f, 0.0f,

                    // Back face
                    0.0f, 0.0f, -1.0f,
                    0.0f, 0.0f, -1.0f,
                    0.0f, 0.0f, -1.0f,
                    0.0f, 0.0f, -1.0f,
                    0.0f, 0.0f, -1.0f,
                    0.0f, 0.0f, -1.0f,

                    // Left face
                    -1.0f, 0.0f, 0.0f,
                    -1.0f, 0.0f, 0.0f,
                    -1.0f, 0.0f, 0.0f,
                    -1.0f, 0.0f, 0.0f,
                    -1.0f, 0.0f, 0.0f,
                    -1.0f, 0.0f, 0.0f,

                    // Top face
                    0.0f, 1.0f, 0.0f,
                    0.0f, 1.0f, 0.0f,
                    0.0f, 1.0f, 0.0f,
                    0.0f, 1.0f, 0.0f,
                    0.0f, 1.0f, 0.0f,
                    0.0f, 1.0f, 0.0f,

                    // Bottom face
                    0.0f, -1.0f, 0.0f,
                    0.0f, -1.0f, 0.0f,
                    0.0f, -1.0f, 0.0f,
                    0.0f, -1.0f, 0.0f,
                    0.0f, -1.0f, 0.0f,
                    0.0f, -1.0f, 0.0f
            };

    final float[] cubeTextureCoordinateData =
            {
                    // Front face
                    0.0f, 0.0f,
                    0.0f, 1.0f,
                    1.0f, 0.0f,
                    0.0f, 1.0f,
                    1.0f, 1.0f,
                    1.0f, 0.0f,

                    // Right face
                    0.0f, 0.0f,
                    0.0f, 1.0f,
                    1.0f, 0.0f,
                    0.0f, 1.0f,
                    1.0f, 1.0f,
                    1.0f, 0.0f,

                    // Back face
                    0.0f, 0.0f,
                    0.0f, 1.0f,
                    1.0f, 0.0f,
                    0.0f, 1.0f,
                    1.0f, 1.0f,
                    1.0f, 0.0f,

                    // Left face
                    0.0f, 0.0f,
                    0.0f, 1.0f,
                    1.0f, 0.0f,
                    0.0f, 1.0f,
                    1.0f, 1.0f,
                    1.0f, 0.0f,

                    // Top face
                    0.0f, 0.0f,
                    0.0f, 1.0f,
                    1.0f, 0.0f,
                    0.0f, 1.0f,
                    1.0f, 1.0f,
                    1.0f, 0.0f,

                    // Bottom face
                    0.0f, 0.0f,
                    0.0f, 1.0f,
                    1.0f, 0.0f,
                    0.0f, 1.0f,
                    1.0f, 1.0f,
                    1.0f, 0.0f
            };

    final float[] cubeTextureCoordinateDataForPlane =
            {
                    // Front face
                    0.0f, 0.0f,
                    0.0f, 25.0f,
                    25.0f, 0.0f,
                    0.0f, 25.0f,
                    25.0f, 25.0f,
                    25.0f, 0.0f,

                    // Right face
                    0.0f, 0.0f,
                    0.0f, 25.0f,
                    25.0f, 0.0f,
                    0.0f, 25.0f,
                    25.0f, 25.0f,
                    25.0f, 0.0f,

                    // Back face
                    0.0f, 0.0f,
                    0.0f, 25.0f,
                    25.0f, 0.0f,
                    0.0f, 25.0f,
                    25.0f, 25.0f,
                    25.0f, 0.0f,

                    // Left face
                    0.0f, 0.0f,
                    0.0f, 25.0f,
                    25.0f, 0.0f,
                    0.0f, 25.0f,
                    25.0f, 25.0f,
                    25.0f, 0.0f,

                    // Top face
                    0.0f, 0.0f,
                    0.0f, 25.0f,
                    25.0f, 0.0f,
                    0.0f, 25.0f,
                    25.0f, 25.0f,
                    25.0f, 0.0f,

                    // Bottom face
                    0.0f, 0.0f,
                    0.0f, 25.0f,
                    25.0f, 0.0f,
                    0.0f, 25.0f,
                    25.0f, 25.0f,
                    25.0f, 0.0f
            };


    //vertex shader code
    private static  final String vShaderStr =
                    "uniform mat4 uMVPMatrix;     \n"
                    + "attribute vec4 vPosition;           \n"
                            + "attribute vec2 a_TexCoordinate;           \n"
                            + "varying vec2 v_TexCoordinate;         \n"
                    + "void main()                  \n"
                    + "{                            \n"
                    + "   gl_Position = uMVPMatrix * vPosition;  \n"
                            + "   v_TexCoordinate = a_TexCoordinate;  \n"
                    + "}                            \n";
    //fragment shader code.
    private static  final String fShaderStr = "precision mediump float;					  	\n"
                    + "uniform sampler2D u_Texture;	 			 		  	\n"
                    + "varying vec2 v_TexCoordinate; 			 		  	\n"
                    + "void main()                                  \n"
                    + "{                                            \n"
                    + "  gl_FragColor = texture2D(u_Texture, v_TexCoordinate);                    	\n"
                    + "}                                            \n";

    String TAG = "Estation";

    private LatLng latLng;

    public StationLogoModel(Context context,String name, LatLng latLng, final int logo) {
        //first setup the mVertices correctly.
        // Initialize the buffers.

        this.latLng = latLng;
        mCubePositions = ByteBuffer.allocateDirect(cubePositionData.length * mBytesPerFloat)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        mCubePositions.put(cubePositionData).position(0);

        mCubeNormals = ByteBuffer.allocateDirect(cubeNormalData.length * mBytesPerFloat)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        mCubeNormals.put(cubeNormalData).position(0);

        mCubeTextureCoordinates = ByteBuffer.allocateDirect(cubeTextureCoordinateData.length * mBytesPerFloat)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        mCubeTextureCoordinates.put(cubeTextureCoordinateData).position(0);

        mCubeTextureCoordinatesForPlane = ByteBuffer.allocateDirect(cubeTextureCoordinateDataForPlane.length * mBytesPerFloat)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        mCubeTextureCoordinatesForPlane.put(cubeTextureCoordinateDataForPlane).position(0);

        //setup the shaders
        int vertexShader;
        int fragmentShader;
        // Load the vertex/fragment shaders
        vertexShader = OpenGLRenderer.LoadShader(GLES20.GL_VERTEX_SHADER, vShaderStr);
        fragmentShader = OpenGLRenderer.LoadShader(GLES20.GL_FRAGMENT_SHADER, fShaderStr);

        mProgramHandle = ShaderHelper.createAndLinkProgram(vertexShader, fragmentShader,
                new String[] {"vPosition", "a_TexCoordinate"});



        // Load the texture
        mLogoHandle = TextureHelper.loadTexture(context, logo);
        GLES20.glGenerateMipmap(GLES20.GL_TEXTURE_2D);


        if (mQueuedMinFilter != 0)
        {
            setMinFilter(mQueuedMinFilter);
        }

        if (mQueuedMagFilter != 0)
        {
            setMagFilter(mQueuedMagFilter);
        }
        this.name = name;
    }

    public StationLogoModel(Context context,String name, LatLng latLng, final int logo,float size) {
        //first setup the mVertices correctly.
        // Initialize the buffers.
        float[] cubePositionData =
                {
                        // Front face
                        -size, size, size,
                        -size, -size, size,
                        size, size, size,
                        -size, -size, size,
                        size, -size, size,
                        size, size, size,

                        // Right face
                        size, size, size,
                        size, -size, size,
                        size, size, -size,
                        size, -size, size,
                        size, -size, -size,
                        size, size, -size,

                        // Back face
                        size, size, -size,
                        size, -size, -size,
                        -size, size, -size,
                        size, -size, -size,
                        -size, -size, -size,
                        -size, size, -size,

                        // Left face
                        -size, size, -size,
                        -size, -size, -size,
                        -size, size, size,
                        -size, -size, -size,
                        -size, -size, size,
                        -size, size, size,

                        // Top face
                        -size, size, -size,
                        -size, size, size,
                        size, size, -size,
                        -size, size, size,
                        size, size, size,
                        size, size, -size,

                        // Bottom face
                        size, -size, -size,
                        size, -size, size,
                        -size, -size, -size,
                        size, -size, size,
                        -size, -size, size,
                        -size, -size, -size,
                };
        this.latLng = latLng;
        mCubePositions = ByteBuffer.allocateDirect(cubePositionData.length * mBytesPerFloat)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        mCubePositions.put(cubePositionData).position(0);

        mCubeNormals = ByteBuffer.allocateDirect(cubeNormalData.length * mBytesPerFloat)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        mCubeNormals.put(cubeNormalData).position(0);

        mCubeTextureCoordinates = ByteBuffer.allocateDirect(cubeTextureCoordinateData.length * mBytesPerFloat)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        mCubeTextureCoordinates.put(cubeTextureCoordinateData).position(0);

        mCubeTextureCoordinatesForPlane = ByteBuffer.allocateDirect(cubeTextureCoordinateDataForPlane.length * mBytesPerFloat)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        mCubeTextureCoordinatesForPlane.put(cubeTextureCoordinateDataForPlane).position(0);

        //setup the shaders
        int vertexShader;
        int fragmentShader;
        // Load the vertex/fragment shaders
        vertexShader = OpenGLRenderer.LoadShader(GLES20.GL_VERTEX_SHADER, vShaderStr);
        fragmentShader = OpenGLRenderer.LoadShader(GLES20.GL_FRAGMENT_SHADER, fShaderStr);

        mProgramHandle = ShaderHelper.createAndLinkProgram(vertexShader, fragmentShader,
                new String[] {"vPosition", "a_TexCoordinate"});



        // Load the texture
        mLogoHandle = TextureHelper.loadTexture(context, logo);
        GLES20.glGenerateMipmap(GLES20.GL_TEXTURE_2D);


        if (mQueuedMinFilter != 0)
        {
            setMinFilter(mQueuedMinFilter);
        }

        if (mQueuedMagFilter != 0)
        {
            setMagFilter(mQueuedMagFilter);
        }
        this.name = name;
    }



    public void draw(float[] mvpMatrix) {

        // Use the program object
        GLES20.glUseProgram(mProgramHandle);
        mCubePositions.position(0);
        // get handle to shape's transformation matrix
        mMVPMatrixHandle = GLES20.glGetUniformLocation(mProgramHandle, "uMVPMatrix");
        mTextureUniformHandle = GLES20.glGetUniformLocation(mProgramHandle, "u_Texture");
        mTextureCoordinateHandle = GLES20.glGetAttribLocation(mProgramHandle, "a_TexCoordinate");

        // get handle to fragment shader's vColor member
       // mColorHandle = GLES30.glGetUniformLocation(mProgramObject, "vColor");

        // Set the active texture unit to texture unit 0.
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);

        // Bind the texture to this unit.
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mLogoHandle);

        // Tell the texture uniform sampler to use this texture in the shader by binding to texture unit 0.
        GLES20.glUniform1i(mTextureUniformHandle, 0);

        // Pass in the texture coordinate information
        mCubeTextureCoordinates.position(0);
        GLES20.glVertexAttribPointer(mTextureCoordinateHandle, mTextureCoordinateDataSize, GLES20.GL_FLOAT, false,
                0, mCubeTextureCoordinates);

        GLES20.glEnableVertexAttribArray(mTextureCoordinateHandle);

        // Apply the projection and view transformation
        GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mvpMatrix, 0);



        GLES20.glVertexAttribPointer(mPositionHandle, mPositionDataSize, GLES20.GL_FLOAT, false,
                0, mCubePositions);
        GLES20.glEnableVertexAttribArray(mPositionHandle);



        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 36);


    }


    public void setMinFilter(final int filter)
    {
        if (mLogoHandle != 0)
        {
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mLogoHandle);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, filter);

        }
        else
        {
            mQueuedMinFilter = filter;
        }
    }

    public void setMagFilter(final int filter)
    {
        if (mLogoHandle != 0)
        {
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mLogoHandle);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, filter);
        }
        else
        {
            mQueuedMagFilter = filter;
        }
    }


    public LatLng getLatLng() {
        return latLng;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StationLogoModel that = (StationLogoModel) o;
        return name.equals(that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
