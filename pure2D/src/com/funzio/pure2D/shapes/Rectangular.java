/**
 * 
 */
package com.funzio.pure2D.shapes;

import android.graphics.PointF;
import android.graphics.RectF;
import android.view.MotionEvent;

import org.xmlpull.v1.XmlPullParser;

import com.funzio.pure2D.gl.gl10.QuadBuffer;
import com.funzio.pure2D.gl.gl10.textures.TextureCoordBuffer;
import com.funzio.pure2D.ui.TouchListener;
import com.funzio.pure2D.ui.UIManager;
import com.funzio.pure2D.ui.UIObject;

/**
 * @author long
 */
public class Rectangular extends Shape implements UIObject {

    protected boolean mTouchable;
    protected boolean mFocus;
    protected int mTouchPointerID = -1;
    protected TouchListener mTouchListener;

    public Rectangular() {
        super();

        // use default texture coordinates
        setTextureCoordBuffer(TextureCoordBuffer.getDefault());
    }

    @Override
    public void setSize(final float w, final float h) {
        if (mVertexBuffer == null) {
            mVertexBuffer = new QuadBuffer(0, 0, w, h);
        } else {
            ((QuadBuffer) mVertexBuffer).setSize(w, h);
        }

        super.setSize(w, h);
    }

    public void setRect(final RectF rect) {
        mPosition.x = rect.left;
        mPosition.y = rect.top;
        setSize(rect.width(), rect.height());
    }

    /**
     * @param x
     * @param y
     * @return true if point(x,y) is within the bounds
     */
    public boolean hitTest(final float x, final float y) {
        return mBounds.contains(x, y);
    }

    @Override
    public void setTouchable(final boolean touchable) {
        mTouchable = touchable;

        // auto update bounds
        if (!mAutoUpdateBounds) {
            mAutoUpdateBounds = true;
        }
    }

    @Override
    public boolean isTouchable() {
        return mTouchable && mAlive;
    }

    public TouchListener getTouchListener() {
        return mTouchListener;
    }

    public void setTouchListener(final TouchListener listener) {
        mTouchListener = listener;
    }

    /**
     * Note: This is called from UI-Thread
     * 
     * @hide
     */
    @Override
    public boolean onTouchEvent(final MotionEvent event) {
        if (!mTouchable || !mAlive) {
            return false;
        }

        final int action = event.getActionMasked();
        final int pointerIndex = (event.getAction() & MotionEvent.ACTION_POINTER_INDEX_MASK) >> MotionEvent.ACTION_POINTER_INDEX_SHIFT;
        final PointF touchedPoint = getScene().getTouchedPoint(pointerIndex);

        if (action == MotionEvent.ACTION_DOWN || action == MotionEvent.ACTION_POINTER_DOWN) {
            if (!mFocus && hitTest(touchedPoint.x, touchedPoint.y)) {
                // keep pointer id
                mTouchPointerID = event.getPointerId(pointerIndex);
                // flag focus
                mFocus = true;

                // event
                if (mTouchListener != null) {
                    mTouchListener.onTouchDown(this);
                }

                // take control
                return true;
            }
        } else if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_POINTER_UP) {
            if (mFocus && event.getPointerId(pointerIndex) == mTouchPointerID) {
                mTouchPointerID = -1;
                // unflag focus
                mFocus = false;

                // hit test
                final boolean hit = hitTest(touchedPoint.x, touchedPoint.y);
                // event
                if (mTouchListener != null) {
                    mTouchListener.onTouchUp(this, hit);
                }
                if (hit) {
                    // take control
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    public void setXMLAttributes(final XmlPullParser xmlParser, final UIManager manager) {
        super.setXMLAttributes(xmlParser, manager);

        final String touchable = xmlParser.getAttributeValue(null, "touchable");
        if (touchable != null) {
            setTouchable(Boolean.valueOf(touchable));
        }
    }

}
