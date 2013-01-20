package meenakshi.project.meenakshiocr;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AbsoluteLayout;

public class ScalingLayout extends AbsoluteLayout {
	
	int _virtualWidth, _virtualHeight;
	private int nchildren;

	public ScalingLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
		
		TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.virtualDimensions, defStyle, 0);

		_virtualWidth  = a.getInt(R.styleable.virtualDimensions_virtualWidth, 0);
		_virtualHeight  = a.getInt(R.styleable.virtualDimensions_virtualHeight, 0);
	}
	
	
	
	// Overridden to retain aspect of this layout view
	@Override
	protected void onMeasure (int widthMeasureSpec, int heightMeasureSpec) {
	    double aspect = widthMeasureSpec / (double)heightMeasureSpec;
	    // Those are from XML layout
	    double virtualAspect = _virtualWidth / (double)_virtualHeight;
	    int width, height;

	    measureChildren(widthMeasureSpec, heightMeasureSpec);

	    if(aspect > virtualAspect) {
	        height = heightMeasureSpec;
	        width = (int) (height * virtualAspect);
	    } else {
	        width = widthMeasureSpec;
	        height = (int) (width / virtualAspect);
	    }

	    setMeasuredDimension(width, height);
	}

	@Override
	@SuppressWarnings("deprecation")
	protected void onLayout (boolean changed, int left, int top, int right, int bottom) {
	    double factor = (right - left) / (double)_virtualWidth;

	    nchildren = getChildCount();

	    for(int i = 0; i < nchildren; i++) {
	        View child = getChildAt(i);
	        LayoutParams lp = (LayoutParams) child.getLayoutParams();
	        // Scale child according to given space
	        child.layout((int)(lp.x * factor),
	        		(int)(lp.y * factor),
	        		(int)((lp.x + child.getMeasuredWidth()) * factor),
	        		(int)((lp.y + child.getMeasuredHeight()) * factor) );
	    }
	}
}
