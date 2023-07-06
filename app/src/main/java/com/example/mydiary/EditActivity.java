package com.example.mydiary;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.text.SimpleDateFormat;
import java.util.Date;

public class EditActivity extends BaseActivity {
    private EditText editTitle;
    private EditText editAuthor;
    private EditText editContent;//编辑的笔记
    private String old_title = "";
    private String old_author = "";
    private String old_content = "";
    private String old_time = "";
    private int old_tag = 1;
    private long id = 0;
    private int openMode = 0;
    private int tag = 1;
    public Intent intent = new Intent();
    private Boolean tagChange = false;
    private Toolbar noteToolbar;

    /**
     * 引入menu菜单栏
     *
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_edit, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        editTitle = findViewById(R.id.editTitle);
        editAuthor = findViewById(R.id.editAuthor);
        editContent = findViewById(R.id.editText1);
        noteToolbar = findViewById(R.id.noteToolbar);

        //设置Action Bar，自定义Toolbar
        setSupportActionBar(noteToolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);//设置toolber取代action bar
        //设置Navigation返回键的事件监听，与系统的返回键功能相同
        noteToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                autoSetMessage(intent);//自动更新笔记
                setResult(RESULT_OK, intent);
                finish();
            }
        });

        Intent getIntent = getIntent();
        //定义意图
        openMode = getIntent.getIntExtra("mode", 0);
        if (openMode == 3) {
            //打开已存在的note，将内容写入到已编辑的笔记中，实现继续编辑
            id = getIntent.getLongExtra("id", 0);
            old_title = getIntent.getStringExtra("title");
            old_author = getIntent.getStringExtra("author");
            old_content = getIntent.getStringExtra("content");
            old_time = getIntent.getStringExtra("time");
            old_tag = getIntent.getIntExtra("tag", 1);
            editTitle.setText(old_title);
            editAuthor.setText(old_author);
            editContent.setText(old_content);//填充内容
            editContent.setSelection(old_content.length());//移动光标的位置（最后），方便再次书写
        }
    }

    /**
     * 设置菜单栏按钮监听
     *
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        final Intent intent = new Intent();
        if(item.getItemId()==R.id.delete){
            new AlertDialog.Builder(EditActivity.this)
                    .setMessage("您确定要删除吗？")
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (openMode == 4) {//如果是新增笔记，则不创建
                                intent.putExtra("mode", -1);
                                setResult(RESULT_OK, intent);
                            } else {//如果是修改笔记，则删除
                                intent.putExtra("mode", 2);
                                intent.putExtra("id", id);
                                setResult(RESULT_OK, intent);
                            }
                            finish();
                        }
                    }).setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();//关闭窗口
                        }
                    }).create().show();
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * 系统按钮监听
     *
     * @param keyCode
     * @param event
     * @return
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_HOME) {
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent intent = new Intent();
            autoSetMessage(intent);
            setResult(RESULT_OK, intent);
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 判断是新增笔记还是修改笔记,mode值是3为修改，为4是新增
     */
    public void autoSetMessage(Intent intent) {
        if (openMode == 4) {
            //判断笔记是否为空，若为空，则不新增笔记
            if (editTitle.getText().toString().length() == 0
                    && editContent.getText().toString().length() == 0) {
                intent.putExtra("mode", -1);
            }
            else {
                intent.putExtra("mode", 0);
                intent.putExtra("title", editTitle.getText().toString());
                intent.putExtra("author", editAuthor.getText().toString());
                intent.putExtra("content", editContent.getText().toString());
                intent.putExtra("time", dateToStr());
                intent.putExtra("tag", tag);
            }
        } else {
            //判断笔记是否被修改，或者标签是否更换，否则不更新笔记
            if (editTitle.getText().toString().equals(old_title)
                    && editAuthor.getText().toString().equals(old_author)
                    && editContent.getText().toString().equals(old_content)
                    && !tagChange) {
                intent.putExtra("mode", -1);
            } else {
                intent.putExtra("mode", 1);
                intent.putExtra("title", editTitle.getText().toString());
                intent.putExtra("author", editAuthor.getText().toString());
                intent.putExtra("content", editContent.getText().toString());
                intent.putExtra("time", dateToStr());
                intent.putExtra("id", id);
                intent.putExtra("tag", tag);
            }
        }
    }

    /**
     * 转换时间格式
     *
     * @return
     */
    public String dateToStr() {
        Date date = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return simpleDateFormat.format(date);
    }
}





















