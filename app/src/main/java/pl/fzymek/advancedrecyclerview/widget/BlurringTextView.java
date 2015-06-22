package pl.fzymek.advancedrecyclerview.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.v8.renderscript.Allocation;
import android.support.v8.renderscript.Element;
import android.support.v8.renderscript.RenderScript;
import android.support.v8.renderscript.ScriptIntrinsicBlur;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import pl.fzymek.advancedrecyclerview.R;

/**
 * Created by Filip Zymek on 2015-06-22.
 */
public class BlurringTextView extends TextView {

	public static final int DEFAULT_BLUR_RADIUS = 5;
	public static final int DEFAULT_DOWNSAMPLE_FACTOR = 8;

	private int blurRadius;
	private int downsampleFactor;
	private int overlayColor;
	private View blurredView;
	private int blurredViewWidth;
	private int blurredViewHeight;

	private boolean hasDownsampleFactorChanged;
	private Bitmap bitmapToBlur, blurredBitmap;
	private Canvas blurringCanvas;
	private RenderScript rs;
	private ScriptIntrinsicBlur blurScript;
	private Allocation blurInput;
	private Allocation blurOutput;

	public BlurringTextView(Context context) {
		super(context);
		initRenderscript(context);
		setBlurRadius(DEFAULT_BLUR_RADIUS);
		setDownsampleFactor(DEFAULT_DOWNSAMPLE_FACTOR);
		setOverlayColor(context.getResources().getColor(android.R.color.transparent));
	}

	public BlurringTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context, attrs);
	}

	public BlurringTextView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init(context, attrs);
	}

	public int getBlurRadius() {
		return blurRadius;
	}

	public void setBlurRadius(int blurRadius) {
		if (blurRadius < 0) {
			throw new IllegalArgumentException("blurRadius property must be greater than 0!");
		}
		this.blurRadius = blurRadius;
		if (!isInEditMode()) {
			blurScript.setRadius(this.blurRadius);
		}
	}

	public int getDownsampleFactor() {
		return downsampleFactor;
	}

	public void setDownsampleFactor(int downsampleFactor) {
		if (downsampleFactor < 0) {
			throw new IllegalArgumentException("downsampleFactor property must be greater than 0!");
		}
		if (this.downsampleFactor != downsampleFactor) {
			this.downsampleFactor = downsampleFactor;
			hasDownsampleFactorChanged = true;
		}
	}

	public int getOverlayColor() {
		return overlayColor;
	}

	public void setOverlayColor(int overlayColor) {
		this.overlayColor = overlayColor;
	}

	public View getBlurredView() {
		return blurredView;
	}

	public void setBlurredView(View blurredView) {
		this.blurredView = blurredView;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		if (!isInEditMode()) {
			if (blurredView != null) {
				if (prepareForBlurring()) {

					Drawable blurredViewBackground = blurredView.getBackground();
					if (blurredViewBackground != null && blurredViewBackground instanceof ColorDrawable) {
						bitmapToBlur.eraseColor(((ColorDrawable) blurredViewBackground).getColor());
					} else {
						bitmapToBlur.eraseColor(Color.TRANSPARENT);
					}

					blurredView.draw(blurringCanvas);
					blur();

					canvas.save();
					canvas.translate(blurredView.getX() - getX(), blurredView.getY() - getY());
					canvas.scale(downsampleFactor, downsampleFactor);
					canvas.drawBitmap(blurredBitmap, 0, 0, null);
					canvas.restore();

				}
				canvas.drawColor(overlayColor);
			}
		}
		super.onDraw(canvas);
	}

	private boolean prepareForBlurring() {
		int width = blurredView.getWidth();
		int height = blurredView.getHeight();


		if (blurringCanvas == null || hasDownsampleFactorChanged || blurredViewWidth != width || blurredViewHeight != height) {
			hasDownsampleFactorChanged = false;

			blurredViewWidth = width;
			blurredViewHeight = height;

			int scaledWidth = width / downsampleFactor;
			int scaledHeight = height / downsampleFactor;

			//recalculate to avoid renderscript artifacts
			scaledWidth = scaledWidth - scaledWidth % 4 + 4;
			scaledHeight = scaledHeight - scaledHeight % 4 + 4;

			if (blurredBitmap == null || blurredBitmap.getWidth() != scaledWidth || blurredBitmap.getHeight() != scaledHeight) {
				bitmapToBlur = Bitmap.createBitmap(scaledWidth, scaledHeight, Bitmap.Config.ARGB_8888);
				if (bitmapToBlur == null) {
					return false;
				}

				blurredBitmap= Bitmap.createBitmap(scaledWidth, scaledHeight, Bitmap.Config.ARGB_8888);
				if (blurredBitmap == null) {
					return false;
				}
			}

			blurringCanvas = new Canvas(bitmapToBlur);
			blurringCanvas.scale(1f/downsampleFactor, 1f/downsampleFactor);
			blurInput = Allocation.createFromBitmap(rs, bitmapToBlur, Allocation.MipmapControl.MIPMAP_NONE, Allocation.USAGE_SCRIPT);
			blurOutput = Allocation.createTyped(rs, blurInput.getType());
		}

		return true;
	}

	@Override
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		if (rs != null) {
			rs.destroy();
		}
	}

	protected void blur() {
		blurInput.copyFrom(bitmapToBlur);
		blurScript.setInput(blurInput);
		blurScript.forEach(blurOutput);
		blurOutput.copyTo(blurredBitmap);
	}

	private void init(Context context, AttributeSet attrs) {
		if (!isInEditMode()) {
			initRenderscript(context);
		}

		TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.BlurringTextView);
		try {
			int radius = typedArray.getInteger(R.styleable.BlurringTextView_blurRadius, DEFAULT_BLUR_RADIUS);
			int factor = typedArray.getInteger(R.styleable.BlurringTextView_downsampleFactor, DEFAULT_DOWNSAMPLE_FACTOR);
			int color = context.getResources().getColor(android.R.color.transparent);

			setBlurRadius(radius);
			setDownsampleFactor(factor);
			setOverlayColor(color);

		} finally {
			typedArray.recycle();
		}
	}

	private void initRenderscript(Context context) {
		rs = RenderScript.create(context);
		blurScript = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));
	}
}
