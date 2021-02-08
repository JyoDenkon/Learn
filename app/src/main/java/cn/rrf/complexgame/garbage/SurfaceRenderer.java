package cn.rrf.complexgame.garbage;

import android.opengl.GLES31;
import android.opengl.GLSurfaceView;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class SurfaceRenderer implements GLSurfaceView.Renderer {
    private final String vertexShaderCode = "attribute vec4 vPosition;uniform mat4 vPMatrix;void main(){gl_Position=vPMatrix*vPosition;}";
    private final String fragmentShaderCode = "precision mediump float;uniform vec4 vColor;void main(){gl_FragColor=vec4(0.1f,0.1f,0.1f,1.0f);}";

    private int shaderProgram;
    private final float[] triangle = {
            0.0f, 0.5f, 0.0f,
            -0.5f, -0.5f, 0.0f,
            0.5f, -0.5f, 0.0f
    };
    private FloatBuffer triangleBuffer;
    private final float[] colors= {0.1f,0.1f,0.1f,1.0f};
    /*private float[] projectionMatrix;
    private float[] viewMatrix;
    private float[] transformMatrix;
    private float[] scaleMatrix;
    private int positionHandler;
    private int colorHandler;
    private int matrixHandler;
    private final float[] vertices = {
            0.5f, 0.5f, 0.0f,
            -0.5f, 0.5f, 0.0f,
            -0.5f, -0.5f, 0.0f,
            0, 5f, -0.5f, 0.0f
    };
    private final short[] indices = {
            0, 1, 3, 2, 1, 3
    };
    private final float[] colors = {0.1f, 0.1f, 0.1f, 1.0f};
    private int shaderProgram;
    private FloatBuffer verticesBuffer;
    private ShortBuffer indicesBuffer;
    private Outline outer;*/

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        int vertexShader = GLES31.glCreateShader(GLES31.GL_VERTEX_SHADER);
        GLES31.glShaderSource(vertexShader, vertexShaderCode);
        GLES31.glCompileShader(vertexShader);
        int fragmentShader = GLES31.glCreateShader(GLES31.GL_FRAGMENT_SHADER);
        GLES31.glShaderSource(fragmentShader, fragmentShaderCode);
        GLES31.glCompileShader(fragmentShader);
        shaderProgram = GLES31.glCreateProgram();
        GLES31.glAttachShader(shaderProgram, vertexShader);
        GLES31.glAttachShader(shaderProgram, fragmentShader);
        GLES31.glLinkProgram(shaderProgram);
        GLES31.glDeleteShader(vertexShader);
        GLES31.glDeleteShader(fragmentShader);

        triangleBuffer = ByteBuffer.allocateDirect(triangle.length * Float.SIZE / 8).order(ByteOrder.nativeOrder()).asFloatBuffer().put(triangle);
        triangleBuffer.position(0);
        /*projectionMatrix = new float[16];
        viewMatrix = new float[16];
        transformMatrix = new float[16];
        scaleMatrix = new float[16];
        int vertexShader = GLES31.glCreateShader(GLES31.GL_VERTEX_SHADER);
        int fragmentShader = GLES31.glCreateShader(GLES31.GL_FRAGMENT_SHADER);
        String vertexShaderCode = "attribute vec4 vPosition;" +
                "uniform mat4 vPMatrix;" +
                "void main() {" +
                "gl_Position = vPMatrix * vPosition;" +
                "}";
        GLES31.glShaderSource(vertexShader, vertexShaderCode);
        String fragmentShaderCode =
                "precision mediump float;" +
                        "uniform vec4 vColor;" +
                        "void main() {" +
                        "gl_FragColor = vColor;" +
                        "}";
        GLES31.glShaderSource(fragmentShader, fragmentShaderCode);
        GLES31.glCompileShader(vertexShader);
        GLES31.glCompileShader(fragmentShader);


        shaderProgram = GLES31.glCreateProgram();
        GLES31.glAttachShader(shaderProgram, vertexShader);
        GLES31.glAttachShader(shaderProgram, fragmentShader);
        GLES31.glLinkProgram(shaderProgram);
        GLES31.glDeleteShader(vertexShader);
        GLES31.glDeleteShader(fragmentShader);
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(vertices.length * Float.SIZE / 8);
        byteBuffer.order(ByteOrder.nativeOrder());
        verticesBuffer = byteBuffer.asFloatBuffer();
        verticesBuffer.put(vertices);
        verticesBuffer.position(0);

        byteBuffer = ByteBuffer.allocateDirect(indices.length * Short.SIZE / 8);
        byteBuffer.order(ByteOrder.nativeOrder());
        indicesBuffer = byteBuffer.asShortBuffer();
        indicesBuffer.put(indices);
        indicesBuffer.position(0);*/
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        GLES31.glViewport(0, 0, width, height);
        /*GLES31.glViewport(0, 0, width, height);
        float ratio = (float) width / height;
        Matrix.frustumM(projectionMatrix, 0, -ratio, ratio, -1, 1, 3, 7);
        Matrix.setLookAtM(viewMatrix, 0, 0, 0, -3, 0, 0, 0, 0, 1, 0);
        Matrix.translateM(transformMatrix, 0, 0, 0, 0);
        Matrix.scaleM(scaleMatrix, 0, 1, 1, 1);
        outer = new Outline(transformMatrix, scaleMatrix, projectionMatrix, viewMatrix);*/
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        GLES31.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
        GLES31.glClear(GLES31.GL_COLOR_BUFFER_BIT);

        GLES31.glUseProgram(shaderProgram);
        int positionHandle = GLES31.glGetAttribLocation(shaderProgram, "vPosition");
        int colorHandle = GLES31.glGetUniformLocation(shaderProgram, "vColor");
        GLES31.glEnableVertexAttribArray(positionHandle);
        GLES31.glVertexAttribPointer(positionHandle, 3, GLES31.GL_FLOAT, false, 12, triangleBuffer);

        GLES31.glUniform4fv(colorHandle,1,colors,0);
        GLES31.glDrawArrays(GLES31.GL_TRIANGLES, 0, 3);
        GLES31.glDisableVertexAttribArray(positionHandle);

        /*GLES31.glClearColor(0.2f, 0.3f, 0.3f, 1.0f);
        GLES31.glClear(GLES31.GL_COLOR_BUFFER_BIT);
        //outer.draw();
        GLES31.glUseProgram(shaderProgram);
        positionHandler = GLES31.glGetAttribLocation(shaderProgram, "vPosition");
        colorHandler = GLES31.glGetUniformLocation(shaderProgram, "vColor");
        matrixHandler = GLES31.glGetUniformLocation(shaderProgram, "vPMatrix");
        GLES31.glEnableVertexAttribArray(positionHandler);
        GLES31.glVertexAttribPointer(positionHandler, 3, GLES31.GL_FLOAT, false, 12, 0);
        GLES31.glUniform4fv(colorHandler, 1, colors, 0);
        //GLES31.glUniformMatrix4fv(matrixHandler, 1, false, matrix, 0);
        GLES31.glDrawElements(GLES31.GL_TRIANGLES, indices.length, GLES31.GL_UNSIGNED_SHORT, indicesBuffer);

        GLES31.glDisableVertexAttribArray(positionHandler);*/
    }
}

