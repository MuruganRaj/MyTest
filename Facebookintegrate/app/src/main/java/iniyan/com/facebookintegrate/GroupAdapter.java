package iniyan.com.facebookintegrate;

import android.content.Intent;
import android.graphics.Movie;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Handler;

import iniyan.com.facebookintegrate.model.GetgroupsResponse;

/**
 * Created by Murugan on 14-11-2018.
 */

public class GroupAdapter extends RecyclerView.Adapter<GroupAdapter.MyViewHolder> {
    private String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

    GetgroupsResponse[] list = new GetgroupsResponse[0];
    Runnable runnable;
    private android.os.Handler handler = new android.os.Handler();

    public GroupAdapter(GetgroupsResponse[] moviesList) {
        this.list = moviesList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.group_list_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        GetgroupsResponse movie = list[position];

        Log.e("adptaaaa", "" + movie.getExpirydate());
        holder.tvName.setText("" + movie.getFirstName());

        int gcount = 10 - Integer.parseInt(movie.getGroup_count());

        holder.tvCount.setText(gcount + "people  \n  finish the group");

        countDownStart(movie.getExpirydate(), holder);


    }

    @Override
    public int getItemCount() {
        return list.length;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tvTime, tvName, tvCount;
        ImageView im_profile;
        Button btnJoin;

        public MyViewHolder(View itemView) {
            super(itemView);

            tvTime = (TextView) itemView.findViewById(R.id.genre);
            tvCount = (TextView) itemView.findViewById(R.id.year);
            tvName = (TextView) itemView.findViewById(R.id.title);
            btnJoin = (Button) itemView.findViewById(R.id.btnJoin);
            im_profile = (ImageView) itemView.findViewById(R.id.imageView);
        }
    }

    public String parseDateToddMMyyyy(String time) {
        String inputPattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
        String outputPattern = "yyyy-MM-dd HH:mm:ss";
        SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern);
        SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern);

        Date date = null;
        String str = null;

        try {
            date = inputFormat.parse(time);
            str = outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return str;
    }

    private void countDownStart(final String count, final MyViewHolder myViewHolder) {


        final String tempDate = parseDateToddMMyyyy(count);
//     final   long counthour = Long.parseLong(count);
        Log.e("rewwwwwwwww", "" + tempDate);


        runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    handler.postDelayed(this, 1000);

                    SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
                    Date event_date = dateFormat.parse(tempDate);
                    Date current_date = new Date();

                    if (!current_date.after(event_date)) {

                        long diff = event_date.getTime() - current_date.getTime();

                        Log.e("diiddd", "" + diff);
                        long Days = diff / (24 * 60 * 60 * 1000);
                        long Hours = diff / (60 * 60 * 1000) % 24;
                        long Minutes = diff / (60 * 1000) % 60;
                        long Seconds = diff / 1000 % 60;

                        myViewHolder.tvTime.setText(Days + ":" + Hours + ":" + Minutes + ":" + Seconds);
                        myViewHolder.btnJoin.setVisibility(View.VISIBLE);

//                        tv_days.setText(String.format("%02d", Days));
//                        tv_hour.setText(String.format("%02d", Hours));
//                        tv_minute.setText(String.format("%02d", Minutes));
//                        tv_second.setText(String.format("%02d", Seconds));

                    } else {

                        myViewHolder.tvTime.setText("expired");
                        myViewHolder.btnJoin.setVisibility(View.INVISIBLE);
//                        linear_layout_1.setVisibility(View.VISIBLE);
//                        linear_layout_2.setVisibility(View.GONE);
                        handler.removeCallbacks(runnable);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        handler.postDelayed(runnable, 0);
    }

}
