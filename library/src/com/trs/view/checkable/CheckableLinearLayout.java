package com.trs.view.checkable;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.ViewDebug;
import android.widget.Checkable;
import android.widget.LinearLayout;

/**
 * Created by john on 14-2-19.
 */
public class CheckableLinearLayout extends LinearLayout implements CheckableViewGroup{
	private boolean mChecked;
	private boolean mBroadcasting;
	private OnCheckedChangeListener mOnCheckedChangeListener;
	private OnCheckedChangeListener mOnCheckedChangeWidgetListener;
	private boolean mUncheckEnabled = false;

	private static final int[] CHECKED_STATE_SET = {
			android.R.attr.state_checked,
	};

	public CheckableLinearLayout(Context context) {
		super(context);

		initialize();
	}

	public CheckableLinearLayout(Context context, AttributeSet attrs) {
		super(context, attrs);

		initialize();
	}

	private void initialize(){
		setClickable(true);
	}

	public void toggle() {
		if(mChecked && !mUncheckEnabled){
			return;
		}

		setChecked(!mChecked);
	}

	@Override
	public boolean performClick() {
        /*
         * XXX: These are tiny, need some surrounding 'expanded touch area',
         * which will need to be implemented in Button if we only override
         * performClick()
         */

        /* When clicked, toggle the state */
		toggle();
		return super.performClick();
	}

	@ViewDebug.ExportedProperty
	public boolean isChecked() {
		return mChecked;
	}

	/**
	 * <p>Changes the checked state of this button.</p>
	 *
	 * @param checked true to check the button, false to uncheck it
	 */
	public void setChecked(boolean checked) {
		if (mChecked != checked) {
			mChecked = checked;
			refreshDrawableState();

			// Avoid infinite recursions if setChecked() is called from a listener
			if (mBroadcasting) {
				return;
			}

			mBroadcasting = true;
			if (mOnCheckedChangeListener != null) {
				mOnCheckedChangeListener.onCheckedChanged(this, mChecked);
			}

			if (mOnCheckedChangeWidgetListener != null) {
				mOnCheckedChangeWidgetListener.onCheckedChanged(this, mChecked);
			}

			mBroadcasting = false;
		}
	}

	/**
	 * Register a callback to be invoked when the checked state of this button
	 * changes.
	 *
	 * @param listener the callback to call on checked state change
	 */
	public void setOnCheckedChangeListener(OnCheckedChangeListener listener) {
		mOnCheckedChangeListener = listener;
	}

	@Override
	public void setOnCheckedChangeWidgetListener(OnCheckedChangeListener listener) {
		mOnCheckedChangeWidgetListener = listener;
	}

	@Override
	protected int[] onCreateDrawableState(int extraSpace) {
		final int[] drawableState = super.onCreateDrawableState(extraSpace + 1);
		if (isChecked()) {
			mergeDrawableStates(drawableState, CHECKED_STATE_SET);
		}
		return drawableState;
	}

	static class SavedState extends BaseSavedState {
		boolean checked;

		/**
		 * Constructor called from {@link CheckableLinearLayout#onSaveInstanceState()}
		 */
		SavedState(Parcelable superState) {
			super(superState);
		}

		/**
		 * Constructor called from {@link #CREATOR}
		 */
		private SavedState(Parcel in) {
			super(in);
			checked = (Boolean)in.readValue(null);
		}

		@Override
		public void writeToParcel(Parcel out, int flags) {
			super.writeToParcel(out, flags);
			out.writeValue(checked);
		}

		@Override
		public String toString() {
			return "CheckableLinearLayout.SavedState{"
					+ Integer.toHexString(System.identityHashCode(this))
					+ " checked=" + checked + "}";
		}

		public static final Creator<SavedState> CREATOR
				= new Creator<SavedState>() {
			public SavedState createFromParcel(Parcel in) {
				return new SavedState(in);
			}

			public SavedState[] newArray(int size) {
				return new SavedState[size];
			}
		};
	}

	@Override
	public Parcelable onSaveInstanceState() {
		// Force our ancestor class to save its state
		Parcelable superState = super.onSaveInstanceState();

		SavedState ss = new SavedState(superState);

		ss.checked = isChecked();
		return ss;
	}

	@Override
	public void onRestoreInstanceState(Parcelable state) {
		SavedState ss = (SavedState) state;

		super.onRestoreInstanceState(ss.getSuperState());
		setChecked(ss.checked);
		requestLayout();
	}

}
