/**
 * 
 */
package com.funzio.pure2D.gl.gl10;

import com.funzio.pure2D.gl.GLColor;

/**
 * @author long
 */
public class QuadMeshColorBuffer extends ColorBuffer {
    public static final int NUM_CHANNEL_PER_COLOR = 4;
    public static final int NUM_COLOR_PER_CELL = 4;

    protected float[] mValues;
    protected int mNumCells = 0;

    protected boolean mInvalidated = false;

    public QuadMeshColorBuffer(final int numCells) {
        super();

        setNumCells(numCells);
    }

    public void setNumCells(final int numCells) {
        if (numCells > mNumCells) {
            mValues = new float[numCells * NUM_CHANNEL_PER_COLOR * NUM_COLOR_PER_CELL];

            int start = 0;
            for (int i = 0; i < numCells; i++) {
                for (int j = 0; j < NUM_COLOR_PER_CELL; j++) {
                    mValues[start++] = 1f;
                    mValues[start++] = 1f;
                    mValues[start++] = 1f;
                    mValues[start++] = 1f;
                }
            }

            mInvalidated = true;
        }

        mNumCells = numCells;
    }

    public int getNumCells() {
        return mNumCells;
    }

    /**
     * Set color at the specified cell
     * 
     * @param index
     * @param color
     * @see #validate()
     */
    public void setColorAt(final int index, final GLColor color) {

        int start = index * NUM_CHANNEL_PER_COLOR * NUM_COLOR_PER_CELL;
        for (int j = 0; j < NUM_COLOR_PER_CELL; j++) {
            if (color != null) {
                mValues[start++] = color.r;
                mValues[start++] = color.g;
                mValues[start++] = color.b;
                mValues[start++] = color.a;
            } else {
                mValues[start++] = 1f;
                mValues[start++] = 1f;
                mValues[start++] = 1f;
                mValues[start++] = 1f;
            }
        }

        mInvalidated = true;
    }

    public void setAlphaAt(final int index, final float alpha) {

        int start = index * NUM_CHANNEL_PER_COLOR * NUM_COLOR_PER_CELL;
        for (int j = 0; j < NUM_COLOR_PER_CELL; j++) {
            mValues[start + 3] = alpha;

            start += NUM_COLOR_PER_CELL;
        }

        mInvalidated = true;
    }

    /**
     * Applies the values set by {@link #setColorAt(int, float...)}
     */
    public void validate() {
        if (mInvalidated) {
            setValues(mValues);

            mInvalidated = false;
        }
    }

}
