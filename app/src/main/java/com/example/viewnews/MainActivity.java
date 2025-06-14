package com.example.viewnews;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.example.viewnews.tools.ActivityCollector;
import com.example.viewnews.tools.BaseActivity;
import com.example.viewnews.tools.DataCleanManager;
import com.example.viewnews.usermodel.ArticleActivity;
import com.example.viewnews.usermodel.LoginActivity;
import com.example.viewnews.usermodel.UserDetailActivity;
import com.example.viewnews.usermodel.UserFavoriteActivity;
import com.example.viewnews.usermodel.UserInfo;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;

import org.litepal.LitePal;
import org.litepal.tablemanager.Connector;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;
import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends BaseActivity {

    private static final String TAG = "MainActivity";

    private Toolbar toolbar;
    private DrawerLayout mDrawerLayout;
    private NavigationView navigationView;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private List<String> list;

    private TextView userNickName, userSignature;

    private ImageView userAvatar;

    // 采用静态变量来存储当前登录的账号
    public static String currentUserId;
    // 记录读者账号，相当于Session来使用
    private String currentUserNickName, currentSignature, currentImagePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = findViewById(R.id.toolbar);

        //注意：只需第一次创建或升级本地数据库，第二次运行就注释掉

        // 🌟初始化数据库（首次使用LitePal时运行，之后可注释）

        Connector.getDatabase();
        //Toast.makeText(MainActivity.this, "创建数据库成功", Toast.LENGTH_LONG).show();

        //获取抽屉布局实例
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        //获取菜单控件实例
        navigationView = (NavigationView) findViewById(R.id.nav_design);

        //无法直接通过findViewById方法获取header布局id

        // 🌟获取侧边栏头部视图
        View v = navigationView.getHeaderView(0);

        CircleImageView circleImageView = (CircleImageView) v.findViewById(R.id.icon_image);

        tabLayout = (TabLayout) findViewById(R.id.tabLayout);

        viewPager = (ViewPager) findViewById(R.id.viewPager);

        list = new ArrayList<>();
    }

    //在活动由不可见变为可见的时候调用
    @Override
    protected void onStart() {
        super.onStart();
        System.out.println("当前MainActivity活动又被加载onStart");
        //设置标题栏的logo
        //toolbar.setLogo(R.drawable.icon);
        //设置标题栏标题
        toolbar.setTitle("昨日头条");
        //设置自定义的标题栏实例
        setSupportActionBar(toolbar);
        //获取到ActionBar的实例
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            //通过HomeAsUp来让导航按钮显示出来🌟显示导航按钮
            actionBar.setDisplayHomeAsUpEnabled(true);
            //设置Indicator来添加一个点击图标（默认图标是一个返回的箭头）
            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);
        }
        //设置默认选中第一个
        navigationView.setCheckedItem(R.id.nav_edit);
        //🌟设置菜单项的监听事件
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                //逻辑页面处理
                mDrawerLayout.closeDrawers();//🌟关闭抽屉
                switch (menuItem.getItemId()) {
                    case R.id.nav_edit:
                        //🌟编辑用户信息
                        //每个菜单项的点击事件，通过Intent实现点击item简单实现活动页面的跳转。
                        if (!TextUtils.isEmpty(currentUserId)) {
                            Intent editIntent = new Intent(MainActivity.this, UserDetailActivity.class);
                            editIntent.putExtra("user_edit_id", currentUserId);
                            startActivityForResult(editIntent, 3);
                        } else {
                            Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
                            loginIntent.putExtra("loginStatus", "请先登录后才能操作！");
                            startActivityForResult(loginIntent, 1);
                        }
                        break;
                    case R.id.nav_articles:
                        // 🌟我发布的文章
                        if (!TextUtils.isEmpty(currentUserId)) {
                            Intent editIntent = new Intent(MainActivity.this, ArticleActivity.class);
                            editIntent.putExtra("user_article_id", currentUserId);
                            startActivityForResult(editIntent, 6);
                        } else {
                            Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
                            loginIntent.putExtra("loginStatus", "请先登录后才能操作！");
                            startActivityForResult(loginIntent, 1);
                        }
                        break;

                        //🌟查看收藏
                    case R.id.nav_favorite:
                        if (!TextUtils.isEmpty(currentUserId)) {
                            Intent loveIntent = new Intent(MainActivity.this, UserFavoriteActivity.class);
                            loveIntent.putExtra("user_love_id", currentUserId);
                            startActivity(loveIntent);
                        } else {
                            Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
                            loginIntent.putExtra("loginStatus", "请先登录后才能操作！");
                            startActivityForResult(loginIntent, 1);
                        }
                        break;
                    case R.id.nav_clear_cache:
                        // 清除缓存
                        // Toast.makeText(MainActivity.this,"你点击了清除缓存，下步实现把",Toast.LENGTH_SHORT).show();
                        clearCacheData();
                        break;
                    case R.id.nav_switch:
                        // 切换账号
                        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                        // 登录请求码是1
                        startActivityForResult(intent, 1);
                        break;
                    default:
                }
                return true;
            }
        });
        //🌟新闻标签类设置tab标题
        list.add("头条");
        list.add("社会");
        list.add("国内");
        list.add("国际");
        list.add("娱乐");
        list.add("体育");
        list.add("军事");
        list.add("科技");
        list.add("财经");

        //表示ViewPager（默认）预加载一页
        //viewPager.setOffscreenPageLimit(1);
        /*
            当fragment不可见时, 可能会将fragment的实例也销毁(执行 onDestory, 是否执行与setOffscreenPageLimit 方法设置的值有关).
            所以内存开销会小些, 适合多fragment的情形.
            具体讲解查看：https://blog.csdn.net/StrongerCoder/article/details/70158836
            https://blog.csdn.net/Mr_LiaBill/article/details/48749807
        */
        viewPager.setAdapter(new FragmentStatePagerAdapter(getSupportFragmentManager(), 1) {
            //以下方法的使用可以查看：https://blog.csdn.net/fyq520521/article/details/80595684
            //得到当前页的标题，也就是设置当前页面显示的标题是tabLayout对应标题
            @Nullable
            @Override
            public CharSequence getPageTitle(int position) {
                return list.get(position);
            }

            //返回position位置关联的Fragment。
            @Override
            public Fragment getItem(int position) {
                NewsFragment newsFragment = new NewsFragment();
                //判断所选的标题，进行传值显示
                //Bundle主要用于传递数据；它保存的数据，是以key-value(键值对)的形式存在的。
                //详细讲解：https://blog.csdn.net/yiranruyuan/article/details/78049219
                Bundle bundle = new Bundle();
                if (list.get(position).equals("头条")) {
                    bundle.putString("name", "top");
                } else if (list.get(position).equals("社会")) {
                    bundle.putString("name", "shehui");
                } else if (list.get(position).equals("国内")) {
                    bundle.putString("name", "guonei");
                } else if (list.get(position).equals("国际")) {
                    bundle.putString("name", "guoji");
                } else if (list.get(position).equals("娱乐")) {
                    bundle.putString("name", "yule");
                } else if (list.get(position).equals("体育")) {
                    bundle.putString("name", "tiyu");
                } else if (list.get(position).equals("军事")) {
                    bundle.putString("name", "junshi");
                } else if (list.get(position).equals("科技")) {
                    bundle.putString("name", "keji");
                } else if (list.get(position).equals("财经")) {
                    bundle.putString("name", "caijing");
                } else if (list.get(position).equals("时尚")) {
                    bundle.putString("name", "shishang");
                }
                //设置当前newsFragment的bundle
                //具体讲解：https://www.jb51.net/article/102383.htm
                newsFragment.setArguments(bundle);
                return newsFragment;
            }

            //创建指定位置的页面视图
            @NonNull
            @Override
            public Object instantiateItem(@NonNull ViewGroup container, int position) {
                NewsFragment newsFragment = (NewsFragment) super.instantiateItem(container, position);
                return newsFragment;
            }

            //具体讲解：https://www.cnblogs.com/cheneasternsun/p/6017012.html，但是这样用比较浪费资源
            @Override
            public int getItemPosition(@NonNull Object object) {
                return FragmentStatePagerAdapter.POSITION_NONE;
            }

            //返回当前有效视图的数量，这其实也就是将list和tab选项卡关联起来
            @Override
            public int getCount() {
                return list.size();
            }
        });
        //🌟将TabLayout与ViewPager关联显示
        tabLayout.setupWithViewPager(viewPager);
        //🌟 加载上次登录的账号，起到记住会话的功能
        String inputText = load();
        if (!TextUtils.isEmpty(inputText) && TextUtils.isEmpty(currentUserId)) {
            currentUserId = inputText;
        }
        //🌟初始化用户界面
        View v = navigationView.getHeaderView(0);
        userNickName = v.findViewById(R.id.text_nickname);
        userSignature = v.findViewById(R.id.text_signature);
        userAvatar = v.findViewById(R.id.icon_image);
        if (!TextUtils.isEmpty(currentUserId)) {
            List<UserInfo> userInfos = LitePal.where("userAccount = ?", currentUserId).find(UserInfo.class);
            userNickName.setText(userInfos.get(0).getNickName());
            userSignature.setText(userInfos.get(0).getUserSignature());
            currentImagePath = userInfos.get(0).getImagePath();
            System.out.println("主界面初始化数据：" + userInfos);
            diplayImage(currentImagePath);
        } else {
            userNickName.setText("请先登录");
            userSignature.setText("请先登录");
            userAvatar.setImageResource(R.drawable.no_login_avatar);
        }
    }

    // 解析、展示图片🌟将图片路径转为 Bitmap 展示头像
    private void diplayImage(String imagePath) {
        if (!TextUtils.isEmpty(imagePath)) {
            Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
            userAvatar.setImageBitmap(bitmap);
        } else {
            userAvatar.setImageResource(R.drawable.no_login_avatar);
        }
    }


    // 通过登录来接收值
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        View v = navigationView.getHeaderView(0);
        userNickName = v.findViewById(R.id.text_nickname);
        userSignature = v.findViewById(R.id.text_signature);

        switch (requestCode) {
            case 1: // 切换账号登录后来主界面
                if (resultCode == RESULT_OK) {
                    Toast.makeText(MainActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
                    currentUserId = data.getStringExtra("userID");
                    currentUserNickName = data.getStringExtra("userNick");
                    currentSignature = data.getStringExtra("userSign");
                    currentImagePath = data.getStringExtra("imagePath");
                    Log.d(TAG, "当前用户的账号为：" + currentUserId);
                    Log.d(TAG, "当前用户的昵称为：" + currentUserNickName);
                    Log.d(TAG, "当前用户的个性签名为： " + currentSignature);
                    Log.d(TAG, "当前用户的头像地址为: " + currentImagePath);
                    userNickName.setText(currentUserNickName);
                    userSignature.setText(currentSignature);
                    diplayImage(currentImagePath);
                }
                break;
            case 3: // 从个人信息返回来的数据，要更新导航栏中的数据，包括昵称，签名，图片路径
                if (resultCode == RESULT_OK) {
                    currentUserNickName = data.getStringExtra("nickName");
                    currentSignature = data.getStringExtra("signature");
                    currentImagePath = data.getStringExtra("imagePath");
                    Log.d(TAG, "当前用户的昵称为：" + currentUserNickName);
                    Log.d(TAG, "当前用户的个性签名为： " + currentSignature);
                    Log.d(TAG, "当前用户的图片路径为： " + currentImagePath);
                    System.out.println("当前用户的图片路径7777777为： " + currentImagePath);
                    userNickName.setText(currentUserNickName);
                    userSignature.setText(currentSignature);
                    diplayImage(currentImagePath);
                }
                break;
            default:
                break;
        }
    }



    //🌟清除缓存逻辑
    public void clearCacheData() {
        // 缓存目录为 /data/user/0/com.example.viewnews/cache
        File file = new File(MainActivity.this.getCacheDir().getPath());
        System.out.println("缓存目录为：" + MainActivity.this.getCacheDir().getPath());
        String cacheSize = null;
        try {
            cacheSize = DataCleanManager.getCacheSize(file);
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("缓存大小为：" + cacheSize);
        new MaterialDialog.Builder(MainActivity.this)
                .title("提示")
                .content("当前缓存大小一共为" + cacheSize + "。确定要删除所有缓存？离线内容及其图片均会被清除。")
                .positiveText("确认")
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(MaterialDialog dialog, DialogAction which) {
                        // dialog 此弹窗实例共享父实例
                        System.out.println("点击了啥内容：" + which);
                        // 没起作用
                        DataCleanManager.cleanInternalCache(MainActivity.this);
                        Toast.makeText(MainActivity.this, "成功清除缓存。", Toast.LENGTH_SHORT).show();
                    }
                })
                .negativeText("取消")
                .show();

    }

    //加载标题栏的菜单布局
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //获取toolbar菜单项
        getMenuInflater().inflate(R.menu.toolbar, menu);
        return true;
    }

    //监听标题栏的菜单item的选择事件
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            //R.id.home修改导航按钮的点击事件为打开侧滑
            case android.R.id.home:
                //打开侧滑栏，注意要与xml中保持一致START
                mDrawerLayout.openDrawer(GravityCompat.START);
                break;
            case R.id.userFeedback:
                //填写用户反馈
                new MaterialDialog.Builder(MainActivity.this)
                        .title("用户反馈")
                        .inputRangeRes(1, 50, R.color.colorBlack)
                        .inputType(InputType.TYPE_CLASS_TEXT)
                        .input(null, null, new MaterialDialog.InputCallback() {
                            @Override
                            public void onInput(MaterialDialog dialog, CharSequence input) {
                                System.out.println("反馈的内容为：" + input);
                                Toast.makeText(MainActivity.this, "反馈成功！反馈内容为：" + input, Toast.LENGTH_SHORT).show();
                            }
                        })
                        .positiveText("确定")
                        .negativeText("取消")
                        .show();
                break;
            case R.id.userExit:
                SweetAlertDialog mDialog = new SweetAlertDialog(MainActivity.this, SweetAlertDialog.NORMAL_TYPE)
                        .setTitleText("提示")
                        .setContentText("您是否要退出？")
                        .setCustomImage(null)
                        .setCancelText("取消")
                        .setConfirmText("确定")
                        .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                sweetAlertDialog.dismiss();
                            }
                        }).setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                sweetAlertDialog.dismiss();
                                ActivityCollector.finishAll();
                            }
                        });
                mDialog.show();
                break;
            default:
        }
        return true;
    }

    // 加载数据
    public String load() {
        //读取我们之前存储到data文件中的账号，方便下次启动app时直接使用
        FileInputStream in = null;
        BufferedReader reader = null;
        StringBuilder content = new StringBuilder();
        try {
            in = openFileInput("data");
            System.out.println("是否读到文件内容" + in);
            reader = new BufferedReader(new InputStreamReader(in));
            String line = "";
            while ((line = reader.readLine()) != null) {
                content.append(line);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return content.toString();
    }
}