package com.app.phonesafe.activities;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.app.phonesafe.R;
import com.app.phonesafe.db.CommonNumDao;

import java.util.List;

/**
 * Created by 14501_000 on 2016/8/10.
 */
public class CommonNumberActivity extends Activity {
    ExpandableListView elv_common_number;
    private List<CommonNumDao.Group> mGroup;
    MyAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_common_number);
        initUI();
        initData();
    }

    private void initUI() {
        elv_common_number = (ExpandableListView) findViewById(R.id.elv_common_number);
    }

    /**
     * 给可扩展ListView准备数据,并且填充
     */
    private void initData() {
        CommonNumDao commonNumDao = new CommonNumDao();
        mGroup = commonNumDao.getGroup();
        adapter = new MyAdapter();
        elv_common_number.setAdapter(adapter);

        elv_common_number.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                startCall(adapter.getChild(groupPosition, childPosition).number);
                return false;

            }
        });

    }

    private void startCall(String number) {
        //开启系统的打电话界面
        Intent intent = new Intent(Intent.ACTION_CALL);
        intent.setData(Uri.parse("tel:" + number));
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        startActivity(intent);
    }

    class MyAdapter extends BaseExpandableListAdapter{

        @Override
        public int getGroupCount() {
            return mGroup.size();
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            return mGroup.get(groupPosition).childList.size();
        }

        @Override
        public CommonNumDao.Group getGroup(int groupPosition) {
            return mGroup.get(groupPosition);
        }

        @Override
        public CommonNumDao.Child getChild(int groupPosition, int childPosition) {
            return mGroup.get(groupPosition).childList.get(childPosition);
        }

        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            View view = View.inflate(getApplicationContext(), R.layout.elv_group_item, null);

            TextView textView = (TextView) view.findViewById(R.id.tv_group_name);
            textView.setText(getGroup(groupPosition).name);
            textView.setTextColor(Color.BLUE);
//            textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
            return textView;
        }

        @Override
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            View view = View.inflate(getApplicationContext(), R.layout.elv_child_item, null);
            TextView tv_name = (TextView) view.findViewById(R.id.tv_name);
            TextView tv_number = (TextView) view.findViewById(R.id.tv_number);

            tv_name.setText(getChild(groupPosition, childPosition).name);
            tv_number.setText(getChild(groupPosition, childPosition).number);

            return view;
        }

        //孩子节点是否响应事件
        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }
    }
}
