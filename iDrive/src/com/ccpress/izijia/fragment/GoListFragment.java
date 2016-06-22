package com.ccpress.izijia.fragment;

import android.content.Intent;
import com.ccpress.izijia.Constant;
import com.ccpress.izijia.activity.LinesDetailUserUploadActivity;
import com.ccpress.izijia.activity.WebViewActivity;
import com.trs.types.ListItem;
import com.trs.util.StringUtil;

/**
 * Created by Wu Jingyu
 * Date: 2015/9/7
 * Time: 10:55
 */
public class GoListFragment extends HomeiDriveListFragment {
    @Override
    protected void onItemClick(ListItem item) {
        if(getActivity() != null && !StringUtil.isEmpty(item.getType())){
            if(item.getType().equals(Constant.CType_Des)){//目的地
                Intent intent = new Intent(getActivity(), LinesDetailUserUploadActivity.class);
                intent.putExtra(LinesDetailUserUploadActivity.EXTRA_ACT_TYPE, LinesDetailUserUploadActivity.Destination);
                intent.putExtra(LinesDetailUserUploadActivity.EXTRA_LID, item.getId());
                getActivity().startActivity(intent);
            } else if(item.getType().equals(Constant.CType_Line)){//线路
                Intent intent = new Intent(getActivity(), LinesDetailUserUploadActivity.class);
                intent.putExtra(LinesDetailUserUploadActivity.EXTRA_LID, item.getId());
                intent.putExtra(LinesDetailUserUploadActivity.EXTRA_ACT_TYPE, LinesDetailUserUploadActivity.OfficialLines);
                getActivity().startActivity(intent);
            }
        }
    }
}
