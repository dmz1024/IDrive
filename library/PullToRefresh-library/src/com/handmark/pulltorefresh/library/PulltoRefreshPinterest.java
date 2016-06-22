package com.handmark.pulltorefresh.library;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import com.etsy.android.grid.StaggeredGridView;
import com.handmark.pulltorefresh.library.internal.EmptyViewMethodAccessor;

/**
 * Created by wubingqian on 14-3-13.
 */
public class PulltoRefreshPinterest extends PullToRefreshAdapterViewBase<StaggeredGridView>{

    public PulltoRefreshPinterest(Context context) {
        super(context);
    }

    public PulltoRefreshPinterest(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PulltoRefreshPinterest(Context context, Mode mode) {
        super(context, mode);
    }

    public PulltoRefreshPinterest(Context context, Mode mode, AnimationStyle animStyle) {
        super(context, mode, animStyle);
    }

    @Override
    public Orientation getPullToRefreshScrollDirection() {
        return Orientation.VERTICAL;
    }

	@Override
	protected final StaggeredGridView createRefreshableView(Context context, AttributeSet attrs) {
		final StaggeredGridView gv;
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
			gv = new InternalGridViewSDK9(context, attrs);
		} else {
			gv = new InternalGridView(context, attrs);
		}

		gv.setId(R.id.gridview);
		return gv;
	}

	class InternalGridView extends StaggeredGridView implements EmptyViewMethodAccessor {

		public InternalGridView(Context context, AttributeSet attrs) {
			super(context, attrs);
		}

		@Override
		public void setEmptyView(View emptyView) {
			PulltoRefreshPinterest.this.setEmptyView(emptyView);
		}

		@Override
		public void setEmptyViewInternal(View emptyView) {
			super.setEmptyView(emptyView);
		}
	}

	@TargetApi(9)
	final class InternalGridViewSDK9 extends InternalGridView {

		public InternalGridViewSDK9(Context context, AttributeSet attrs) {
			super(context, attrs);
		}

		@Override
		protected boolean overScrollBy(int deltaX, int deltaY, int scrollX, int scrollY, int scrollRangeX,
									   int scrollRangeY, int maxOverScrollX, int maxOverScrollY, boolean isTouchEvent) {

			final boolean returnValue = super.overScrollBy(deltaX, deltaY, scrollX, scrollY, scrollRangeX,
					scrollRangeY, maxOverScrollX, maxOverScrollY, isTouchEvent);

			// Does all of the hard work...
			OverscrollHelper.overScrollBy(PulltoRefreshPinterest.this, deltaX, scrollX, deltaY, scrollY, isTouchEvent);

			return returnValue;
		}
	}

}
