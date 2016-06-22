package com.trs.app;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import com.trs.constants.Constants;
import com.trs.fragment.*;
import com.trs.types.Channel;
import com.trs.util.log.Log;
import net.endlessstudio.util.Util;

import java.util.HashMap;

/**
 * Created by john on 14-3-11.
 */
public class FragmentFactory {
    public static String TAG = "FragmentFactory";
    private static HashMap<String, String> sTypeNameMap;

    public static Fragment createFragment(Context context, Channel c) {
        Fragment cachedFragment = FragmentCache.getInstance().get(c);
        if (cachedFragment != null) {
            return cachedFragment;
        }

        String typeCode = c.getType();
        String name = getTypeNameMap(context).get(typeCode);
        android.util.Log.d("typeCode", typeCode + "--" + name);
        android.util.Log.d("name", name);
        //Link type channel can not change type, just 0
        if ("0".equals(typeCode) && c.getUrl().endsWith(".html")) {
            name = WebViewFragment.class.getName();
        }

        if (name != null && name.length() > 0) {
            try {
                Fragment fragment;
                fragment = Fragment.instantiate(context, name);
                // 通过配置初始化一个fragment.

                // 给fragment赋值.
                initFragment(fragment, c);
                FragmentCache.getInstance().put(c, fragment);
                return fragment;
            } catch (Exception e) {
                Log.w(TAG, String.format("Instant fragment %s error %s", name, e));
            }
        }

        return null;
    }

    private static void initFragment(Fragment fragment, Channel c) {
        if (fragment == null) {
            return;
        }

        if (fragment instanceof AbsTRSFragment) {
            Bundle bundle = fragment.getArguments();
            bundle = bundle == null ? new Bundle() : bundle;
            bundle.putString(AbsTRSFragment.EXTRA_TITLE, c.getTitle());
            fragment.setArguments(bundle);
        }

        if (fragment instanceof AbsUrlFragment) {
            Bundle bundle = fragment.getArguments();
            bundle = bundle == null ? new Bundle() : bundle;
            bundle.putString(AbsUrlFragment.EXTRA_URL, c.getUrl());
            fragment.setArguments(bundle);
        }

        if (fragment instanceof AbsWCMListFragment) {
            Bundle bundle = fragment.getArguments();
            bundle = bundle == null ? new Bundle() : bundle;
            fragment.setArguments(bundle);
        }

        if (fragment instanceof GridFragment) {
            Bundle bundle = fragment.getArguments();
            bundle = bundle == null ? new Bundle() : bundle;
            fragment.setArguments(bundle);
        }

        if (fragment instanceof TabFragment || fragment instanceof DocumentListFragment) {
            Bundle bundle = fragment.getArguments();
            bundle = bundle == null ? new Bundle() : bundle;
            bundle.putString(TabFragment.EXTRA_CATEGORY, String.valueOf(c.getId()));
            fragment.setArguments(bundle);
        }
    }

    private static HashMap<String, String> getTypeNameMap(Context context) {
        if (sTypeNameMap == null) {
            sTypeNameMap = new HashMap<String, String>();

            sTypeNameMap.putAll(Util.simpleProperty2HashMap(context, Constants.BASE_TYPE_FRAGMENT_MAP_PATH));
            sTypeNameMap.putAll(Util.simpleProperty2HashMap(context, Constants.EXT_TYPE_FRAGMENT_MAP_PATH));
        }

        return sTypeNameMap;
    }
}
