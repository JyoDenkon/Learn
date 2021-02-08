package cn.rrf.complexgame.garbage;

import android.opengl.GLES31;
import android.opengl.Matrix;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

public class Outline {
    private final float[] vertices = {
            0.5f, 0.5f, 0.0f,
            -0.5f, 0.5f, 0.0f,
            -0.5f, -0.5f, 0.0f,
            0, 5f, -0.5f, 0.0f
    };
    private final short[] indices = {
            0, 1, 3, 2, 1, 3
    };
    private float[] matrix = new float[16];
    private float[] projectionMatrix = new float[16];
    private float[] viewMatrix = new float[16];
    private float[] innerMatrix = new float[16];
    private final float[] colors = {0.1f, 0.1f, 0.1f, 1.0f};
    private int shaderProgram;
    private int positionHandler;
    private int colorHandler;
    private int matrixHandler;

    private FloatBuffer verticesBuffer;
    private ShortBuffer indicesBuffer;

    public Outline(float[] transform, float[] scale, float[] projection, float[] view) {
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



        Matrix.multiplyMM(matrix, 0, projection, 0, view, 0);
        Matrix.multiplyMM(matrix, 0, transform, 0, matrix, 0);
        Matrix.multiplyMM(matrix, 0, scale, 0, matrix, 0);

        Matrix.scaleM(innerMatrix, 0, matrix, 0, 0.9f, 0.9f, 1.0f);
    }

    public void init() {
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(vertices.length * Float.SIZE / 8);
        byteBuffer.order(ByteOrder.nativeOrder());
        verticesBuffer = byteBuffer.asFloatBuffer();
        verticesBuffer.put(vertices);
        verticesBuffer.position(0);

        byteBuffer = ByteBuffer.allocateDirect(indices.length * Short.SIZE / 8);
        byteBuffer.order(ByteOrder.nativeOrder());
        indicesBuffer = byteBuffer.asShortBuffer();
        indicesBuffer.put(indices);
        indicesBuffer.position(0);
    }

    public void draw() {
        GLES31.glUseProgram(shaderProgram);
        positionHandler = GLES31.glGetAttribLocation(shaderProgram, "vPosition");
        colorHandler = GLES31.glGetUniformLocation(shaderProgram, "vColor");
        matrixHandler = GLES31.glGetUniformLocation(shaderProgram, "vPMatrix");
        GLES31.glEnableVertexAttribArray(positionHandler);
        GLES31.glVertexAttribPointer(positionHandler, 3, GLES31.GL_FLOAT, false, 12, 0);
        GLES31.glUniform4fv(colorHandler, 1, colors, 0);
        GLES31.glUniformMatrix4fv(matrixHandler, 1, false, matrix, 0);
        GLES31.glDrawElements(GLES31.GL_TRIANGLES, indices.length, GLES31.GL_UNSIGNED_SHORT, indicesBuffer);

        GLES31.glDisableVertexAttribArray(positionHandler);
    }
}
