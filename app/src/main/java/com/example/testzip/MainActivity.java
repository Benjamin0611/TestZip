package com.example.testzip;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.ParcelFileDescriptor;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    static byte[] buffer = new byte[5];
    static int bytesRead = 0;


        ParcelFileDescriptor[] pipe = null;
        ParcelFileDescriptor readSide = null;
        ParcelFileDescriptor writeSide = null;
        InputStream inputStream;
        OutputStream outputStream;


    static byte[] buffer02 = new byte[2048];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button button1 = (Button)findViewById(R.id.Button1);
        Button button2 = (Button)findViewById(R.id.Button2);
        Button button3 = (Button)findViewById(R.id.Button3);
        Button button4 = (Button)findViewById(R.id.Button4);
        button1.setOnClickListener(this);
        button2.setOnClickListener(this);
        button3.setOnClickListener(this);
        button4.setOnClickListener(this);
        verifyStoragePermissions(this);

        try{
            ParcelFileDescriptor[] pipe = ParcelFileDescriptor.createPipe();
            ParcelFileDescriptor readSide = pipe[0];
            ParcelFileDescriptor writeSide = pipe[1];
            outputStream = new ParcelFileDescriptor.AutoCloseOutputStream(writeSide);
            inputStream = new ParcelFileDescriptor.AutoCloseInputStream(readSide);

        }catch(Exception e){

        }

        new Thread(){
            @Override
            public void run() {
                try {
                    MyZipInputStream zipInputStream = new MyZipInputStream(inputStream);
                   // ZipInputStream zipInputStream = new ZipInputStream(inputStream);
                    ZipUtils.upZipStreamToFile(zipInputStream, "/sdcard/test/008");

                }catch (Exception e){
                    e.printStackTrace();

                }

            }
        }.start();

        int i= 0;
        if(i == 0)
            return;
        new Thread(){
            @Override
            public void run() {
                try {
                    File file = new File("/sdcard/test/005.zip");
                    FileOutputStream fileOutputStream = new FileOutputStream(file) ;

                    System.out.println("read stream begin ");
                    int len = 0;
                    int lenTotal = 0;
                    int count = 0;
                    while ((len = inputStream.read(buffer02)) >= 0) {
                        fileOutputStream.write(buffer02, 0, len);
                        count++;
                        lenTotal += len;
                        if(count % 100 == 0){
                            System.out.println("read lenTotal = " + (lenTotal/100));
                        }
                    }
                    fileOutputStream.close();

                }catch (Exception e){

                }

            }
        }.start();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.Button1:
                File file = new File("/sdcard/test/1.zip");

                try {
                    ZipOutputStream zipOutputStream = new ZipOutputStream(new FileOutputStream(file));

                    zipOutputStream.setLevel(0);
                    ZipEntry zipEntry = new ZipEntry("1.txt");
                    zipOutputStream.setMethod(ZipOutputStream.STORED);
                    zipEntry.setSize(13);
                    zipEntry.setCompressedSize(13);
                    zipEntry.setCrc(0);
                    zipOutputStream.putNextEntry(zipEntry);
                    zipEntry.setCrc(0x3e9780a5L);
                    zipOutputStream.write("helloworld111".getBytes());
                    zipOutputStream.closeEntry();


                    zipEntry = new ZipEntry("2.txt");
                    zipOutputStream.setMethod(ZipOutputStream.STORED);
                    zipEntry.setSize(13);
                    zipEntry.setCompressedSize(13);
                    zipEntry.setCrc(0);
                    zipOutputStream.putNextEntry(zipEntry);
                    zipEntry.setCrc(0x8ef53c85L);
                    zipOutputStream.write("helloworld222".getBytes());
                    zipOutputStream.flush();
                    zipOutputStream.closeEntry();

                    zipEntry = new ZipEntry("3.txt");
                    zipOutputStream.setMethod(ZipOutputStream.STORED);
                    zipEntry.setSize(13);
                    zipEntry.setCompressedSize(13);
                    zipEntry.setCrc(0);
                    zipOutputStream.putNextEntry(zipEntry);
                    zipEntry.setCrc(0x8ef53c85L);
                    zipOutputStream.write("helloworld333".getBytes());
                    zipOutputStream.flush();
                    zipOutputStream.closeEntry();


                    zipOutputStream.close();


                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                break;
            case R.id.Button2:

                File file2 = new File("/sdcard/test/1.zip");
                File fileOutput = new File("/sdcard/test/101.txt");


                try {
                    FileOutputStream fileOutputStream = new FileOutputStream(fileOutput);


                    MyZipInputStream zipInputStream = new MyZipInputStream(new FileInputStream(file2));
                    //ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream(file2));

                    ZipEntry zipEntry = zipInputStream.getNextEntry();

                    while(zipEntry != null) {
                        long len = zipEntry.getSize();
                        System.out.println("total is " + len);

                        while ((bytesRead = zipInputStream.read(buffer)) > 0) {
                            fileOutputStream.write(buffer, 0, bytesRead);
                        }


                        zipEntry = zipInputStream.getNextEntry();
                /*

                        while ((bytesRead = zipInputStream.read(buffer)) > 0) {
                            fileOutputStream.write(buffer, 0, bytesRead);
                        }

                        zipEntry = zipInputStream.getNextEntry();*/
                    }
                    zipInputStream.close();
                    fileOutputStream.close();



                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    System.out.println("0004");
                    e.printStackTrace();
                }


                break;


            case R.id.Button3:

                try {
                   // ZipUtils.zipFiles("/sdcard/test/002", "/sdcard/test/002.zip");
                    ZipUtils.zipToStream("/sdcard/test/002", outputStream);
                   //ZipUtils.zipToStream("/sdcard/test/001", outputStream);
                }catch (Exception e){

                }
                break;

            case R.id.Button4:

                try {
                    ZipUtils.upZipFile(new File("/sdcard/test/002.zip"),"/sdcard/test/002Unzip");
                }catch (Exception e){
                    e.printStackTrace();
                }
                break;











        }

    }



    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            "android.permission.READ_EXTERNAL_STORAGE",
            "android.permission.WRITE_EXTERNAL_STORAGE" };


    public static void verifyStoragePermissions(Activity activity) {

        try {
            //检测是否有写的权限
            int permission = ActivityCompat.checkSelfPermission(activity,
                    "android.permission.WRITE_EXTERNAL_STORAGE");
            if (permission != PackageManager.PERMISSION_GRANTED) {
                // 没有写的权限，去申请写的权限，会弹出对话框
                ActivityCompat.requestPermissions(activity, PERMISSIONS_STORAGE,REQUEST_EXTERNAL_STORAGE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
