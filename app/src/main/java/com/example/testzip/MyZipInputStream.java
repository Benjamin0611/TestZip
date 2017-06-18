package com.example.testzip;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipInputStream;

/**
 * Created by Administrator on 2017/6/16 0016.
 */

public class MyZipInputStream extends ZipInputStream {

    long lenRemaining = 0;
    int lenReaded = 0;


    public MyZipInputStream(InputStream in) {
        super(in);
    }

    @Override
    public ZipEntry getNextEntry() throws IOException {
        ZipEntry zipentry = null;
        try {
            zipentry = super.getNextEntry();
            System.out.println("00002");
            //lenRemaining = zipentry.getCompressedSize();
            if(zipentry != null) {
                lenRemaining = zipentry.getSize();
            }
            return zipentry;
        } catch (ZipException e) {
            // e.printStackTrace();
            System.out.println("00003");
        } catch (EOFException e) {
            // e.printStackTrace();
            System.out.println("00009");
            return null;
        }

        try {
            if (zipentry == null) {
                zipentry = super.getNextEntry();
            }
        } catch (ZipException e) {
            // e.printStackTrace();
            System.out.println("00007");
            return null;
        } catch (EOFException e) {
            // e.printStackTrace();
            System.out.println("00008");
            return null;
        }finally {
            return zipentry;
        }

    }


    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        try{
            lenReaded = super.read(b, off, len);
            lenRemaining -= lenReaded;
            return lenReaded;
        }catch(ZipException e){
            e.printStackTrace();
            System.out.println("lenRemaining = " + lenRemaining);
            return (int)lenRemaining;
        }
    }

    @Override
    public void close() throws IOException {
        try{
            super.close();
        }catch(ZipException e){
            System.out.println("00001");
            //e.printStackTrace();
        }
    }
}
