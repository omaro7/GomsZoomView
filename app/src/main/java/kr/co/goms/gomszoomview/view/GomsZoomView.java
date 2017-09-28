package kr.co.goms.gomszoomview.view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;

import kr.co.goms.gomszoomview.util.GomsLog;

import static android.view.MotionEvent.INVALID_POINTER_ID;

/**
 * Created by HJH on 2017-04-20.
 */

public class GomsZoomView extends AppCompatImageView implements ScaleGestureDetector.OnScaleGestureListener {

    private static final String TAG = GomsZoomView.class.getSimpleName();

    private Paint mScalePaint;

    private boolean animateOnReset = true;
    private boolean autoCenter = true;
    @AutoResetMode private int autoResetMode = AutoResetMode.UNDER;

    private Matrix mMatrix = new Matrix();
    private Matrix mStartMatrix = new Matrix();

    private float[] matrixValues = new float[9];
    private float[] startValues = null;

    private final int RESET_DURATION = 200;

    private static float MIN_SCALE = 1.0f;
    private static float MAX_SCALE = 4f;

    private float minScale = MIN_SCALE;
    private float maxScale = MAX_SCALE;

    //the adjusted scale bounds that account for an image's starting scale values
    private float calculatedMinScale = MIN_SCALE;
    private float calculatedMaxScale = MAX_SCALE;

    private boolean isScale = false;
    private float mScaleFactor = 1.f;
    private ScaleGestureDetector mScaleDetector;
    private GestureDetector mGestureDetector;

    private float mScaleCenterPointX;
    private float mScaleCenterPointY;

    private final RectF mBounds = new RectF();
    private PointF mRectangleStartPoint = new PointF(0, 0);
    private PointF mRectangleLastPoint = new PointF(0, 0);
    private PointF mLastPoint = new PointF(0, 0);
    private float startScale = 1f;
    private float scaleBy = 1f;
    private int previousPointerCount = 1;

    private static final float TOUCH_TOLERANCE = 4;

    private boolean isDoubleTap = false;

    public GomsZoomView(Context context, AttributeSet attrs) {
        super(context, attrs);

        setFocusable(true); // necessary for getting the touch events

        mScalePaint = new Paint();
        mScalePaint.setTextSize(100);
        mScalePaint.setColor(Color.WHITE);
        mScalePaint.setStyle(Paint.Style.STROKE);

        mScaleDetector = new ScaleGestureDetector(getContext(), this);
        mGestureDetector = new GestureDetector(context, new GestureListener());
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldW, int oldH) {
        super.onSizeChanged(w, h, oldW, oldH);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        final int action = event.getAction();

        if (startValues == null) {
            setStartValues();
        }

        //get the current state of the image matrix, its values, and the bounds of the drawn bitmap
        mMatrix.set(getImageMatrix());
        mMatrix.getValues(matrixValues);
        updateBounds(matrixValues);

        mScaleDetector.onTouchEvent(event);

        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                touch_start(x, y);
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                touch_move(event, x, y);
                invalidate();
                break;
            case MotionEvent.ACTION_UP:

                float oldDist = spacing(event, mRectangleStartPoint);

                if(!isScale && event.getPointerCount() == 1 && oldDist < 20f) {
                    //if (!isScale && oldDist < 10f) {
                        Matrix inverseMatrix = new Matrix();
                        mMatrix.invert(inverseMatrix);

                        // Transform to relative coordinates
                        float[] point = new float[2];
                        point[0] = event.getX();
                        point[1] = event.getY();
                        inverseMatrix.mapPoints(point);

                        GomsLog.d(TAG, "x : " + x);
                        GomsLog.d(TAG, "y : " + y);
                        GomsLog.d(TAG, "point.x : " + point[0]);
                        GomsLog.d(TAG, "point.y : " + point[1]);
                        GomsLog.d(TAG, "scale : " + Math.abs(matrixValues[Matrix.MSCALE_X]));

                        mZoomListener.onOneTap(0, event, mScaleFactor, point[0], point[1]);
                    //}
                }
                isScale = false;
                invalidate();
                break;
        }

        if (event.getActionMasked() == MotionEvent.ACTION_DOWN || event.getPointerCount() != previousPointerCount) {
            GomsLog.d(TAG, "event.getActionMasked() == MotionEvent.ACTION_DOWN || event.getPointerCount() != previousPointerCount");
            mLastPoint.set(mScaleDetector.getFocusX(), mScaleDetector.getFocusY());
        }else if (event.getActionMasked() == MotionEvent.ACTION_MOVE) {

            if(event.getPointerCount() > 1) {
                GomsLog.d(TAG, "event.getPointerCount() : " + event.getPointerCount());

                final float focusx = mScaleDetector.getFocusX();
                final float focusy = mScaleDetector.getFocusY();

                //translatable
                //calculate the distance for translation
                float xdistance = getXDistance(focusx, mLastPoint.x);
                float ydistance = getYDistance(focusy, mLastPoint.y);
                mMatrix.postTranslate(xdistance, ydistance);

                //zoomable
                mMatrix.postScale(scaleBy, scaleBy, focusx, focusy);

                setImageMatrix(mMatrix);

                mLastPoint.set(focusx, focusy);
                //isScale = true;
                mScaleFactor = Math.abs(matrixValues[Matrix.MSCALE_X]);
                isScale = true;

                mZoomListener.onOriginalMove(mMatrix);

            }else{
                isScale = false;
            }
        }

        if (event.getActionMasked() == MotionEvent.ACTION_UP) {
            GomsLog.d(TAG, "event.getActionMasked() == MotionEvent.ACTION_UP");
            scaleBy = 1f;
            resetImage();
            isScale = false;
            isDoubleTap = false;
        }

        //this tracks whether they have changed the number of fingers down
        previousPointerCount = event.getPointerCount();

        return true;
    }

    /**
     * --------------------------------------------------------------------------------------------
     * 그림 그리기 onDraw
     * -----------------------------------------------------------------------------------------------
     */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if(isScale) {
            GomsLog.d(TAG, "mScaleFactor : " + mScaleFactor);
            String strNumber = String.format("%.2f", mScaleFactor);
            StringBuffer sb = new StringBuffer();
            sb.append("X ");
            sb.append(strNumber);
            canvas.drawText(sb.toString(), canvas.getWidth() / 2 - 100, canvas.getHeight() / 2, mScalePaint);
        }
    }

    /**
     * Get the x distance to translate the current image.
     *
     * @param toX   the current x location of touch focus
     * @param fromX the last x location of touch focus
     * @return the distance to move the image,
     * will restrict the translation to keep the image on screen.
     */
    private float getXDistance(final float toX, final float fromX) {
        float xdistance = toX - fromX;

        //if (restrictmBounds) {
            xdistance = getRestrictedXDistance(xdistance);
        //}

        //prevents image from translating an infinite distance offscreen
        if (mBounds.right + xdistance < 0) {
            xdistance = -mBounds.right;
        }
        else if (mBounds.left + xdistance > getWidth()) {
            xdistance = getWidth() - mBounds.left;
        }

        return xdistance;
    }


    /**
     * Get the horizontal distance to translate the current image, but restrict
     * it to the outer mBounds of the . If the current
     * image is smaller than the mBounds, keep it within the current mBounds.
     * If it is larger, prevent its edges from translating farther inward
     * from the outer edge.
     * @param xdistance the current desired horizontal distance to translate
     * @return the actual horizontal distance to translate with mBounds restrictions
     */
    private float getRestrictedXDistance(final float xdistance) {
        float restrictedXDistance = xdistance;

        if (getCurrentDisplayedWidth() >= getWidth()) {
            if (mBounds.left <= 0 && mBounds.left + xdistance > 0 && !mScaleDetector.isInProgress()) {
                restrictedXDistance = -mBounds.left;
            } else if (mBounds.right >= getWidth() && mBounds.right + xdistance < getWidth() && !mScaleDetector.isInProgress()) {
                restrictedXDistance = getWidth() - mBounds.right;
            }
        } else if (!mScaleDetector.isInProgress()) {
            if (mBounds.left >= 0 && mBounds.left + xdistance < 0) {
                restrictedXDistance = -mBounds.left;
            } else if (mBounds.right <= getWidth() && mBounds.right + xdistance > getWidth()) {
                restrictedXDistance = getWidth() - mBounds.right;
            }
        }

        return restrictedXDistance;
    }

    /**
     * Get the y distance to translate the current image.
     *
     * @param toY   the current y location of touch focus
     * @param fromY the last y location of touch focus
     * @return the distance to move the image,
     * will restrict the translation to keep the image on screen.
     */
    private float getYDistance(final float toY, final float fromY) {
        float ydistance = toY - fromY;

        //if (restrictmBounds) {
            ydistance = getRestrictedYDistance(ydistance);
        //}

        //prevents image from translating an infinite distance offscreen
        if (mBounds.bottom + ydistance < 0) {
            ydistance = -mBounds.bottom;
        }
        else if (mBounds.top + ydistance > getHeight()) {
            ydistance = getHeight() - mBounds.top;
        }

        return ydistance;
    }

    /**
     * Get the vertical distance to translate the current image, but restrict
     * it to the outer mBounds of the . If the current
     * image is smaller than the mBounds, keep it within the current mBounds.
     * If it is larger, prevent its edges from translating farther inward
     * from the outer edge.
     * @param ydistance the current desired vertical distance to translate
     * @return the actual vertical distance to translate with mBounds restrictions
     */
    private float getRestrictedYDistance(final float ydistance) {
        float restrictedYDistance = ydistance;

        if (getCurrentDisplayedHeight() >= getHeight()) {
            if (mBounds.top <= 0 && mBounds.top + ydistance > 0 && !mScaleDetector.isInProgress()) {
                restrictedYDistance = -mBounds.top;
            } else if (mBounds.bottom >= getHeight() && mBounds.bottom + ydistance < getHeight() && !mScaleDetector.isInProgress()) {
                restrictedYDistance = getHeight() - mBounds.bottom;
            }
        } else if (!mScaleDetector.isInProgress()) {
            if (mBounds.top >= 0 && mBounds.top + ydistance < 0) {
                restrictedYDistance = -mBounds.top;
            } else if (mBounds.bottom <= getHeight() && mBounds.bottom + ydistance > getHeight()) {
                restrictedYDistance = getHeight() - mBounds.bottom;
            }
        }

        return restrictedYDistance;
    }

    /**
     * Update the bounds of the displayed image based on the current matrix.
     *
     * @param values the image's current matrix values.
     */
    private void updateBounds(final float[] values) {
        if (getDrawable() != null) {
            mBounds.set(values[Matrix.MTRANS_X],
                    values[Matrix.MTRANS_Y],
                    getDrawable().getIntrinsicWidth() * values[Matrix.MSCALE_X] + values[Matrix.MTRANS_X],
                    getDrawable().getIntrinsicHeight() * values[Matrix.MSCALE_Y] + values[Matrix.MTRANS_Y]);
        }
    }

    /**
     * Get the width of the displayed image.
     *
     * @return the current width of the image as displayed (not the width of the  itself.
     */
    private float getCurrentDisplayedWidth() {
        if (getDrawable() != null)
            return getDrawable().getIntrinsicWidth() * matrixValues[Matrix.MSCALE_X];
        else
            return 0;
    }

    /**
     * Get the height of the displayed image.
     *
     * @return the current height of the image as displayed (not the height of the  itself.
     */
    private float getCurrentDisplayedHeight() {
        if (getDrawable() != null)
            return getDrawable().getIntrinsicHeight() * matrixValues[Matrix.MSCALE_Y];
        else
            return 0;
    }

    /**
     * Remember our starting values so we can animate our image back to its original position.
     */
    private void setStartValues() {
        startValues = new float[9];
        mStartMatrix = new Matrix(getImageMatrix());
        mStartMatrix.getValues(startValues);
        calculatedMinScale = minScale * startValues[Matrix.MSCALE_X];
        calculatedMaxScale = maxScale * startValues[Matrix.MSCALE_X];
    }

    /**
     * Reset the image based on the specified {@link AutoResetMode} mode.
     */
    private void resetImage() {
        switch (autoResetMode) {
            case AutoResetMode.UNDER:
                if (matrixValues[Matrix.MSCALE_X] <= startValues[Matrix.MSCALE_X]) {
                    mZoomListener.onOriginalReset(matrixValues, startValues);
                    reset();
                } else {
                    center();
                    mZoomListener.onOriginalCenter(matrixValues, mBounds);
                }
                break;
            case AutoResetMode.OVER:
                if (matrixValues[Matrix.MSCALE_X] >= startValues[Matrix.MSCALE_X]) {
                    reset();
                } else {
                    center();
                }
                break;
            case AutoResetMode.ALWAYS:
                reset();
                break;
            case AutoResetMode.NEVER:
                center();
        }
    }

    /**
     * This helps to keep the image on-screen by animating the translation to the nearest
     * edge, both vertically and horizontally.
     */
    private void center() {
        if (autoCenter) {
            animateTranslationX();
            animateTranslationY();
        }
    }

    /**
     * Reset image back to its original size. Will snap back to original size
     * if animation on reset is disabled via )}.
     */
    public void reset() {
        reset(animateOnReset);
    }

    /**
     * Reset image back to its starting size. If {@code animate} is false, image
     * will snap back to its original size.
     * @param animate animate the image back to its starting size
     */
    public void reset(final boolean animate) {
        if (animate) {
            animateToStartMatrix();
        }
        else {
            setImageMatrix(mStartMatrix);
        }
    }

    /**
     * Animate the matrix back to its original position after the user stopped interacting with it.
     */
    private void animateToStartMatrix() {

        final Matrix beginMatrix = new Matrix(getImageMatrix());
        beginMatrix.getValues(matrixValues);

        //difference in current and original values
        final float xsdiff = startValues[Matrix.MSCALE_X] - matrixValues[Matrix.MSCALE_X];
        final float ysdiff = startValues[Matrix.MSCALE_Y] - matrixValues[Matrix.MSCALE_Y];
        final float xtdiff = startValues[Matrix.MTRANS_X] - matrixValues[Matrix.MTRANS_X];
        final float ytdiff = startValues[Matrix.MTRANS_Y] - matrixValues[Matrix.MTRANS_Y];

        ValueAnimator anim = ValueAnimator.ofFloat(0, 1f);
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            final Matrix activeMatrix = new Matrix(getImageMatrix());
            final float[] values = new float[9];

            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float val = (Float) animation.getAnimatedValue();
                activeMatrix.set(beginMatrix);
                activeMatrix.getValues(values);
                values[Matrix.MTRANS_X] = values[Matrix.MTRANS_X] + xtdiff * val;
                values[Matrix.MTRANS_Y] = values[Matrix.MTRANS_Y] + ytdiff * val;
                values[Matrix.MSCALE_X] = values[Matrix.MSCALE_X] + xsdiff * val;
                values[Matrix.MSCALE_Y] = values[Matrix.MSCALE_Y] + ysdiff * val;
                activeMatrix.setValues(values);
                setImageMatrix(activeMatrix);
            }
        });
        anim.setDuration(RESET_DURATION);
        anim.start();
    }

    private void animateTranslationX() {
        if (getCurrentDisplayedWidth() > getWidth()) {
            //the left edge is too far to the interior
            if (mBounds.left > 0) {
                animateMatrixIndex(Matrix.MTRANS_X, 0);
            }
            //the right edge is too far to the interior
            else if (mBounds.right < getWidth()) {
                animateMatrixIndex(Matrix.MTRANS_X, mBounds.left + getWidth() - mBounds.right);
            }
        } else {
            //left edge needs to be pulled in, and should be considered before the right edge
            if (mBounds.left < 0) {
                animateMatrixIndex(Matrix.MTRANS_X, 0);
            }
            //right edge needs to be pulled in
            else if (mBounds.right > getWidth()) {
                animateMatrixIndex(Matrix.MTRANS_X, mBounds.left + getWidth() - mBounds.right);
            }
        }
    }

    private void animateTranslationY() {
        if (getCurrentDisplayedHeight() > getHeight()) {
            //the top edge is too far to the interior
            if (mBounds.top > 0) {
                animateMatrixIndex(Matrix.MTRANS_Y, 0);
            }
            //the bottom edge is too far to the interior
            else if (mBounds.bottom < getHeight()) {
                animateMatrixIndex(Matrix.MTRANS_Y, mBounds.top + getHeight() - mBounds.bottom);
            }
        } else {
            //top needs to be pulled in, and needs to be considered before the bottom edge
            if (mBounds.top < 0) {
                animateMatrixIndex(Matrix.MTRANS_Y, 0);
            }
            //bottom edge needs to be pulled in
            else if (mBounds.bottom > getHeight()) {
                animateMatrixIndex(Matrix.MTRANS_Y, mBounds.top + getHeight() - mBounds.bottom);
            }
        }
    }

    private void animateMatrixIndex(final int index, final float to) {
        ValueAnimator animator = ValueAnimator.ofFloat(matrixValues[index], to);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            final float[] values = new float[9];
            Matrix current = new Matrix();

            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                current.set(getImageMatrix());
                current.getValues(values);
                values[index] = (Float) animation.getAnimatedValue();
                current.setValues(values);
                setImageMatrix(current);
            }
        });
        animator.setDuration(RESET_DURATION);
        animator.start();
    }

    private static void drawScale(Canvas canvas, Bitmap bitmap, int x, int y, float scale){
        Matrix matrix = new Matrix();
        matrix.setScale(scale,scale);
        x =(int)(x /scale);
        y =(int)(y /scale);

        canvas.save();
        canvas.setMatrix(matrix);
        canvas.drawBitmap(bitmap,x,y,null);
        canvas.restore();
    }

    private void touch_start(float x, float y) {
        mRectangleStartPoint.set(x,y);
    }

    private void touch_move(MotionEvent ev, float x, float y) {
        mRectangleLastPoint.x = x;
        mRectangleLastPoint.y = y;
    }
    private void touch_up() {

    }

    /* 우측에서 좌측으로 처리, 좌측에서 우측으로 처리에 대한 방향 처리해서 드로잉
    private void drawRectangle(Canvas canvas,Paint paint){
        float right = mStartX > mX ? mStartX : mX;
        float left = mStartX > mX ? mX : mStartX;
        float bottom = mStartY > mY ? mStartY : mY;
        float top = mStartY > mY ? mY : mStartY;
        canvas.drawRect(left, top , right, bottom, paint);
    }
    */

    /**
     * Determine the space between the first two fingers
     */
    private float spacing(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float)Math.sqrt(x * x + y * y);
    }

    private float spacing(MotionEvent event, PointF pointF) {
        float x = event.getX(0) - pointF.x;
        float y = event.getY(0) - pointF.y;
        return (float)Math.sqrt(x * x + y * y);
    }


    /**
     * Calculate the mid point of the first two fingers
     */
    private void midPoint(PointF point, MotionEvent event) {
        float x = event.getX(0) + event.getX(1);
        float y = event.getY(0) + event.getY(1);
        point.set(x / 2, y / 2);
    }

    // The ‘active pointer’ is the one currently moving our object.
    private int mActivePointerId = INVALID_POINTER_ID;

    /**
     * This method converts dp unit to equivalent pixels, depending on device density.
     *
     * @param dp      A value in dp (density independent pixels) unit. Which we need to convert into pixels
     * @param context Context to get resources and device specific display metrics
     * @return A float value to represent px equivalent to dp depending on device density
     */
    public static float convertDpToPixel(float dp, Context context) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float px = dp * (metrics.densityDpi / 160f);
        return px;
    }

    private class Element {
        int x;
        int y;
        int num;
        public Element(int x, int y, int num) {
            this.x = x;
            this.y = y;
            this.num = num;
        }
    }

    @Override
    public boolean onScale(ScaleGestureDetector detector) {

        GomsLog.d(TAG, "onScale");
        GomsLog.d(TAG, "startScale : " + startScale);

        //calculate value we should scale by, ultimately the scale will be startScale*scaleFactor
        scaleBy = (startScale * detector.getScaleFactor()) / matrixValues[Matrix.MSCALE_X];

        //what the scaling should end up at after the transformation
        final float projectedScale = scaleBy * matrixValues[Matrix.MSCALE_X];

        //clamp to the min/max if it's going over
        if (projectedScale < calculatedMinScale) {
            scaleBy = calculatedMinScale / matrixValues[Matrix.MSCALE_X];
        } else if (projectedScale > calculatedMaxScale) {
            scaleBy = calculatedMaxScale / matrixValues[Matrix.MSCALE_X];
        }
        GomsLog.d(TAG, "scaleBy : " + scaleBy );

        return false;
    }

    @Override
    public boolean onScaleBegin(ScaleGestureDetector detector) {
        startScale = matrixValues[Matrix.MSCALE_X];
        return true;
    }

    @Override
    public void onScaleEnd(ScaleGestureDetector detector) {
        scaleBy = 1f;
    }

    private class GestureListener extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }
        // event when double tap occurs
        @Override
        public boolean onDoubleTap(MotionEvent e) {
            float x = e.getX();
            float y = e.getY();

            GomsLog.d("Double Tap", "Tapped at: (" + x + "," + y + ")");
            isDoubleTap = true;
            return true;
        }
    }

    private ZoomListener mZoomListener;

    public void setZoomListener(ZoomListener zoomListener) {
        mZoomListener = zoomListener;
    }

    public interface ZoomListener {
        void onOneTap(int type, MotionEvent motionEvent, float scale, float dx, float dy);
        void onOriginalMove(Matrix matrix);
        void onOriginalReset(float[] matrixValues, float[] startValues);
        void onOriginalCenter(float[] matrixValues, RectF mBounds);

    }

    public void refreshZoomView(){
        GomsLog.d(TAG, "refreshZoomView()");
        mMatrix = new Matrix();
        scaleBy = 1f;
        mMatrix.postScale(scaleBy, scaleBy);
        mMatrix.postTranslate(0, 0);
        setImageMatrix(mMatrix);
        isDoubleTap = false;
        invalidate();
    }



}

