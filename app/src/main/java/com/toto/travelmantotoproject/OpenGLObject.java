package com.toto.travelmantotoproject;

import android.content.Context;
import android.opengl.GLES30;
import android.opengl.Matrix;
import android.util.Log;

import com.toto.travelmantotoproject.opengl.render.OpenGLRenderer;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class OpenGLObject {


    private static final String TAG = OpenGLObject.class.getName();


    private final static String fragmentShader = "precision mediump float;" +
                                                    "void main() {" +
                                                    "gl_FragColor = vec4(1, 0.5, 0, 1.0);" +
                                                    "}";
    private final static String vertexShader =  "attribute vec4 position;" +
                                                "uniform mat4 matrix;" +
                                                "void main() {" +
                                                "gl_Position = uMVPMatrix * vPosition;" +
                                                 "}";


    private List<String> verticesList;
    private List<String> facesList;

    private FloatBuffer verticesBuffer;
    private ShortBuffer facesBuffer;

    private static int programObject;


    private Context context;

    private String objFile;

    public OpenGLObject(Context context, String objFile) {
        this.context = context;
        this.objFile = objFile;
       init();
    }


    private void  init(){
        verticesList = new ArrayList<>();
        facesList = new ArrayList<>();

        readFile();

        // Create buffer for vertices
        ByteBuffer buffer1 = ByteBuffer.allocateDirect(verticesList.size() * 3 * 4);
        buffer1.order(ByteOrder.nativeOrder());
        verticesBuffer = buffer1.asFloatBuffer();



        // Create buffer for faces
        ByteBuffer buffer2 = ByteBuffer.allocateDirect(facesList.size() * 3 * 2);
        buffer2.order(ByteOrder.nativeOrder());
        facesBuffer = buffer2.asShortBuffer();


        for(String vertex: verticesList) {
            String coords[] = vertex.split(" "); // Split by space
            float x = Float.parseFloat(coords[1]);
            float y = Float.parseFloat(coords[2]);
            float z = Float.parseFloat(coords[3]);
            verticesBuffer.put(x);
            verticesBuffer.put(y);
            verticesBuffer.put(z);
        }
        verticesBuffer.position(0);



        for(String face: facesList) {
            face = face.replace("f ","");
            Log.d(TAG,  ">> "+face);
            String vertexIndices[] = face.split(" ");

            for(String vertexIndice : vertexIndices){

                String[] index = vertexIndice.split("//");
                short vertex = Short.parseShort(index[0]);

                facesBuffer.put((short)(vertex - 1));
            }

        }
        facesBuffer.position(0);

        int vertexShader = OpenGLRenderer.LoadShader(GLES30.GL_VERTEX_SHADER, OpenGLObject.vertexShader);

        int fragmentShader = OpenGLRenderer.LoadShader(GLES30.GL_FRAGMENT_SHADER, OpenGLObject.fragmentShader);


        // Create the program object

        programObject = GLES30.glCreateProgram();

        if (programObject == 0) {
            Log.e(TAG, "So some kind of error, but what?");
            return;
        }

        GLES30.glAttachShader(programObject, vertexShader);
        GLES30.glAttachShader(programObject, fragmentShader);


        GLES30.glLinkProgram(programObject);
        GLES30.glUseProgram(programObject);


    }



    public void draw() {
        // Drawing code goes here

        int position = GLES30.glGetAttribLocation(programObject, "position");
        GLES30.glEnableVertexAttribArray(position);

        GLES30.glVertexAttribPointer(position,3, GLES30.GL_FLOAT, false, 3 * 4,
                verticesBuffer);


        float[] projectionMatrix = new float[16];
        float[] viewMatrix = new float[16];

        float[] productMatrix = new float[16];

        Matrix.frustumM(projectionMatrix, 0,
                -1, 1,
                -1, 1,
                2, 9);


        Matrix.setLookAtM(viewMatrix, 0,
                0, 0, -4,
                0, 0, 0,
                0, 1, 0);


        Matrix.multiplyMM(productMatrix, 0,
                projectionMatrix, 0,
                viewMatrix, 0);


        int matrix = GLES30.glGetUniformLocation(programObject, "matrix");
        GLES30.glUniformMatrix4fv(matrix, 1, false, productMatrix, 0);



        GLES30.glDrawElements(GLES30.GL_TRIANGLES,
                facesList.size() * 3, GLES30.GL_UNSIGNED_SHORT, facesBuffer);

        GLES30.glDisableVertexAttribArray(position);

    }



    private void readFile()  {

        Scanner scanner = null;
        try {
            scanner = new Scanner(context.getAssets().open(objFile));
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Loop through all its lines
        while(scanner.hasNextLine()) {
            String line = scanner.nextLine();
            if(line.startsWith("v ")) {
                // Add vertex line to list of vertices
                verticesList.add(line);
            } else if(line.startsWith("f "))
                // Add face line to faces list
                facesList.add(line);
            }

        scanner.close();

    }

    public static int   getProgramObject() {
        return programObject;
    }


}
