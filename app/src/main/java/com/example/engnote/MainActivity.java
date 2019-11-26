package com.example.engnote;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SearchEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TableLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private ArrayAdapter<String> adapter;
    WordsDBHelper mDbHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ListView list = (ListView) findViewById(R.id.lstWords);
        registerForContextMenu(list);
        mDbHelper = new WordsDBHelper(this);
        ArrayList<Map<String, String>> items=getAll();
        setWordsListView(items);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR);//横竖屏切换
        }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mDbHelper.close();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.menu_search:
                SearchDialog();
                return true;
            case R.id.menu_add:
                InsertDialog();
                return true;
            case R.id.menu_all:
                ArrayList<Map<String,String>> itemall=getAll();
                setWordsListView(itemall);
                return true;
            case R.id.menu_help:
                HelpDialog();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }                                        //****************************************左上角菜单
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        getMenuInflater().inflate(R.menu.contextmenu_wordslistview, menu);
    }
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        TextView textId=null;
        TextView textWord=null;
        TextView textMeaning=null;
        TextView textSample=null;
        AdapterView.AdapterContextMenuInfo info=null;
        View itemView=null;
        switch (item.getItemId()){
            case R.id.action_delete:
                info=(AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
                itemView=info.targetView;
                textId =(TextView)itemView.findViewById(R.id.textId);
                if(textId!=null){
                    String strId=textId.getText().toString();
                    DeleteUseSql(strId);

                }
                ArrayList<Map<String, String>> items=getAll();
                setWordsListView(items);
                break;
            case R.id.action_update:
                info=(AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
                itemView=info.targetView;
                textId =(TextView)itemView.findViewById(R.id.textId);
                String strId=textId.getText().toString();
                UpdateDialog(strId);
                break;
        }
        return true;
    }                                        //****************************************长按菜单
    private void UpdateDialog(final String ID) {
        final TableLayout tableLayout = (TableLayout) getLayoutInflater().inflate(R.layout.insert, null);
        new AlertDialog.Builder(this)
                .setTitle("修改单词")//标题
                .setView(tableLayout)//设置视图
                //确定按钮及其动作
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String strWord=((EditText)tableLayout.findViewById(R.id.txtWord)).getText().toString();
                        String strMeaning=((EditText)tableLayout.findViewById(R.id.txtMeaning)).getText().toString();
                        String strSample=((EditText)tableLayout.findViewById(R.id.txtSample)).getText().toString();
                        UpdateUseSql(ID,strWord, strMeaning, strSample);
                        ArrayList<Map<String, String>> items=getAll();
                        setWordsListView(items);
                    }
                })
                //取消按钮及其动作
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .create()//创建对话框
                .show();//显示对话框
    }                                                 //****************************************更新单词对话框
    private void setWordsListView(ArrayList<Map<String, String>> items){
        SimpleAdapter adapter = new SimpleAdapter(this, items, R.layout.item,
                new String[]{Words.Word._ID,Words.Word.COLUMN_NAME_WORD, Words.Word.COLUMN_NAME_MEANING, Words.Word.COLUMN_NAME_SAMPLE},
                new int[]{R.id.textId,R.id.textViewWord, R.id.textViewMeaning, R.id.textViewSample});
        ListView list = (ListView) findViewById(R.id.lstWords);
        list.setAdapter(adapter);
    }                         //****************************************设置list函数
    private void InsertUserSql(String strWord, String strMeaning, String strSample){
        String sql="insert into  words(word,meaning,sample) values(?,?,?)";
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        db.execSQL(sql,new String[]{strWord,strMeaning,strSample});
    }             //****************************************添加单词函数
    private void InsertDialog() {
        final TableLayout tableLayout = (TableLayout) getLayoutInflater().inflate(R.layout.insert, null);
        new AlertDialog.Builder(this)
                .setTitle("新增单词")//标题
                .setView(tableLayout)//设置视图
                //确定按钮及其动作
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String strWord=((EditText)tableLayout.findViewById(R.id.txtWord)).getText().toString();
                        String strMeaning=((EditText)tableLayout.findViewById(R.id.txtMeaning)).getText().toString();
                        String strSample=((EditText)tableLayout.findViewById(R.id.txtSample)).getText().toString();
                        InsertUserSql(strWord, strMeaning, strSample);

                        ArrayList<Map<String, String>> items=getAll();
                        setWordsListView(items);
                    }
                })
                //取消按钮及其动作
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .create()//创建对话框
                .show();//显示对话框
    }                                                                //****************************************添加单词对话框
    private void DeleteUseSql(String strId) {
        String sql="delete from words where _id='"+strId+"'";
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        db.execSQL(sql);
    }                                                    //****************************************删除单词函数
    private void UpdateUseSql(String strId,String strWord, String strMeaning, String strSample) {
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        String sql="update words set word=?,meaning=?,sample=? where _id=?";
        db.execSQL(sql, new String[]{strWord, strMeaning, strSample,strId});
    }//****************************************更新单词函数
    private ArrayList<Map<String, String>> SearchUseSql(String strWordSearch) {
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        String sql="select * from words where word like ? order by word desc";
        Cursor c=db.rawQuery(sql,new String[]{"%"+strWordSearch+"%"});
        return ConvertCursor2List(c);
    }                  //****************************************查找单词函数
    private ArrayList<Map<String,String>> ConvertCursor2List(Cursor cursor) {
        ArrayList<Map<String,String>>result = new ArrayList<Map<String,String>>();
        while(cursor.moveToNext()) {
            Map<String,String> item;
            item = new HashMap<String,String>();
            item.put(Words.Word._ID,cursor.getString(0));
            item.put(Words.Word.COLUMN_NAME_WORD, cursor.getString(1));
            item.put(Words.Word.COLUMN_NAME_MEANING, cursor.getString(2));
            item.put(Words.Word.COLUMN_NAME_SAMPLE, cursor.getString(3));
            result.add(item);
        }
        return result;
    }                    //****************************************取数据函数
    private ArrayList<Map<String, String>> getAll() {
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        String sql="select * from words order by word desc";
        Cursor cursor=db.rawQuery(sql,null);
        ArrayList<Map<String,String>>result = new ArrayList<Map<String,String>>();
        int ii=1;
        while(cursor.moveToNext()) {
            Map<String,String> item;
            item = new HashMap<String,String>();
            item.put(Words.Word._ID,cursor.getString(0));
            item.put(Words.Word.COLUMN_NAME_WORD, cursor.getString(1));
            item.put(Words.Word.COLUMN_NAME_MEANING, cursor.getString(2));
            item.put(Words.Word.COLUMN_NAME_SAMPLE, cursor.getString(3));
            result.add(item);
        }
        return result;
    }                                            //****************************************getAll函数
    private void SearchDialog() {
        final TableLayout tableLayout = (TableLayout) getLayoutInflater().inflate(R.layout.search, null);
        new AlertDialog.Builder(this)
                .setTitle("查找单词")//标题
                .setView(tableLayout)//设置视图
                //确定按钮及其动作
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String strWord=((EditText)tableLayout.findViewById(R.id.txtWord)).getText().toString();
                        ArrayList<Map<String,String>> sch=SearchUseSql(strWord);
                        setWordsListView(sch);
                    }
                })
                //取消按钮及其动作
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .create()//创建对话框
                .show();//显示对话框
    }                                                                //****************************************查询单词对话框
    private void HelpDialog() {
        final TableLayout tableLayout = (TableLayout) getLayoutInflater().inflate(R.layout.help, null);
        new AlertDialog.Builder(this)
                .setTitle("帮助")//标题
                .setView(tableLayout)//设置视图
                //确定按钮及其动作
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                //取消按钮及其动作
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .create()//创建对话框
                .show();//显示对话框
    }                                                                  //****************************************帮助对话框
}
