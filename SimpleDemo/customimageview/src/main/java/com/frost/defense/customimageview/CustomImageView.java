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
    private boolean isRound = false;//是否圆形
    private boolean isBorderOver = false;//边框线是否覆盖在头像上
    private boolean isDrawCorner = false;//是否绘制圆角
    private float borderWidth;
    private int width, height, borderColor, cornerRadius, topLeftRadius, topRightRadius, botLeftRadius, botRightRadius;


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

    public void setRound(boolean isRound) {
        this.isRound = isRound;
        invalidate();
    }

    public void setTopLeftRadius(int topLeftRadius) {
        this.topLeftRadius = dp2px(getContext(), topLeftRadius);
        initRadius();
        invalidate();
    }

    public void setTopRightRadius(int topRightRadius) {
        this.topRightRadius = dp2px(getContext(), topRightRadius);
        initRadius();
        invalidate();
    }

    public void setBotLeftRadius(int botLeftRadius) {
        this.botLeftRadius = dp2px(getContext(), botLeftRadius);
        initRadius();
        invalidate();
    }

    public void setBotRightRadius(int botRightRadius) {
        this.botRightRadius = dp2px(getContext(), botRightRadius);
        initRadius();
        invalidate();
    }

    private void init(@NonNull Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CustomImageView);
        isRound = typedArray.getBoolean(R.styleable.CustomImageView_round, false);
        cornerRadius = typedArray.getDimensionPixelSize(R.styleable.CustomImageView_corner, 0);
        topLeftRadius = typedArray.getDimensionPixelSize(R.styleable.CustomImageView_corner_top_left, 0);
        topRightRadius = typedArray.getDimensionPixelSize(R.styleable.CustomImageView_corner_top_right, 0);
        botLeftRadius = typedArray.getDimensionPixelSize(R.styleable.CustomImageView_corner_bottom_left, 0);
        botRightRadius = typedArray.getDimensionPixelSize(R.styleable.CustomImageView_corner_bottom_right, 0);
        isBorderOver = typedArray.getBoolean(R.styleable.CustomImageView_border_over, false);
        borderWidth = typedArray.getDimension(R.styleable.CustomImageView_border_width, 0);
        borderColor = typedArray.getColor(R.styleable.CustomImageView_border_color, 0x60ffffff);

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

    private void initRadius(){
        cornerRadiusArr = new float[8];
        borderRadiusArr = new float[8];
        if (cornerRadius > 0) {
            isDrawCorner = true;
            for (int i = 0; i < cornerRadiusArr.length; i++) {
                borderRadiusArr[i] = cornerRadius;
                if (isBorderOver) {
                    cornerRadiusArr[i] = cornerRadius + borderWidth / 2;
                } else {
                    cornerRadiusArr[i] = cornerRadius - borderWidth / 2;
                }
            }
        } else if (topLeftRadius > 0 || botLeftRadius > 0 || topRightRadius > 0 || botRightRadius > 0) {
            isDrawCorner = true;
            borderRadiusArr[0] = borderRadiusArr[1] = topLeftRadius;
            borderRadiusArr[2] = borderRadiusArr[3] = topRightRadius;
            borderRadiusArr[4] = borderRadiusArr[5] = botRightRadius;
            borderRadiusArr[6] = borderRadiusArr[7] = botLeftRadius;

            if (isBorderOver) {
                if (topLeftRadius > 0) {
                    cornerRadiusArr[0] = cornerRadiusArr[1] = topLeftRadius + borderWidth / 2;
                }
                if (topRightRadius > 0) {
                    cornerRadiusArr[2] = cornerRadiusArr[3] = topRightRadius + borderWidth / 2;
                }
                if (botRightRadius > 0) {
                    cornerRadiusArr[4] = cornerRadiusArr[5] = botRightRadius + borderWidth / 2;
                }
                if (botLeftRadius > 0) {
                    cornerRadiusArr[6] = cornerRadiusArr[7] = botLeftRadius + borderWidth / 2;
                }
            } else {
                if (topLeftRadius > 0) {
                    cornerRadiusArr[0] = cornerRadiusArr[1] = topLeftRadius - borderWidth / 2;
                }
                if (topRightRadius > 0) {
                    cornerRadiusArr[2] = cornerRadiusArr[3] = topRightRadius - borderWidth / 2;
                }
                if (botRightRadius > 0) {
                    cornerRadiusArr[4] = cornerRadiusArr[5] = botRightRadius - borderWidth / 2;
                }
                if (botLeftRadius > 0) {
                    cornerRadiusArr[6] = cornerRadiusArr[7] = botLeftRadius - borderWidth / 2;
                }
            }
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        width = getWidth();
        height = getHeight();
        int maxRadius = Math.min(width, height) / 2;
        if (isRound) {
            clipPath.addCircle(width / 2.0f, height / 2.0f, maxRadius, Path.Direction.CCW);
            srcRectF.set(width / 2.0f - maxRadius, height / 2.0f - maxRadius,
                    width / 2.0f + maxRadius, height / 2.0f + maxRadius);
            if (borderWidth > 0) {
                borderPath.addCircle(width / 2.0f, height / 2.0f, maxRadius - borderWidth / 2, Path.Direction.CCW);
                if (!isBorderOver) {
                    srcRectF.set(width / 2.0f - maxRadius + borderWidth, height / 2.0f - maxRadius + borderWidth,
                            width / 2.0f + maxRadius - borderWidth, height / 2.0f + maxRadius - borderWidth);
                }
            }

        } else {
            srcRectF.set(0, 0, width, height);
            if (isDrawCorner) {
                clipPath.addRoundRect(srcRectF, cornerRadiusArr, Path.Direction.CCW);
                if (borderWidth > 0) {
                    borderRectF.set(borderWidth / 2, borderWidth / 2, width - borderWidth / 2, height - borderWidth / 2);
                    borderPath.addRoundRect(borderRectF, borderRadiusArr, Path.Direction.CCW);
                }
            } else if (borderWidth > 0) {
                borderRectF.set(0, 0, width, height);
                borderPath.addRect(borderRectF, Path.Direction.CCW);
            }
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // 使用图形混合模式来显示指定区域的图片
        canvas.saveLayer(srcRectF, null, Canvas.ALL_SAVE_FLAG);
        if (borderWidth > 0 && !isBorderOver) {
            float scaleX = (width - 2 * borderWidth) / width;
            float scaleY = (height - 2 * borderWidth) / height;
            canvas.scale(scaleX, scaleY, width / 2.0f, height / 2.0f);
        }
        super.onDraw(canvas);

        drawClip(canvas);

        drawBorder(canvas);
    }

    private void drawClip(Canvas canvas) {
        if (!isRound && !isDrawCorner) {
            return;
        }
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

    private int dp2px(Context context, float dpValue) {
        if (context == null) {
            return 0;
        }
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
}
