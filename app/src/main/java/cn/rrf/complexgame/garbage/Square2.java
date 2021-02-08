package cn.rrf.complexgame.garbage;

import android.opengl.GLES20;
import android.opengl.Matrix;
import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import cn.rrf.complexgame.Vec;
import cn.rrf.complexgame.dimension_2.Geometry;
import cn.rrf.complexgame.dimension_2.RectangleFrame;


public class Square2 {
    private final short[] indices = {
            0, 1, 3, 2, 1, 3
    };
    private final FloatBuffer verticesBuffer;
    private final ShortBuffer indicesBuffer;
    private final int mProgram;
    private final Vec mScaleVec;
    private final float[] vPMatrix;
    private final int identifier;
    Geometry geometry;
    private Vec mTranslateVec;
    private float[] transformMatrix;
    private float[] IMatrix;
    private float[] colors = {0.1f, 0.1f, 0.1f, 1.0f};
    private float[] projectionMatrix;
    private float[] viewMatrix;

    public Square2(int identifier, float[] projection, float[] view, Vec translate, Vec scale) {
        float[] vertices = {
                0.5f, 0.5f, 0.0f,
                -0.5f, 0.5f, 0.0f,
                -0.5f, -0.5f, 0.0f,
                0.5f, -0.5f, 0.0f
        };
        geometry = new RectangleFrame(0.02f, 0.01f, 0.01f, 0.01f);
        verticesBuffer = ByteBuffer.allocateDirect(geometry.getVertices().length * Float.SIZE / 8).order(ByteOrder.nativeOrder()).asFloatBuffer().put(vertices);
        verticesBuffer.position(0);
        indicesBuffer = ByteBuffer.allocateDirect(geometry.getIndices().length * Short.SIZE / 8).order(ByteOrder.nativeOrder()).asShortBuffer().put(indices);
        indicesBuffer.position(0);

        String vertexShaderCode = "attribute vec4 vPosition;" +
                "uniform mat4 vPMatrix;" +
                "void main() {" +
                "  gl_Position = vPMatrix * vPosition;" +
                "}";
        int vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode);
        String fragmentShaderCode = "precision mediump float;" +
                "uniform vec4 vColor;" +
                "void main() {" +
                "  gl_FragColor = vColor;" +
                "}";
        int fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode);

        mProgram = GLES20.glCreateProgram();
        GLES20.glAttachShader(mProgram, vertexShader);
        GLES20.glAttachShader(mProgram, fragmentShader);
        GLES20.glLinkProgram(mProgram);

        projectionMatrix = new float[16];
        viewMatrix = new float[16];
        vPMatrix = new float[16];

        projectionMatrix = projection;
        viewMatrix = view;
        mTranslateVec = translate;
        mScaleVec = scale;
        this.identifier = identifier;

        caculate();

    }

    private void caculate() {
        Matrix.multiplyMM(vPMatrix, 0, projectionMatrix, 0, viewMatrix, 0);
        Matrix.translateM(vPMatrix, 0, mTranslateVec.x, mTranslateVec.y, mTranslateVec.z);
        Matrix.scaleM(vPMatrix, 0, mScaleVec.x, mScaleVec.y, mScaleVec.z);
    }

    public int getIdentifier() {
        return identifier;
    }

    public Vec getTranslateVec() {
        return mTranslateVec;
    }

    public void setTranslateVec(Vec vector) {
        this.mTranslateVec = vector;
        caculate();
    }

    private int loadShader(int type, String shaderCode) {
        int shader = GLES20.glCreateShader(type);
        GLES20.glShaderSource(shader, shaderCode);
        GLES20.glCompileShader(shader);
        return shader;
    }

    public void setColors(float[] colors) {
        this.colors = colors;
    }


    public boolean testIn(float x, float y) {


        float width = mScaleVec.x;
        float height = mScaleVec.y;
        float cx = mTranslateVec.x;
        float cy = mTranslateVec.y;
        if (true) {
            Log.w("Square", "ID" + identifier);
            Log.w("Square", "X" + x);
            Log.w("Square", "Y" + y);
            Log.w("Square", "PositionX" + cx);
            Log.w("Square", "PositionY" + cy);
            Log.w("Square", "width" + width);
            Log.w("Square", "height" + height);
            float[] center = {cx, cy, 0, 1};
            Matrix.multiplyMV(center, 0, vPMatrix, 0, center, 0);
            Log.w("Square", "centerNDC" + center[0] / center[3] + "  " + center[1] / center[3] + " " + center[2] / center[3]);
        }

        return x < cx + width / 2 && x > cx - width / 2 && y < cy + height / 2 && y > cy - height / 2;
    }

    private float[] genIMatrix() {
        float[] im = {1.0f, 0.0f, 0.0f, 0.0f,
                0.0f, 1.0f, 0.0f, 0.0f,
                0.0f, 0.0f, 1.0f, 0.0f,
                0.0f, 0.0f, 0.0f, 1.0f,
        };
        return im;
    }

    public void draw() {
        GLES20.glUseProgram(mProgram);

        int positionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");

        GLES20.glVertexAttribPointer(positionHandle, 3, GLES20.GL_FLOAT, false, 12, verticesBuffer);

        int colorHandle = GLES20.glGetUniformLocation(mProgram, "vColor");
        GLES20.glUniform4fv(colorHandle, 1, colors, 0);

        int matrixHandle = GLES20.glGetUniformLocation(mProgram, "vPMatrix");
        GLES20.glUniformMatrix4fv(matrixHandle, 1, false, vPMatrix, 0);
        GLES20.glEnableVertexAttribArray(positionHandle);
        GLES20.glDrawElements(GLES20.GL_TRIANGLES, geometry.getIndices().length, GLES20.GL_UNSIGNED_SHORT, indicesBuffer);
        if (false) {
            float[] scaled = new float[16];
            Matrix.scaleM(scaled, 0, vPMatrix, 0, 0.95f, 0.95f, 1);

            GLES20.glUniformMatrix4fv(matrixHandle, 1, false, scaled, 0);
            if (identifier != -1) GLES20.glUniform4f(colorHandle, 1.0f, 1.0f, 1.0f, 1.0f);

            GLES20.glDrawElements(GLES20.GL_TRIANGLES, geometry.getIndices().length, GLES20.GL_UNSIGNED_SHORT, indicesBuffer);
            GLES20.glDisableVertexAttribArray(positionHandle);

        }
    }
}

