package com.trs.main.slidingmenu;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Toast;
import com.trs.app.TRSFragmentActivity;
import com.trs.fragment.DocumentListFragment;
import com.trs.mobile.R;

/**
 * Created by john on 14-3-11.
 */
public class SlidingMenuMainActivity extends TRSFragmentActivity implements MenuFragment.DisplayFragmentListener {
	public static final String EXTRA_CHANNEL = "channel";
	private Fragment mLeftMenuFragment;
	private Fragment mRightMenuFragment;
	private DrawerLayout mDrawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main_sliding_menu);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer);
        mLeftMenuFragment = createLeftMenu();
        mRightMenuFragment = createRightMenu();

        FragmentManager fm = getSupportFragmentManager();
        fm.beginTransaction()
                .replace(R.id.left_drawer, mLeftMenuFragment)
                //.replace(R.id.right_drawer, mRightMenuFragment)
                .commit();
    }

	public Fragment createLeftMenu(){
		LeftMenuFragment leftMenu = new LeftMenuFragment();
		leftMenu.setDisplayFragmentListener(this);

		return leftMenu;
	}

	public Fragment createRightMenu(){
		return new RightMenuFragment();
	}

    public void onBtnMenuClick(View view){
        showLeftMenu();
    }

    public void onBtnSettingClick(View view){
        mDrawerLayout.openDrawer(Gravity.RIGHT);
    }

    public void showLeftMenu(){
        mDrawerLayout.openDrawer(Gravity.LEFT);
    }

	public void showRightMenu(){
        mDrawerLayout.openDrawer(Gravity.RIGHT);
    }

    public void hideLeftMenu(){
        mDrawerLayout.closeDrawer(Gravity.LEFT);
    }

	public void hideRightMenu(){
        mDrawerLayout.closeDrawer(Gravity.RIGHT);
    }

	public void toglleLeftMenu(){
		toggleMenu(Gravity.LEFT);
	}

	public void toglleRightMenu(){
		toggleMenu(Gravity.RIGHT);
	}

    private void toggleMenu(int menuGravity){
        if(mDrawerLayout.isDrawerOpen(menuGravity)){
            mDrawerLayout.closeDrawer(menuGravity);
        }
        else{
            mDrawerLayout.openDrawer(menuGravity);
        }
    }

	private boolean isLeftMenuOpen(){
		return mDrawerLayout.isDrawerOpen(Gravity.LEFT);
	}

	private boolean isRightMenuOpen(){
		return mDrawerLayout.isDrawerOpen(Gravity.RIGHT);
	}

    public Fragment getLeftMenu(){
        return mLeftMenuFragment;
    }

    private long lastBackKeyDownTick = 0;
    public static final long MAX_DOUBLE_BACK_DURATION = 1500;
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK){
			long currentTick = System.currentTimeMillis();

			if(currentTick - lastBackKeyDownTick > MAX_DOUBLE_BACK_DURATION){
				Toast.makeText(this, "再按一次退出", Toast.LENGTH_SHORT).show();
				lastBackKeyDownTick = currentTick;

				return true;
			}
        }

        return super.onKeyDown(keyCode, event);
    }

	@Override
	public void displayFragment(String title, Fragment fragment) {
		if(!isFinishing()){

			if(fragment != null){
				FragmentManager fm = getSupportFragmentManager();
				fm.beginTransaction().replace(R.id.content, fragment).commit();
				if(fragment instanceof DocumentListFragment){
					((DocumentListFragment) fragment).notifyDisplay();
				}
			}

			mDrawerLayout.closeDrawer(Gravity.LEFT);
			mDrawerLayout.closeDrawer(Gravity.RIGHT);
		}
	}
}
