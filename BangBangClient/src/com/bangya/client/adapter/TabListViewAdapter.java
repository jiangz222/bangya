package com.bangya.client.adapter;

import java.util.List;

import com.bangya.client.BBUI.AboutMe;
import com.bangya.client.BBUI.BaseUtil;
import com.bangya.client.Util.Constants;
import com.bangya.client.comm.PushReceiver;
import com.bangya.client.model.SecondTabListViewItem;
import com.joeapp.bangya.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
class ListViewLayout{
    public ImageView icon;
    public TextView title;
    public ImageView notification;
}

public class TabListViewAdapter extends BaseAdapter {
    private LayoutInflater layoutInflater;
    public static int fromMYTrends=1;
    public static int fromAboutMe=2;
    private int direction;
    private List<SecondTabListViewItem> listviewItem;
    public TabListViewAdapter(Context context,int direction,List<SecondTabListViewItem> listviewItem) {
        super();
        this.layoutInflater = LayoutInflater.from(context);
        this.direction = direction; 
        this.listviewItem = listviewItem;
    }
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return this.listviewItem.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup arg2) {
		ListViewLayout listviewLayout= null;

        if (convertView == null){
            convertView = this.layoutInflater.inflate(R.layout.bb_2nd_layer_list_view,null);
            listviewLayout = new ListViewLayout();
            listviewLayout.icon = (ImageView) convertView.findViewById(R.id.snd_layer_list_icon);
            listviewLayout.title = (TextView) convertView.findViewById(R.id.snd_layer_list_title);
            listviewLayout.notification = (ImageView) convertView.findViewById(R.id.notification);
            convertView.setTag(listviewLayout);
        }else{
        	listviewLayout = (ListViewLayout) convertView.getTag();
     }
    listviewLayout.icon.setImageResource(listviewItem.get(position).icon);
    listviewLayout.title.setText(listviewItem.get(position).title);
   if(direction == fromMYTrends){
 	if(PushReceiver.PCCInfo.IsChanged() && PushReceiver.PCCInfo.getChangdDetail().size() !=0){
 		if(position == 0 && PushReceiver.PCCInfo.isDirectionEnable(Constants.PUSH_MSG_DIRECTION_TO_OWNER)){
 			listviewLayout.notification.setImageResource(R.drawable.ui_notification_large);
 	    }
 	   if(position == 1 && PushReceiver.PCCInfo.isDirectionEnable(Constants.PUSH_MSG_DIRECTION_TO_PICKER)){
 		  listviewLayout.notification.setImageResource(R.drawable.ui_notification_large);
 	   }
 	}
 	}else{
 		if(BaseUtil.getVersionNeedUpdateFlag() == true){
 			if(position == (AboutMe.settingPosition-1))
 	 		  listviewLayout.notification.setImageResource(R.drawable.ui_notification_large);
 		}
 	}
		return convertView;
    }

}
