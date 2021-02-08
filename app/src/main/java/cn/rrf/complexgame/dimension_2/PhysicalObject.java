package cn.rrf.complexgame.dimension_2;

import cn.rrf.complexgame.Vec;

public class PhysicalObject extends Renderer {

	public PhysicalObject(int id, Geometry geo, float[] projection, float[] view, Vec pos,
	                      Vec scale) {
		super(id, geo, projection, view, pos, scale);
	}
}
