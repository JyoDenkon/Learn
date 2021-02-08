package cn.rrf.complexgame.dimension_2;

import android.opengl.GLES31;
import android.opengl.Matrix;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import cn.rrf.complexgame.Vec;

public class Renderer {
	private int mIdentifier;
	private float[] mProjectionMatrix;
	private float[] mViewMatrix;
	private FloatBuffer mVerticesBuffer;
	private ShortBuffer mIndicesBuffer;
	private Geometry mGeometry;
	private int mShaderProgram;
	private float[] mVPMatrix;
	private Vec mPositionVector;
	private Vec mScaleVector;
	private float[] mColor;

	public Renderer() {}

	public Renderer(int id, Geometry geo, float[] projection, float[] view, Vec pos, Vec scale) {
		mProjectionMatrix = projection;
		mViewMatrix = view;
		mPositionVector = pos;
		mScaleVector = scale;
		mIdentifier = id;
		mGeometry = geo;

		mVPMatrix = new float[16];
		mColor = new float[4];
		mColor[0] = 0.0f;
		mColor[1] = 0.0f;
		mColor[2] = 0.0f;
		mColor[3] = 1.0f;

		init();
		calculateMatrix();
	}

	public Renderer setProjectionMatrix(float[] projectionMatrix) {
		mProjectionMatrix = projectionMatrix;
		return this;
	}

	public Renderer setViewMatrix(float[] viewMatrix) {
		mViewMatrix = viewMatrix;
		return this;
	}

	public Vec getPositionVector() {
		return mPositionVector;
	}

	public void setPositionVector(Vec positionVector) {
		this.mPositionVector = positionVector;
		calculateMatrix();
	}

	public void setPositionIncremental(Vec incremental) {
		mPositionVector.setX(mPositionVector.getX() + incremental.getX());
		mPositionVector.setY(mPositionVector.getY() + incremental.getY());
		mPositionVector.setZ(mPositionVector.getZ() + incremental.getZ());
	}

	public int getIdentifier() {
		return mIdentifier;
	}

	public void setColor(float[] color) {
		this.mColor = color;
	}

	public Vec getScaleVector() {
		return mScaleVector;
	}

	public void setScaleVector(Vec scaleVector) {
		this.mScaleVector = scaleVector;
	}

	private void init() {
		mVerticesBuffer =
				ByteBuffer.allocateDirect(mGeometry.getVertices().length * Float.SIZE / 8)
		                            .order(ByteOrder.nativeOrder()).asFloatBuffer().put(
						mGeometry.getVertices());
		mVerticesBuffer.position(0);

		mIndicesBuffer = ByteBuffer.allocateDirect(mGeometry.getIndices().length * Short.SIZE / 8)
		                           .order(ByteOrder.nativeOrder()).asShortBuffer().put(
						mGeometry.getIndices());
		mIndicesBuffer.position(0);

		String vertex =
				"attribute vec4 vPosition;" + "uniform mat4 vPMatrix;" + "void main() {" + "  " +
						"gl_Position = vPMatrix * vPosition;" + "}";
		String fragment = "precision mediump float;" + "uniform vec4 vColor;" + "void main() {" +
				"  gl_FragColor = vColor;" + "}";
		//readShader(vertex, fragment);

		int vertexShader = GLES31.glCreateShader(GLES31.GL_VERTEX_SHADER);
		GLES31.glShaderSource(vertexShader, vertex);
		GLES31.glCompileShader(vertexShader);

		int fragmentShader = GLES31.glCreateShader(GLES31.GL_FRAGMENT_SHADER);
		GLES31.glShaderSource(fragmentShader, fragment);
		GLES31.glCompileShader(fragmentShader);

		mShaderProgram = GLES31.glCreateProgram();
		GLES31.glAttachShader(mShaderProgram, vertexShader);
		GLES31.glAttachShader(mShaderProgram, fragmentShader);
		GLES31.glLinkProgram(mShaderProgram);
		//GLES31.glDeleteShader(vertexShader);
		//GLES31.glDeleteShader(fragmentShader);
	}

	private void calculateMatrix() {
		Matrix.multiplyMM(mVPMatrix, 0, mProjectionMatrix, 0, mViewMatrix, 0);
		Matrix.translateM(mVPMatrix, 0, mPositionVector.getX(), mPositionVector.getY(),
		                  mPositionVector.getZ());
		Matrix.scaleM(mVPMatrix, 0, mScaleVector.getX(), mScaleVector.getY(), mScaleVector.getZ());
	}

	public void draw() {
		GLES31.glUseProgram(mShaderProgram);

		int positionHandle = GLES31.glGetAttribLocation(mShaderProgram, "vPosition");
		GLES31.glVertexAttribPointer(positionHandle, 3, GLES31.GL_FLOAT, false, 3 * Float.SIZE / 8,
		                             mVerticesBuffer);

		int colorHandle = GLES31.glGetUniformLocation(mShaderProgram, "vColor");
		GLES31.glUniform4fv(colorHandle, 1, mColor, 0);

		int matrixHandle = GLES31.glGetUniformLocation(mShaderProgram, "vPMatrix");
		GLES31.glUniformMatrix4fv(matrixHandle, 1, false, mVPMatrix, 0);

		GLES31.glEnableVertexAttribArray(positionHandle);
		GLES31.glDrawElements(GLES31.GL_TRIANGLES, mGeometry.getIndices().length,
		                      GLES31.GL_UNSIGNED_SHORT, mIndicesBuffer);
		GLES31.glDisableVertexAttribArray(positionHandle);
	}

	public boolean testIn(float x, float y) {
		float width = mScaleVector.x;
		float height = mScaleVector.y;
		float cx = mPositionVector.x;
		float cy = mPositionVector.y;

		return x < cx + width / 2 && x > cx - width / 2 && y < cy + height / 2 && y > cy - height / 2;
	}

	/*private void readShader(String vertex, String fragment) {
		try {
			BufferedReader bufferedReader = new BufferedReader(new FileReader("Renderer.vertex"));
			StringBuilder stringBuilder = new StringBuilder();
			for (String s; (s = bufferedReader.readLine()) != null; ) {
				stringBuilder.append(s);
			}
			vertex = stringBuilder.toString();

			bufferedReader = new BufferedReader(new FileReader("Renderer.fragment"));
			stringBuilder = new StringBuilder();
			for (String s; (s = bufferedReader.readLine()) != null; ) {
				stringBuilder.append(s);
			}
			fragment = stringBuilder.toString();

		} catch (IOException e) {
			e.printStackTrace();
		}*/

	public Geometry getGeometry() {
		return mGeometry;
	}

	public void setGeometry(Geometry geometry) {
		this.mGeometry = geometry;
	}

	private void onMatricesChanged() {
		calculateMatrix();
	}

	public class Buider {
		private Renderer mRenderer;
		private float[] projectionMatrix;
		private float[] viewMatrix;
		private Vec position;
		private Vec scale;

		public Buider setProjectionMatrix(float[] projectionMatrix) {
			this.projectionMatrix = projectionMatrix;
			return this;
		}

		public Buider setViewMatrix(float[] viewMatrix) {
			this.viewMatrix = viewMatrix;
			return this;
		}

		public Buider setPosition(Vec position) {
			this.position = position;
			return this;
		}

		public Buider setScale(Vec scale) {
			this.scale = scale;
			return this;
		}
	}
}
