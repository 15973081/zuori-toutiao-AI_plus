package com.example.viewnews;

import android.content.Context;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;//🌟adpter组件
import android.widget.ImageView;//🌟imageview组件
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.List;

//自定义新闻列表的适配器
public class TabAdapter extends BaseAdapter {

    private List<NewsBean.ResultBean.DataBean> list;

    private Context context;

    //设置正常加载图片的个数
    private int IMAGE_01 = 0;

    private int IMAGE_02 = 1;

    private int IMAGE_03 = 2;

    private int VIEW_COUNT = 3;

    public TabAdapter(Context context, List<NewsBean.ResultBean.DataBean> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    //得到不同item的总数
    @Override
    public int getViewTypeCount() {
        return VIEW_COUNT;
    }

    //得到当前新闻子项item的类型


    //🌟多布局适配，根据图片的数量动态决定展示那种新闻卡片
    @Override
    public int getItemViewType(int position) {
        if (list.get(position).getThumbnail_pic_s() != null &&
                list.get(position).getThumbnail_pic_s02() != null &&
                list.get(position).getThumbnail_pic_s03() != null) {
            return IMAGE_03;
        } else if (list.get(position).getThumbnail_pic_s() != null &&
                list.get(position).getThumbnail_pic_s02() != null) {
            return IMAGE_02;
        }
        return IMAGE_01;
    }

    //提升ListView的运行效率，参数convertView用于将之前加载好的布局进行缓存，以便以后可以重用：https://blog.csdn.net/xiao_ziqiang/article/details/50812471
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (getItemViewType(position) == IMAGE_01) {
            Image01_ViewHolder holder;
            //🌟避免重复布局膨胀和控件查找，优化性能。
            if (convertView == null) {
                convertView = View.inflate(context, R.layout.item_layout01, null);
                holder = new Image01_ViewHolder();
                //查找控件
                holder.author_name = (TextView) convertView.findViewById(R.id.author_name);
                holder.title = (TextView) convertView.findViewById(R.id.title);
                holder.image = (ImageView) convertView.findViewById(R.id.image);

                convertView.setTag(holder);
            } else {
                holder = (Image01_ViewHolder) convertView.getTag();
            }

            //获取数据重新赋值
            holder.title.setText(list.get(position).getTitle());
            holder.author_name.setText(list.get(position).getAuthor_name());

            /**
             * DiskCacheStrategy.NONE： 表示不缓存任何内容。
             */
            Glide.with(context)
                    .load(list.get(position).getThumbnail_pic_s())
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .placeholder(R.mipmap.ic_launcher)
                    .error(R.mipmap.ic_launcher)
                    .into(holder.image);


        } else if (getItemViewType(position) == IMAGE_02) {
            Image02_ViewHolder holder;
            if (convertView == null) {
                convertView = View.inflate(context, R.layout.item_layout02, null);
                holder = new Image02_ViewHolder();
                //查找控件
                holder.image001 = (ImageView) convertView.findViewById(R.id.image001);
                holder.image002 = (ImageView) convertView.findViewById(R.id.image002);
                holder.title = (TextView) convertView.findViewById(R.id.title);
                //将ViewHolder对象存储在View中
                convertView.setTag(holder);

            } else {
                holder = (Image02_ViewHolder) convertView.getTag();
            }
            //获取数据重新赋值
            holder.title.setText(list.get(position).getTitle());
            Glide.with(context)
                    .load(list.get(position).getThumbnail_pic_s())
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .placeholder(R.mipmap.ic_launcher)
                    .error(R.mipmap.ic_launcher)
                    .into(holder.image001);

            Glide.with(context)
                    .load(list.get(position).getThumbnail_pic_s02())
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .placeholder(R.mipmap.ic_launcher)
                    .error(R.mipmap.ic_launcher)
                    .into(holder.image002);

        } else {
            Image03_ViewHolder holder;
            if (convertView == null) {
                convertView = View.inflate(context, R.layout.item_layout03, null);
                holder = new Image03_ViewHolder();
                //查找控件
                holder.image01 = (ImageView) convertView.findViewById(R.id.image01);
                holder.image02 = (ImageView) convertView.findViewById(R.id.image02);
                holder.image03 = (ImageView) convertView.findViewById(R.id.image03);
                holder.title = (TextView) convertView.findViewById(R.id.title);
                convertView.setTag(holder);
            } else {
                holder = (Image03_ViewHolder) convertView.getTag();
            }
            //获取数据重新赋值
            holder.title.setText(list.get(position).getTitle());

            Glide.with(context)
                    .load(list.get(position).getThumbnail_pic_s())
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .placeholder(R.mipmap.ic_launcher)
                    .error(R.mipmap.ic_launcher)
                    .into(holder.image01);

            Glide.with(context)
                    .load(list.get(position).getThumbnail_pic_s02())
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .placeholder(R.mipmap.ic_launcher)
                    .error(R.mipmap.ic_launcher)
                    .into(holder.image02);

            Glide.with(context)
                    .load(list.get(position).getThumbnail_pic_s03())
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .placeholder(R.mipmap.ic_launcher)
                    .error(R.mipmap.ic_launcher)
                    .into(holder.image03);

        }
        return convertView;
    }

    //新增3个内部类
    class Image01_ViewHolder {
        TextView title, author_name;
        ImageView image;
    }

    class Image02_ViewHolder {
        TextView title;
        ImageView image001, image002;
    }

    class Image03_ViewHolder {
        TextView title;
        ImageView image01, image02, image03;
    }
}