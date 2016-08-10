package com.app.phonesafe.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.app.phonesafe.R;
import com.app.phonesafe.db.BlackNumberDao;
import com.app.phonesafe.db.BlackNumberInfo;

import java.util.List;

/**
 * Created by 14501_000 on 2016/8/7.
 */
public class BlackNumberActivity extends Activity{
    Button bt_add;
    ListView lv_blacknumber;
    BlackNumberDao blackNumberDao;
    private List<BlackNumberInfo> mBlackNumberList;
    MyAdapter adapter;
    private int mode = 1;
    private boolean mIsLoad = false;
    private int mCount;//数据库索引位置


    Handler mHandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(adapter==null) {
                adapter = new MyAdapter();
                lv_blacknumber.setAdapter(adapter);
            }else{
                adapter.notifyDataSetChanged();
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blacknumber);
        initUI();
        initData();
    }

    private void initData() {
        new Thread(){
            @Override
            public void run() {
                super.run();
                //1.获取数据库对象
                blackNumberDao=BlackNumberDao.getInstance(getApplicationContext());
                mBlackNumberList=blackNumberDao.find(0);
                mCount=blackNumberDao.getCount();
                mHandler.sendEmptyMessage(0);
            }
        }.start();
    }

    private void initUI() {
        bt_add= (Button) findViewById(R.id.bt_add);
        lv_blacknumber= (ListView) findViewById(R.id.lv_black_number_list);

        bt_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog();
            }
        });

        //监听滚动状态
        lv_blacknumber.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
//				OnScrollListener.SCROLL_STATE_FLING	飞速滚动
//				OnScrollListener.SCROLL_STATE_IDLE	 空闲状态
//				OnScrollListener.SCROLL_STATE_TOUCH_SCROLL	拿手触摸着去滚动状态

                if(mBlackNumberList!=null){
                    //条件一:滚动到停止状态
                    //条件二:最后一个条目可见(最后一个条目的索引值>=数据适配器中集合的总条目个数-1)
                    if(scrollState== AbsListView.OnScrollListener.SCROLL_STATE_IDLE
                            &&lv_blacknumber.getLastVisiblePosition()>=mBlackNumberList.size()-1
                            &&!mIsLoad){
                        /*mIsLoad防止重复加载的变量
						如果当前正在加载mIsLoad就会为true,本次加载完毕后,再将mIsLoad改为false
						如果下一次加载需要去做执行的时候,会判断上诉mIsLoad变量,是否为false,如果为true,
						就需要等待上一次加载完成,将其值改为false后再去加载*/

                        //如果条目总数大于集合大小的时,才可以去继续加载更多
                        if(mCount>mBlackNumberList.size()){
                            //加载下一页数据
                            new Thread() {
                                @Override
                                public void run() {
                                    super.run();
                                    //1.获取操作黑名单数据库对象
                                    blackNumberDao=BlackNumberDao.getInstance(getApplicationContext());
                                    //2,查询部分数据
                                    List<BlackNumberInfo> moreData = blackNumberDao.find(mBlackNumberList.size());
                                    //3,添加下一页数据的过程
                                    mBlackNumberList.addAll(moreData);
                                    //4,通知数据适配器刷新
                                    mHandler.sendEmptyMessage(0);
                                }
                            }.start();
                        }
                    }

                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });


    }
    public void showDialog(){
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        final AlertDialog dialog=builder.create();
        View view=View.inflate(getApplicationContext(),R.layout.dialog_add_blacknumber,null);
        dialog.setView(view,0,0,0,0);

        final EditText et_phone = (EditText) view.findViewById(R.id.et_phone);
        RadioGroup rg_group = (RadioGroup) view.findViewById(R.id.rg_group);

        Button bt_submit = (Button) view.findViewById(R.id.bt_submit);
        Button bt_cancel = (Button)view.findViewById(R.id.bt_cancel);

        //监听其选中条目的切换过程
        rg_group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rb_sms:
                        //拦截短信
                        mode = 1;
                        break;
                    case R.id.rb_phone:
                        //拦截电话
                        mode = 2;
                        break;
                    case R.id.rb_all:
                        //拦截所有
                        mode = 3;
                        break;
                }
            }
        });
        bt_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //1,获取输入框中的电话号码
                String phone = et_phone.getText().toString();
                if(!TextUtils.isEmpty(phone)){
                    //2,数据库插入当前输入的拦截电话号码
                    blackNumberDao.insert(phone, mode+"");
                    //3,让数据库和集合保持同步(1.数据库中数据重新读一遍,2.手动向集合中添加一个对象(插入数据构建的对象))
                    BlackNumberInfo blackNumberInfo = new BlackNumberInfo();
                    blackNumberInfo.setPhone(phone);
                    blackNumberInfo.setMode(mode+"");
                    //4,将对象插入到集合的最顶部
                    mBlackNumberList.add(0, blackNumberInfo);
                    //5,通知数据适配器刷新(数据适配器中的数据有改变了)
                    if(adapter!=null){
                        adapter.notifyDataSetChanged();
                    }
                    //6,隐藏对话框
                    dialog.dismiss();
                }else{
                    Toast.makeText(getApplicationContext(),"请输入拦截号码",Toast.LENGTH_SHORT).show();
                }
            }
        });
        bt_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();

    }
    class MyAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return mBlackNumberList.size();
        }

        @Override
        public Object getItem(int position) {
            return mBlackNumberList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            //1.复用convertView
            //复用viewHolder步骤一
            ViewHolder holder=null;
            if(convertView==null) {
                convertView = View.inflate(getApplicationContext(), R.layout.blacknumber_item, null);
                //2,减少findViewById()次数
                //复用viewHolder步骤三
                holder = new ViewHolder();
                holder.tv_phone = (TextView) convertView.findViewById(R.id.tv_phone);
                holder.tv_mode = (TextView) convertView.findViewById(R.id.tv_mode);
                holder.iv_delete = (ImageView) convertView.findViewById(R.id.iv_delete);
                //复用viewHolder步骤五
                convertView.setTag(holder);
            }else{
                holder= (ViewHolder) convertView.getTag();
            }
                holder.tv_phone.setText(mBlackNumberList.get(position).getPhone());
                int mode=Integer.parseInt(mBlackNumberList.get(position).getMode());
                switch (mode){
                    case 1:
                        holder.tv_mode.setText("拦截短信");break;
                    case 2:
                        holder.tv_mode.setText("拦截电话");break;
                    case 3:
                        holder.tv_mode.setText("拦截所有");break;
                }
                holder.iv_delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        blackNumberDao.delete(mBlackNumberList.get(position).getPhone());
                        mBlackNumberList.remove(position);
                        if(adapter!=null){
                            adapter.notifyDataSetChanged();
                        }
                    }
                });

            return convertView;
        }
    }
    //复用viewHolder步骤二
    static class ViewHolder{
        TextView tv_phone;
        TextView tv_mode;
        ImageView iv_delete;
    }
}
