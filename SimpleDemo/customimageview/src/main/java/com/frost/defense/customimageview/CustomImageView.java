package com.frost.defense.customimageview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;


/**
 * 自定义ImageView，支持圆形，全圆角，部分圆角，加边框，边框是否覆盖在内容上
 * 保留原生ImageView的所有功能，支持Glide显示gif
 *
 * @author Conrad
 * on 2019/9/16
 */
public class CustomImageView extends AppCompatImageView {

    private Paint clipPaint, borderPaint;
    private RectF srcRectF, borderRectF;
    private Path clipPath, srcPath, borderPath;
    private float[] cornerRadiusArr;
    private float[] borderRadiusArr;
    private boolean isRound = true;//是否圆形
    private float borderWidth;//边框宽度
    private int borderColor, cornerRadius, leftTopRadius, rightTopRadius, leftBottomRadius,
            rightBottomRadius;


    public CustomImageView(Context context) {
        this(context, null);
    }

    public CustomImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    public void setRound(boolean state) {
        this.isRound = state;
        initRadius();
        setCornerParams();
        invalidate();
    }

    public void setCornerRadius(int cornerRadius){
        this.cornerRadius = cornerRadius;
        initRadius();
        setCornerParams();
        invalidate();
    }

    public void setLeftTopRadius(int leftTopRadius) {
        this.leftTopRadius = leftTopRadius;
        initRadius();
        setCornerParams();
        invalidate();
    }

    public void setRightTopRadius(int rightTopRadius) {
        this.rightTopRadius = rightTopRadius;
        initRadius();
        setCornerParams();
        invalidate();
    }

    public void setLeftBottomRadius(int leftBottomRadius) {
        this.leftBottomRadius = leftBottomRadius;
        initRadius();
        setCornerParams();
        invalidate();
    }

    public void setRightBottomRadius(int rightBottomRadius) {
        this.rightBottomRadius = rightBottomRadius;
        initRadius();
        setCornerParams();
        invalidate();
    }

    public void setBorderColor(int color){
        this.borderColor = color;
        this.borderWidth = 2f;
        initRadius();
        setCornerParams();
        invalidate();
    }

    private void init(@NonNull Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CustomImageView);
        cornerRadius = typedArray.getDimensionPixelSize(R.styleable.CustomImageView_civCornerRadius, 0);
        leftTopRadius = typedArray.getDimensionPixelSize(R.styleable.CustomImageView_civCornerTopLeft, 0);
        rightTopRadius = typedArray.getDimensionPixelSize(R.styleable.CustomImageView_civCornerTopRight, 0);
        leftBottomRadius = typedArray.getDimensionPixelSize(R.styleable.CustomImageView_civCornerBottomLeft, 0);
        rightBottomRadius = typedArray.getDimensionPixelSize(R.styleable.CustomImageView_civCornerBottomRight, 0);
        borderWidth = typedArray.getDimension(R.styleable.CustomImageView_civBorderWidth, 0);
        borderColor = typedArray.getColor(R.styleable.CustomImageView_civBorderColor, 0x60ffffff);

        typedArray.recycle();

        initConfig();
    }

    private void initConfig() {
        PorterDuffXfermode xFerMode;
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.O_MR1) {
            xFerMode = new PorterDuffXfermode(PorterDuff.Mode.DST_IN);
        } else {
            xFerMode = new PorterDuffXfermode(PorterDuff.Mode.DST_OUT);
            srcPath = new Path();
        }

        clipPaint = new Paint();
        clipPaint.setAntiAlias(true);
        clipPaint.setStyle(Paint.Style.FILL);
        clipPaint.setXfermode(xFerMode);

        borderPaint = new Paint();
        borderPaint.setAntiAlias(true);
        borderPaint.setColor(borderColor);
        borderPaint.setStyle(Paint.Style.STROKE);
        borderPaint.setStrokeWidth(borderWidth);

        srcRectF = new RectF();
        borderRectF = new RectF();
        clipPath = new Path();
        srcPath = new Path();
        borderPath = new Path();

        initRadius();
    }

    private void initRadius() {
        cornerRadiusArr = new float[8];
        borderRadiusArr = new float[8];
        if (cornerRadius > 0) {
            isRound = false;
            for (int i = 0; i < cornerRadiusArr.length; i++) {
                borderRadiusArr[i] = cornerRadius;
                cornerRadiusArr[i] = cornerRadius + borderWidth / 2;
            }
        } else if (leftTopRadius > 0 || leftBottomRadius > 0 || rightTopRadius > 0 || rightBottomRadius > 0) {
            isRound = false;
            borderRadiusArr[0] = borderRadiusArr[1] = leftTopRadius;
            borderRadiusArr[2] = borderRadiusArr[3] = rightTopRadius;
            borderRadiusArr[4] = borderRadiusArr[5] = rightBottomRadius;
            borderRadiusArr[6] = borderRadiusArr[7] = leftBottomRadius;

            if (leftTopRadius > 0) {
                cornerRadiusArr[0] = cornerRadiusArr[1] = leftTopRadius + borderWidth / 2;
            }
            if (rightTopRadius > 0) {
                cornerRadiusArr[2] = cornerRadiusArr[3] = rightTopRadius + borderWidth / 2;
            }
            if (rightBottomRadius > 0) {
                cornerRadiusArr[4] = cornerRadiusArr[5] = rightBottomRadius + borderWidth / 2;
            }
            if (leftBottomRadius > 0) {
                cornerRadiusArr[6] = cornerRadiusArr[7] = leftBottomRadius + borderWidth / 2;
            }
        }
    }

    private void setCornerParams(){
        float width = getWidth();
        float height = getHeight();
        float centerX = width / 2f;
        float centerY = height / 2f;
        float maxImgRadius = Math.min(width - getPaddingLeft() - getPaddingRight(), height - getPaddingTop() - getPaddingBottom()) / 2f;
        float maxBorderRadius = Math.min(width, height) / 2f;
        clipPath.reset();
        srcPath.reset();
        borderPath.reset();
        if (isRound) {
            clipPath.addCircle(centerX, centerY, maxImgRadius, Path.Direction.CCW);
            srcRectF.set(centerX - maxImgRadius,
                    centerY - maxImgRadius,
                    centerX + maxImgRadius,
                    centerY + maxImgRadius);
            if (borderWidth > 0) {
                borderPath.addCircle(centerX, centerY, maxBorderRadius - borderWidth / 2f, Path.Direction.CCW);
            }

        } else {
            srcRectF.set(getPaddingLeft(), getPaddingTop(),
                    width - getPaddingRight(), height - getPaddingBottom());
            clipPath.addRoundRect(srcRectF, cornerRadiusArr, Path.Direction.CCW);
            if (borderWidth > 0) {
                borderRectF.set(borderWidth / 2f, borderWidth / 2f, width - borderWidth / 2f, height - borderWidth / 2f);
                borderPath.addRoundRect(borderRectF, borderRadiusArr, Path.Direction.CCW);
            }
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        setCornerParams();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // 使用图形混合模式来显示指定区域的图片
        canvas.saveLayer(srcRectF, null, Canvas.ALL_SAVE_FLAG);
        super.onDraw(canvas);
        drawClip(canvas);
        drawBorder(canvas);
    }

    private void drawClip(Canvas canvas) {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.O_MR1) {
            canvas.drawPath(clipPath, clipPaint);
        } else {
            srcPath.reset();
            srcPath.addRect(srcRectF, Path.Direction.CCW);
            srcPath.op(clipPath, Path.Op.DIFFERENCE);
            canvas.drawPath(srcPath, clipPaint);
        }
    }

    private void drawBorder(Canvas canvas) {
        if (borderWidth <= 0) {
            return;
        }
        // 恢复画布
        canvas.restore();
        canvas.drawPath(borderPath, borderPaint);
    }
}
