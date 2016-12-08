package com.cs.test_image3;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button mBtnLoading;
    private Button mBtnSaveimage;
    private Button mBtnDelete;
    private ImageView mImage;
    private String URLPATH = "http://ww4.sinaimg.cn/large/610dc034gw1fafmi73pomj20u00u0abr.jpg";
    //外部存储    /mnt/sdcard路径下
    private final static String FILEPATH = Environment.getExternalStorageDirectory() + "/BBB/";
 /*   private final static String FILEPATH1= Environment.getDataDirectory()+"/BBB/";
    private final static String FILEPATH2= Environment.getExternalStorageDirectory()+"/BBB/";*/

    private Bitmap mBitmap;
    private String mFileName = "test.jpg";
    private ProgressDialog mProgressDialog;
    private File file;// mnt/sd/BBB/
    private File dirfile;// mnt/sd/BBB/
    private Button mRxjava;
    private ImageView mImage2;
    private Button mBtnRead;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView() {
        mBtnLoading = (Button) findViewById(R.id.btn_loading);
        mBtnSaveimage = (Button) findViewById(R.id.btn_saveimage);
        mBtnDelete = (Button) findViewById(R.id.btn_delete);

        mBtnLoading.setOnClickListener(this);
        mBtnSaveimage.setOnClickListener(this);
        mBtnDelete.setOnClickListener(this);
        mImage = (ImageView) findViewById(R.id.image);

        mRxjava = (Button) findViewById(R.id.rxjava);
        mRxjava.setOnClickListener(this);
        mImage2 = (ImageView) findViewById(R.id.image2);

        mBtnRead = (Button) findViewById(R.id.btn_read);
        mBtnRead.setOnClickListener(this);
        mBtnRead.setClickable(false);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_loading:
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        // LoadImage1();
                        LoadImage2();

                    }
                }).start();
                mBtnRead.setClickable(true);

                break;
            case R.id.btn_saveimage:
              /*  mProgressDialog = ProgressDialog.show(MainActivity.this, "保存图片", "图片正在保存中，请稍等...", true);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        SaveImage();

                    }
                }).start();
               sdHandler.sendEmptyMessage(2);*/
                mProgressDialog = ProgressDialog.show(MainActivity.this, "保存图片", "图片正在保存中，请稍等...", true);
                SaveImage();
                connectHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Message message = new Message();
                        message.what = 2;
                        connectHandler.sendMessage(message);
                    }
                }, 1000);
                mBtnRead.setClickable(true);
                break;
            case R.id.btn_delete:
                DeleteFile(dirfile);
                mBitmap = null;
                Message message = new Message();
                message.what = 3;
                connectHandler.sendMessage(message);
                mBtnRead.setClickable(true);
                break;
            case R.id.rxjava:
                startActivity(new Intent(MainActivity.this, RxActivity.class));
                mBtnRead.setClickable(true);
                break;
            case R.id.btn_read:
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        if (file.exists()) {
                            ReadFromSDCard(file);
                            Message message = new Message();
                            message.what = 4;
                            connectHandler.sendMessage(message);
                        }else {
                            //Toast.makeText(MainActivity.this, "文件不存在", Toast.LENGTH_SHORT).show();
                            Message message = new Message();
                            message.what = 5;
                            connectHandler.sendMessage(message);
                        }

                    }
                }).start();


                break;
        }
    }



    /**
     * 主线程更新UI
     */
    private Handler connectHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    mImage.setImageBitmap(mBitmap);
                    break;
                case 2:
                    mProgressDialog.dismiss();
                    Toast.makeText(MainActivity.this, "图片保存完成", Toast.LENGTH_SHORT).show();
                    break;
                case 3:
                    mBitmap = null;
                    mImage.setImageBitmap(mBitmap);
                    mImage2.setImageBitmap(mBitmap);
                    break;
                case 4:
                    mImage2.setImageBitmap(mBitmap);
                    break;
                case 5:
                    Toast.makeText(MainActivity.this, "文件不存在", Toast.LENGTH_SHORT).show();
                    break;

            }

        }
    };

    /**
     * 下载图片从字节数组里面
     */
    private void LoadImage1() {
        //将图片从网络获取,并用hanler更行ui
        try {
            //传入需要的网址
            URL url = new URL(URLPATH);
            //打开网络连接
            final HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            //设置网络延时
            connection.setConnectTimeout(5 * 1000);
            //设置获取方式
            connection.setRequestMethod("GET");

            connection.connect();
            //转变为输入流
            InputStream inputStream = connection.getInputStream();
            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                //将输入流转变为字节流
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                //每次读取以一字节读取
                byte[] buffer = new byte[1024];
                //初始化读取字节的长度
                int len;
                //设置len
                while ((len = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, len);
                }
                outputStream.close();
                inputStream.close();
                //将字节流转变为字节数组由bitmapfacoty来写入
                byte[] byteArray = outputStream.toByteArray();
                mBitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
                //用hanler在主线程去更新UI,这里给个延时操作
                connectHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Message message = new Message();
                        message.what = 1;
                        connectHandler.sendMessage(message);
                    }
                }, 2000);
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 下载图片从输入里面
     */
    private void LoadImage2() {
        try {
            URL url = new URL(URLPATH);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(5 * 1000);
            conn.setRequestMethod("GET");
            InputStream inputStream = conn.getInputStream();
            mBitmap = BitmapFactory.decodeStream(inputStream);
            Message message = new Message();
            message.what = 1;
            connectHandler.sendMessage(message);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    /**
     * 保存图片到sd卡指定目录
     */
    private void SaveImage() {
        //创建文件 在是的sd卡外部存储
        dirfile = new File(FILEPATH);
        if (!dirfile.exists()) {
            dirfile.mkdir();
        }

        String fileName;
        //指定保存文件路径
        file = new File(FILEPATH + mFileName);
        //从系统保存需要用到输出流
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            BufferedOutputStream bos = new BufferedOutputStream(fileOutputStream);
            //bitmap进行解码
            if (mBitmap == null) {
                Toast.makeText(this, "Kong", Toast.LENGTH_SHORT).show();
                mProgressDialog.cancel();
            } else {
                mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);

                bos.flush();
                bos.hashCode();
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * 删除sd卡下的图片
     */
    private void DeleteFile(File file) {
        if (file.isFile()) {
            file.delete();
            return;
        }
        if (file.isDirectory()) {
            File[] childFile = file.listFiles();
            if (childFile == null || childFile.length == 0) {
                file.delete();
                return;
            }
           /* for (File f : childFile) {
                DeleteFile(f);
            }
            file.delete();*/
            for (int i = 0; i < childFile.length; i++) {
                //file下面的子文件删除
                DeleteFile(childFile[i]);
            }
            //删除父文件夹
            file.delete();
        }
    }

    /**
     * 从ｓｄ卡里面读取文件
     * @param file
     */
    private void ReadFromSDCard(File file) {
            try {
                FileInputStream fileInputStream = new FileInputStream(file);
               // mBitmap.recycle();
                mBitmap=BitmapFactory.decodeStream(fileInputStream);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
    }
}
