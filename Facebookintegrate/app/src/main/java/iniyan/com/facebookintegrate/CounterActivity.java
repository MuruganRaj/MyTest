package iniyan.com.facebookintegrate;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import iniyan.com.facebookintegrate.model.Getgroups;
import iniyan.com.facebookintegrate.model.GetgroupsResponse;
import iniyan.com.facebookintegrate.model.GroupCount;
import iniyan.com.facebookintegrate.model.GroupCountResponse;
import iniyan.com.facebookintegrate.model.JoinAddResponse;
import iniyan.com.facebookintegrate.model.JoinAddResponseMessage;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

public class CounterActivity extends AppCompatActivity  implements View.OnClickListener,IAddGroupJion {
    String date_time;
    Calendar calendar;
    SimpleDateFormat simpleDateFormat;
    EditText et_hours;
    SharedPreferences mpref;
    SharedPreferences.Editor mEditor;
    TextView tv_count ;
    private Button btn_start, btn_cancel;
    private TextView tv_timer;
    private CompositeDisposable disposable = new CompositeDisposable();
    private ApiService apiService;
    private RecyclerView recyclerView;
    private GroupAdapter mAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_counter);


        apiService = ApiClient.getClient(getApplicationContext()).create(ApiService.class);



        tv_count = (TextView)findViewById(R.id.tvCount);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());



        getGroup();
        getGroupCount();



//        init();
//        listener();
    }


    private void getGroup(){
        disposable.add(apiService.getGroup().subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSingleObserver<Getgroups>() {
                    @Override
                    public void onSuccess(Getgroups getgroups) {
                        GetgroupsResponse[]  response = getgroups.getResponse();

                        Log.e("reeeee",""+response);

                        mAdapter = new GroupAdapter(response,CounterActivity.this);
                       recyclerView.setAdapter(mAdapter);

                        mAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onError(Throwable e) {

                    }
                }));

    }



    private void getGroupCount(){
        disposable.add(apiService.getGroupCount().subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribeWith(new DisposableSingleObserver<GroupCount>() {
            @Override
            public void onSuccess(GroupCount groupCount) {

                GroupCountResponse[] gp = groupCount.getResponse();

            for(int i=0;i<gp.length;i++){
                int count =gp[i].getGroup_count();
                tv_count.setText("Total Group : "+count);

            }

            }

            @Override
            public void onError(Throwable e) {

            }
        }));

    }



    public  void addJoinGroup(int group_id,int customer_id,String join_status,int no_multy,String payment_status,final GetgroupsResponse[] getgroupsResponse){

        disposable.add(apiService.addGroupJoin(group_id,customer_id,join_status,no_multy,payment_status)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSingleObserver<JoinAddResponse>() {
                    @Override
                    public void onSuccess(JoinAddResponse s) {

                        JoinAddResponseMessage[] re = s.getMessage();

                        for(int i=0;i<re.length;i++){

                            String result = re[0].getResult();

                            Toast.makeText(CounterActivity.this, result, Toast.LENGTH_SHORT).show();
                            getGroup();

                        }




//                        mAdapter.notifyDataSetChanged();


                     //   mAdapter.notifyDataSetChanged();


//                        mAdapter = new GroupAdapter(getgroupsResponse,CounterActivity.this);
//                        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
//                        recyclerView.setLayoutManager(mLayoutManager);
//                        recyclerView.setItemAnimator(new DefaultItemAnimator());
//                        recyclerView.setAdapter(mAdapter);
//                        mAdapter.notifyDataSetChanged();



                    }

                    @Override
                    public void onError(Throwable e) {

                    }
                }));

    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void addjoin(int groupId, int customer_id, String join_status, int no_multy, String payment_status,GetgroupsResponse[] getgroupsResponse) {
        addJoinGroup(groupId,customer_id,join_status,no_multy,payment_status,getgroupsResponse);


    }

//    private void init() {
//        btn_start = (Button) findViewById(R.id.btn_timer);
//        tv_timer = (TextView) findViewById(R.id.tv_timer);
//        et_hours = (EditText) findViewById(R.id.et_hours);
//        btn_cancel = (Button) findViewById(R.id.btn_cancel);
//
//
//
//        mpref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
//        mEditor = mpref.edit();
//
//        try {
//            String str_value = mpref.getString("data", "");
//            if (str_value.matches("")) {
//                et_hours.setEnabled(true);
//                btn_start.setEnabled(true);
//                tv_timer.setText("");
//
//            } else {
//
//                if (mpref.getBoolean("finish", false)) {
//                    et_hours.setEnabled(true);
//                    btn_start.setEnabled(true);
//                    tv_timer.setText("");
//                } else {
//
//                    et_hours.setEnabled(false);
//                    btn_start.setEnabled(false);
//                    tv_timer.setText(str_value);
//                }
//            }
//        } catch (Exception e) {
//
//        }
//
//
//
//    }
//
//    private void listener() {
//        btn_start.setOnClickListener(this);
//        btn_cancel.setOnClickListener(this);
//
//    }
//
//    @Override
//    public void onClick(View v) {
//
//        switch (v.getId()) {
//            case R.id.btn_timer:
//
//
//                if (et_hours.getText().toString().length() > 0) {
//
//                    int int_hours = Integer.valueOf(et_hours.getText().toString());
//
//                    if (int_hours<=24) {
//
//
//                        et_hours.setEnabled(false);
//                        btn_start.setEnabled(false);
//
//
//                        calendar = Calendar.getInstance();
//                        simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
//                        date_time = simpleDateFormat.format(calendar.getTime());
//
//                        mEditor.putString("data", date_time).commit();
//                        mEditor.putString("hours", et_hours.getText().toString()).commit();
//
//
//                        Intent intent_service = new Intent(getApplicationContext(), CounterService.class);
//                        startService(intent_service);
//                    }else {
//                        Toast.makeText(getApplicationContext(),"Please select the value below 24 hours",Toast.LENGTH_SHORT).show();
//                    }
///*
//                    mTimer = new Timer();
//                    mTimer.scheduleAtFixedRate(new TimeDisplayTimerTask(), 5, NOTIFY_INTERVAL);*/
//                } else {
//                    Toast.makeText(getApplicationContext(), "Please select value", Toast.LENGTH_SHORT).show();
//                }
//                break;
//
//
//            case R.id.btn_cancel:
//
//
//                Intent intent = new Intent(getApplicationContext(),CounterService.class);
//                stopService(intent);
//
//                mEditor.clear().commit();
//
//                et_hours.setEnabled(true);
//                btn_start.setEnabled(true);
//                tv_timer.setText("");
//
//
//                break;
//
//        }
//
//    }
//
//    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            String str_time = intent.getStringExtra("time");
//            tv_timer.setText(str_time);
//
//        }
//    };
//
//    @Override
//    protected void onResume() {
//        super.onResume();
//        registerReceiver(broadcastReceiver,new IntentFilter(CounterService.str_receiver));
//
//    }
//
//    @Override
//    protected void onPause() {
//        super.onPause();
//        unregisterReceiver(broadcastReceiver);
//    }
}