package com.app.phonesafe.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.format.Formatter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.app.phonesafe.Config;
import com.app.phonesafe.R;
import com.app.phonesafe.db.ProcessInfo;
import com.app.phonesafe.engine.ProcessInfoProvider;
import com.app.phonesafe.utils.SPUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 14501_000 on 2016/8/10.
 */
public class ProcessManageActivity extends Activity implements View.OnClickListener{
    TextView tv_process_count;
    TextView tv_memory_info;
    TextView tv_des;
    ListView lv_processList;
    Button bt_select_all;
    Button bt_select_reverse;
    Button bt_clear;
    Button bt_setting;

    private int mProcessCount;
    private long mAvailSpace;
    private String mStrTotalSpace;

    ProcessInfo processInfo;
    List<ProcessInfo> processInfoList;
    private ArrayList<ProcessInfo> mSystemList;
    private ArrayList<ProcessInfo> mCustomerList;

    MyAdapter adapter;

    Handler mHandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            adapter=new MyAdapter();
            lv_processList.setAdapter(adapter);
            if(tv_des!=null && mCustomerList!=null){
                tv_des.setText("用户应用("+mCustomerList.size()+")");
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_processmanager);
        initUI();
        initTitleData();
        initListData();
    }
    private void initUI() {
        tv_process_count = (TextView) findViewById(R.id.tv_process_count);
        tv_memory_info = (TextView) findViewById(R.id.tv_memory_info);

        tv_des = (TextView) findViewById(R.id.tv_des);

        lv_processList = (ListView) findViewById(R.id.lv_process_list);

        bt_select_all = (Button) findViewById(R.id.bt_select_all);
        bt_select_reverse = (Button) findViewById(R.id.bt_select_reverse);
        bt_clear = (Button)  findViewById(R.id.bt_clear);
        bt_setting = (Button) findViewById(R.id.bt_setting);

        mSystemList=new ArrayList<ProcessInfo>();
        mCustomerList=new ArrayList<ProcessInfo>();

        bt_select_all.setOnClickListener(this);
        bt_select_reverse.setOnClickListener(this);
        bt_clear.setOnClickListener(this);
        bt_setting.setOnClickListener(this);

        lv_processList.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if(mCustomerList!=null && mSystemList!=null){
                    if(firstVisibleItem>=mCustomerList.size()+1){
                        //滚动到了系统条目
                        tv_des.setText("系统进程("+mSystemList.size()+")");
                    }else{
                        //滚动到了用户应用条目
                        tv_des.setText("应用进程("+mCustomerList.size()+")");
                    }
                }
            }
        });

        lv_processList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(position==0||position==mCustomerList.size()+1){
                    return;
                }else{
                    if(position<mCustomerList.size()+1){
                        processInfo=mCustomerList.get(position-1);
                    }else{
                        processInfo=mSystemList.get(position-mCustomerList.size()-2);
                    }
                    if(processInfo!=null){
                        if(!processInfo.packageName.equals(getPackageName())){
                            processInfo.isCheck=!processInfo.isCheck;
                            CheckBox cb_box= (CheckBox) view.findViewById(R.id.cb_box);
                            cb_box.setChecked(processInfo.isCheck);
                        }
                    }
                }
            }
        });

    }
    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.bt_select_all:
                selectAll();
                break;
            case R.id.bt_select_reverse:
                selectReverse();
                break;
            case R.id.bt_clear:
                clear();
                break;
            case R.id.bt_setting:
                setting();
                break;
        }
    }

    private void setting() {
        Intent intent=new Intent(this,ProcessSettingActivity.class);
        startActivityForResult(intent,0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(adapter!=null){
            adapter.notifyDataSetChanged();
        }
    }

    private void selectAll() {
        for(ProcessInfo info:mCustomerList){
            if(info.getPackageName().equals(getPackageName())){
                continue;
            }else{
                info.isCheck=true;
            }
        }
        for(ProcessInfo info:mSystemList){
            info.isCheck=true;
        }
        if(adapter!=null){
            adapter.notifyDataSetChanged();
        }
    }


    private void selectReverse() {
        for(ProcessInfo info:mCustomerList){
            if(info.getPackageName().equals(getPackageName())){
                continue;
            }else{
                info.isCheck=!processInfo.isCheck;
            }
        }
        for(ProcessInfo info:mSystemList){
            info.isCheck=!info.isCheck;
        }
        if(adapter!=null){
            adapter.notifyDataSetChanged();
        }
    }

    private void clear() {
        long totalReleaseSpace = 0;
        //1,获取选中进程
        //2,创建一个记录需要杀死的进程的集合
        List<ProcessInfo> killProcessList = new ArrayList<ProcessInfo>();
        for(ProcessInfo processInfo:mCustomerList){
            if(processInfo.getPackageName().equals(getPackageName())){
                continue;
            }
            if(processInfo.isCheck){
                //3,记录需要杀死的用户进程
                killProcessList.add(processInfo);
            }
        }
        for(ProcessInfo processInfo:mSystemList){
            if(processInfo.isCheck){
                //4,记录需要杀死的系统进程
                killProcessList.add(processInfo);
            }
        }

        for(ProcessInfo info:killProcessList){
            if(mCustomerList.contains(info)){
                mCustomerList.remove(info);
            }
            if(mSystemList.contains(info)){
                mSystemList.remove(info);
            }
            //杀死记录在killProcessList中的进程
            ProcessInfoProvider.killProcess(this,info);
            totalReleaseSpace+=info.getMemSize();
        }
        if(adapter!=null){
            adapter.notifyDataSetChanged();
        }
        mProcessCount-=killProcessList.size();
        mAvailSpace+=totalReleaseSpace;
        tv_process_count.setText("进程总数:"+mProcessCount);
        tv_memory_info.setText("剩余/总共"+Formatter.formatFileSize(this, mAvailSpace)+"/"+mStrTotalSpace);
        String totalRelease=Formatter.formatFileSize(this,totalReleaseSpace);
        Toast.makeText(ProcessManageActivity.this,
                "杀死了"+killProcessList.size()+"个进程,释放了"+totalRelease+"空间",
                Toast.LENGTH_SHORT).show();
    }




    private void initTitleData() {
        mProcessCount= ProcessInfoProvider.getProcessCount(this);
        tv_process_count.setText("进程总数:"+mProcessCount);

        //获取可用内存大小,并且格式化
        mAvailSpace=ProcessInfoProvider.getAvailSpace(this);
        String strAvailSpace= Formatter.formatFileSize(this,mAvailSpace);

        //总运行内存大小,并且格式化
        long totalSpace = ProcessInfoProvider.getTotalSpace(this);
        mStrTotalSpace = Formatter.formatFileSize(this, totalSpace);

        tv_memory_info.setText("剩余/总共:" +strAvailSpace+"/"+mStrTotalSpace);
    }

    private void initListData() {
        new Thread(){
            @Override
            public void run() {
                super.run();
                processInfoList=ProcessInfoProvider.getProcessInfo(getApplicationContext());
                for(ProcessInfo info:processInfoList){
                    if(info.isSystem){
                        mSystemList.add(info);
                    }else{
                        mCustomerList.add(info);
                    }
                }
                mHandler.sendEmptyMessage(0);
            }
        }.start();
    }


    class MyAdapter extends BaseAdapter{
        //获取数据适配器中条目类型的总数,修改成两种(纯文本,图片+文字)
        @Override
        public int getViewTypeCount() {
            return super.getViewTypeCount()+1;
        }

        //指定索引指向的条目类型,条目类型状态码指定(0(复用系统),1)
        @Override
        public int getItemViewType(int position) {
            if(position == 0 || position == mCustomerList.size()+1){
                //返回0,代表纯文本条目的状态码
                return 0;
            }else{
                //返回1,代表图片+文本条目状态码
                return 1;
            }
        }

        @Override
        public int getCount() {
            if(SPUtils.getBoolean(getApplicationContext(), Config.SHOW_SYSTEM,false)){
                return  mCustomerList.size()+1;
            }else{
                return  mCustomerList.size()+mSystemList.size()+2;
            }

        }

        @Override
        public ProcessInfo getItem(int position) {
            if(position == 0 || position == mCustomerList.size()+1){
                return null;
            }else{
                if(position<mCustomerList.size()+1){
                    return mCustomerList.get(position-1);
                }else{
                    //返回系统进程对应条目的对象
                    return mSystemList.get(position - mCustomerList.size()-2);
                }
            }
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            int type = getItemViewType(position);
            if(type == 0) {
                //展示灰色纯文本条目
                ViewTitleHolder holder = null;
                if (convertView == null) {
                    convertView = View.inflate(getApplicationContext(), R.layout.listview_app_item_title, null);
                    holder = new ViewTitleHolder();
                    holder.tv_title = (TextView) convertView.findViewById(R.id.tv_title);
                    convertView.setTag(holder);
                } else {
                    holder = (ViewTitleHolder) convertView.getTag();
                }
                if (position == 0) {
                    holder.tv_title.setText("用户进程(" + mCustomerList.size() + ")");
                } else {
                    holder.tv_title.setText("系统进程(" + mSystemList.size() + ")");
                }
                return convertView;
            }else{
                ViewHolder holder = null;
                if(convertView == null){
                    convertView = View.inflate(getApplicationContext(), R.layout.listview_process_item, null);
                    holder = new ViewHolder();
                    holder.iv_icon = (ImageView)convertView.findViewById(R.id.iv_icon);
                    holder.tv_name = (TextView)convertView.findViewById(R.id.tv_name);
                    holder.tv_memory_info = (TextView) convertView.findViewById(R.id.tv_memory_info);
                    holder.cb_box = (CheckBox) convertView.findViewById(R.id.cb_box);
                    convertView.setTag(holder);
                }else{
                    holder= (ViewHolder) convertView.getTag();
                }
                holder.iv_icon.setBackgroundDrawable(getItem(position).icon);
                holder.tv_name.setText(getItem(position).name);
                String strSize = Formatter.formatFileSize(getApplicationContext(), getItem(position).memSize);
                holder.tv_memory_info.setText(strSize);

                //本进程不能被选中,所以先将checkbox隐藏掉
                if(getItem(position).packageName.equals(getPackageName())){
                    holder.cb_box.setVisibility(View.GONE);
                }else{
                    holder.cb_box.setVisibility(View.VISIBLE);
                }
                holder.cb_box.setChecked(getItem(position).isCheck);
                return convertView;
            }
        }
    }
    static class ViewHolder{
        ImageView iv_icon;
        TextView tv_name;
        TextView tv_memory_info;
        CheckBox cb_box;
    }

    static class ViewTitleHolder{
        TextView tv_title;
    }

}
