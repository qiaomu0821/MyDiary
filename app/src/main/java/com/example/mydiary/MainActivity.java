package com.example.mydiary;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.appcompat.widget.SearchView;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MainActivity extends BaseActivity implements AdapterView.OnItemClickListener {
    private MyDatabaseHelper dbHelper;
    private Context context = this;
    private FloatingActionButton fa_btn;//悬浮按钮组件
    private ListView listView;
    private MyAdapter adapter;
    private ArrayAdapter<String> authorsAdapter;
    private List<Diary> noteList = new ArrayList<Diary>();
    private Toolbar myToolbar;
    int returnMode;//返回的mode值，-1表示无操作，0表示新增，1表示修改，2表示删除

    //弹出菜单
    private PopupWindow popupWindow;//弹出窗口
    private PopupWindow popupCover;//灰色蒙版
    private ViewGroup viewGroup;
    private ViewGroup coverView;
    private LayoutInflater layoutInflater;//渲染布局
    private RelativeLayout main;
    private WindowManager wm;//窗口管理器
    private DisplayMetrics metrics;//显示矩阵，显示手机屏幕的宽高

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //获取组件
        fa_btn = findViewById(R.id.fab);
        listView = findViewById(R.id.lv);
        adapter = new MyAdapter(getApplicationContext(), noteList);
        listView.setAdapter(adapter);
        myToolbar = findViewById(R.id.myToolbar);

        //设置Action Bar，自定义Toolbar
        setSupportActionBar(myToolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);//设置toolber取代action bar
        myToolbar.setNavigationIcon(R.drawable.baseline_dehaze_24);//设置菜单栏图标
        //刷新页面
        refreshListView();//访问数据库后就进行刷新
        //给lv设置点击事件
        listView.setOnItemClickListener(this);
        //点击悬浮按钮，设置点击事件
        fa_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //创建意图
                Intent intent = new Intent(MainActivity.this, EditActivity.class);
                //设置新建笔记的mode值，与修改笔记的mode值为3进行区分
                intent.putExtra("mode", 4);
                //启动活动，获取结果
                startActivityForResult(intent, 0);
            }
        });
        initPopUpView();//初始化弹出窗口


        //设置事件监听
        myToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopUpView();
            }
        });
    }


    //初始化一个弹出窗口
    public void initPopUpView(){
        //渲染布局
        layoutInflater = (LayoutInflater)MainActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        //要显示的内容
        viewGroup = (ViewGroup) layoutInflater.inflate(R.layout.setting_layout,null);
        coverView = (ViewGroup) layoutInflater.inflate(R.layout.setting_cover,null);
        main = findViewById(R.id.main_layout);
        wm = getWindowManager();
        metrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(metrics);//获取屏幕的宽高

    }

    //获取屏幕的宽高
    public void showPopUpView(){
        int width = metrics.widthPixels;
        int height = metrics.heightPixels;

        popupCover = new PopupWindow(coverView,width,height,false);//focusable表示无法获得焦点
        popupWindow = new PopupWindow(viewGroup,(int)(width*0.7),height,true);
        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        //在主界面加载成功之后，显示弹出
        findViewById(R.id.main_layout).post(new Runnable() {
            @Override
            public void run() {
                //显示位置,在左上角显示
                popupCover.showAtLocation(main, Gravity.NO_GRAVITY,0,0);
                popupWindow.showAtLocation(main, Gravity.NO_GRAVITY,0,0);
                coverView.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        popupWindow.dismiss();//弹出窗口消失
                        return true;
                    }
                });
                popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
                    @Override
                    public void onDismiss() {
                        popupCover.dismiss();//对弹出窗口进行监听，若消失，则灰色蒙版也消失
                    }
                });
            }
        });
    }

    /**
     * 引入menu菜单栏
     *
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        //显示搜索栏
        MenuItem mySearch = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) mySearch.getActionView();
        //设置搜索默认提示文字
        searchView.setQueryHint("搜索");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            //提交是进行搜索
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }
            //输入字符改变是进行搜索
            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                if(returnMode!=-1)refreshListView();
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    /**
     * 设置菜单栏按钮监听
     *
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()==R.id.delete_all){
            new AlertDialog.Builder(MainActivity.this)
                    .setMessage("确定要全部删除吗？")
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dbHelper = new MyDatabaseHelper(context);
                            SQLiteDatabase db = dbHelper.getWritableDatabase();
                            db.delete("diaries", null, null);//删除数据表的所有数据
                            db.execSQL("update sqlite_sequence set seq=0 where name='diaries'"); //将索引设置为1
                            verifyAuthorsToSharedPreferences("",3);
                            refreshListView();
                        }
                    }).setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).create().show();
        }
        return super.onOptionsItemSelected(item);
    }
    public List<String> getAuthorListFromSharedPreferences() {
        List<String> authorList = new ArrayList<>();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        Map<String, ?> allAuthors = sharedPreferences.getAll();
        for (Map.Entry<String, ?> entry : allAuthors.entrySet()) {
            String key = entry.getKey();
            if(!key.equals(""))
                authorList.add(key);
        }
        return authorList;
    }
    public void verifyAuthorsToSharedPreferences(String author,int mode){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editorSP = sharedPreferences.edit();
        if(sharedPreferences.getAll().isEmpty()){
            editorSP.putString("GKQ(Default)", "GKQ(Default)");
            editorSP.apply();
        }
        if((!sharedPreferences.contains(author) || author!="") && (mode==0 || mode==1)) {
            editorSP.putString(author, author);
            editorSP.apply();
        }
        else if(mode==2){
            editorSP.remove(author);
            editorSP.apply();
        }
        else if(mode==3){
            editorSP.clear();
            editorSP.putString("GKQ(Default)", "GKQ(Default)");
            editorSP.apply();
        }
        List<String> authorList = getAuthorListFromSharedPreferences();
        authorsAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, authorList);
        ListView listView = viewGroup.findViewById(R.id.lv_tag);
        listView.setAdapter(authorsAdapter);
        authorsAdapter.notifyDataSetChanged();
    }
    //接收startActivityForResult的结果
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        long diary_Id;//笔记的id，主键
        //接收结果
        returnMode = data.getExtras().getInt("mode", -1);
        String title = data.getExtras().getString("title");
        String author = data.getExtras().getString("author");
        String content = data.getExtras().getString("content");
        String time = data.getExtras().getString("time");
        int tag = data.getExtras().getInt("tag", 1);
        diary_Id = data.getExtras().getLong("id", 0);
        if (returnMode == 1) {//修改笔记
            //将结果写入Note实体类
            verifyAuthorsToSharedPreferences(author, 1);
            Diary newDiary = new Diary(title, author, content, time, tag);
            newDiary.setId(diary_Id);//需要通过id进行修改
            MyDatabaseCrud op = new MyDatabaseCrud(context);
            op.open();
            op.updateDiary(newDiary);
            op.close();
        } else if (returnMode == 0) {//新增笔记
            //将结果写入Note实体类
            verifyAuthorsToSharedPreferences(author, 0);
            Diary newDiary = new Diary(title, author, content, time, tag);
            MyDatabaseCrud op = new MyDatabaseCrud(context);
            op.open();
            op.addNote(newDiary);
            op.close();
        }
        else if (returnMode == 2) {//删除笔记
            verifyAuthorsToSharedPreferences(author, 2);
            Diary curDiary = new Diary();
            curDiary.setId(diary_Id);
            MyDatabaseCrud op = new MyDatabaseCrud(context);
            op.open();
            op.removeNote(curDiary);
            op.close();
        }
        refreshListView();//访问数据库后就进行刷新
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 更新内容
     */
    public void refreshListView() {
        MyDatabaseCrud op = new MyDatabaseCrud(context);
        op.open();
        //设置adapter
        if (noteList.size() > 0) {
            noteList.clear();
        }
        noteList.addAll(op.getAllNotes());
        op.close();
        adapter.notifyDataSetChanged();
    }

    /**
     * 重写点击事件
     *
     * @param parent
     * @param view
     * @param position
     * @param id
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if(parent.getId()==R.id.lv){
            Diary curNote = (Diary) parent.getItemAtPosition(position);
            Intent intent = new Intent(MainActivity.this, EditActivity.class);
            intent.putExtra("title", curNote.getTitle());
            intent.putExtra("author", curNote.getAuthor());
            intent.putExtra("content", curNote.getContent());
            intent.putExtra("id", curNote.getId());
            intent.putExtra("time", curNote.getTime());
            //修改笔记的mode设置为3，与新建笔记的mode值为4进行区分
            intent.putExtra("mode", 3);
            intent.putExtra("tag", curNote.getTog());
            startActivityForResult(intent, 1);
        }
    }
}































