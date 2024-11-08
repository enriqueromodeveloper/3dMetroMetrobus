package com.toto.travelmantotoproject;

/**
 * Created by Seker on 2/23/2016.
 * <p/>
 * This code actually will draw a "pyramid".
 */


import android.opengl.GLES30;
import android.util.Log;

import com.toto.travelmantotoproject.opengl.render.OpenGLRenderer;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class Pyramid {
    private int mProgramObject;
    private int mMVPMatrixHandle;
    private int mColorHandle;
    private FloatBuffer mVertices;

    //initial size of the pyramid.  set here, so it is easier to change later.
    float size = 0.1f;
    //this is the initial data, which will need to translated into the mVertices variable in the consturctor.
    float[] mVerticesData = new float[]{
            ////////////////////////////////////////////////////////////////////
            // top
            ////////////////////////////////////////////////////////////////////
            // FRONT
            0.0f, size, 0.0f, // top
            -size, -size, size, // front-left
            size, -size, size, // front-right
            // RIGHT
            0.0f, size, 0.0f, // top
            size, -size, size, // front-right
            size, -size, -size, // back-right
            // BACK
            0.0f, size, 0.0f, // top
            size, -size, -size, // back-right
            -size, -size, -size, // back-left
            // LEFT
            0.0f, size, 0.0f, // top
            -size, -size, -size, // back-left
            -size, -size, size, // front-left

            ////////////////////////////////////////////////////////////////////
            // BOTTOM
            ////////////////////////////////////////////////////////////////////
            // Triangle 1
            -size, -size, -size, // back-left
            -size, -size, size, // front-left
            size, -size, size, // front-right
            // Triangle 2
            size, -size, size, // front-right
            size, -size, -size, // back-right
            -size, -size, -size // back-left
    };

    float colorcyan[] = myColor.cyan();
    float colorblue[] = myColor.blue();
    float colorred[] = myColor.red();
    float colorgray[] = myColor.gray();
    float colorgreen[] = myColor.green();
    float coloryellow[] = myColor.yellow();

    //vertex shader code
    String vShaderStr =
            "#version 300 es 			  \n"
                    + "uniform mat4 uMVPMatrix;     \n"
                    + "in vec4 vPosition;           \n"
                    + "void main()                  \n"
                    + "{                            \n"
                    + "   gl_Position = uMVPMatrix * vPosition;  \n"
                    + "}                            \n";
    //fragment shader code.
    String fShaderStr =
            "#version 300 es		 			          	\n"
                    + "precision mediump float;					  	\n"
                    + "uniform vec4 vColor;	 			 		  	\n"
                    + "out vec4 fragColor;	 			 		  	\n"
                    + "void main()                                  \n"
                    + "{                                            \n"
                    + "  fragColor = vec4(1, 0.5, 0, 1.0);                    	\n"
                    + "}                                            \n";

    String TAG = "Pyramid";

    //finally some methods
    //constructor
    public Pyramid() {
        //first setup the mVertices correctly.
        mVertices = ByteBuffer
                .allocateDirect(mVerticesData.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()
                .put(mVerticesData);
        mVertices.position(0);

        //setup the shaders
        int vertexShader;
        int fragmentShader;
        int programObject;
        int[] linked = new int[1];

        // Load the vertex/fragment shaders
        vertexShader = OpenGLRenderer.LoadShader(GLES30.GL_VERTEX_SHADER, vShaderStr);
        fragmentShader = OpenGLRenderer.LoadShader(GLES30.GL_FRAGMENT_SHADER, fShaderStr);

        // Create the program object
        programObject = GLES30.glCreateProgram();

        if (programObject == 0) {
            Log.e(TAG, "So some kind of error, but what?");
            return;
        }

        GLES30.glAttachShader(programObject, vertexShader);
        GLES30.glAttachShader(programObject, fragmentShader);

        // Bind vPosition to attribute 0
        GLES30.glBindAttribLocation(programObject, 0, "vPosition");

        // Link the program
        GLES30.glLinkProgram(programObject);

        // Check the link status
        GLES30.glGetProgramiv(programObject, GLES30.GL_LINK_STATUS, linked, 0);

        if (linked[0] == 0) {
            Log.e(TAG, "Error linking program:");
            Log.e(TAG, GLES30.glGetProgramInfoLog(programObject));
            GLES30.glDeleteProgram(programObject);
            return;
        }

        // Store the program object
        mProgramObject = programObject;

        //now everything is setup and ready to draw.
    }


    public void draw(float[] mvpMatrix) {

        // Use the program object
        GLES30.glUseProgram(mProgramObject);

        // get handle to shape's transformation matrix
        mMVPMatrixHandle = GLES30.glGetUniformLocation(mProgramObject, "uMVPMatrix");
       // OpenGLRenderer.checkGlError("glGetUniformLocation");

        // get handle to fragment shader's vColor member
        mColorHandle = GLES30.glGetUniformLocation(mProgramObject, "vColor");


        // Apply the projection and view transformation
        GLES30.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mvpMatrix, 0);
       //OpenGLRenderer.checkGlError("glUniformMatrix4fv");

        int VERTEX_POS_INDX = 0;
        mVertices.position(VERTEX_POS_INDX);  //just in case.  We did it already though.

        //add all the points to the space, so they can be correct by the transformations.
        //would need to do this even if there were no transformations actually.
        GLES30.glVertexAttribPointer(VERTEX_POS_INDX, 3, GLES30.GL_FLOAT,
                false, 0, mVertices);
        GLES30.glEnableVertexAttribArray(VERTEX_POS_INDX);


        //Now we are ready to draw the cube finally.
        int startPos = 0;
        int verticesPerface = 3;

        //draw front face
        GLES30.glUniform4fv(mColorHandle, 1, colorblue, 0);
        GLES30.glDrawArrays(GLES30.GL_TRIANGLES, startPos, verticesPerface);
        startPos += verticesPerface;

        //draw right face
        GLES30.glUniform4fv(mColorHandle, 1, colorcyan, 0);
        GLES30.glDrawArrays(GLES30.GL_TRIANGLES, startPos, verticesPerface);
        startPos += verticesPerface;

        //draw back face
        GLES30.glUniform4fv(mColorHandle, 1, colorred, 0);
        GLES30.glDrawArrays(GLES30.GL_TRIANGLES, startPos, verticesPerface);
        startPos += verticesPerface;

        //draw left face
        GLES30.glUniform4fv(mColorHandle, 1, colorgray, 0);
        GLES30.glDrawArrays(GLES30.GL_TRIANGLES, startPos, verticesPerface);
        startPos += verticesPerface;


        //draw bottom face 1 tri square, so 6 faces.
        GLES30.glUniform4fv(mColorHandle, 1, coloryellow, 0);
        GLES30.glDrawArrays(GLES30.GL_TRIANGLES, startPos, verticesPerface);
        startPos += verticesPerface;

        //draw bottom face 2 tri square, so 6 faces.
        GLES30.glUniform4fv(mColorHandle, 1, coloryellow, 0);
        GLES30.glDrawArrays(GLES30.GL_TRIANGLES, startPos, verticesPerface);
        startPos += verticesPerface;
        //last face, so no need to increment.

    }

}
