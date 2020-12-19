/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.elcom.usersystem.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Admin
 */
public class FileUtil {

    private static List<File> splitFile(File f) throws IOException {
        int partCounter = 1;
        List<File> result = new ArrayList<>();
        int sizeOfFiles = 1024 * 1024 / 10;// 1MB
        byte[] buffer = new byte[sizeOfFiles]; // create a buffer of bytes sized as the one chunk size

        BufferedInputStream bis = new BufferedInputStream(new FileInputStream(f));
        String name = f.getName();

        int tmp;
        while ((tmp = bis.read(buffer)) > 0) {
            File newFile = new File(f.getParent(), name + "." + String.format("%03d", partCounter++)); // naming files as <inputFileName>.001, <inputFileName>.002, ...
            FileOutputStream out = new FileOutputStream(newFile);
            out.write(buffer, 0, tmp);//tmp is chunk size. Need it for the last chunk, which could be less then 1 mb.
            result.add(newFile);
        }
        return result;
    }

    private static void mergeFiles(List<File> files, File into)
            throws IOException {
        try (BufferedOutputStream mergingStream = new BufferedOutputStream(new FileOutputStream(into))) {
            for (File f : files) {
                try (InputStream is = new FileInputStream(f)) {
                    Files.copy(f.toPath(), mergingStream);
                    try {
                        Files.delete(f.toPath());
                    } catch (Exception e) {
                    }
                }
            }
        }
    }
    
    private static void mergeFiles02(List<File> files, File into) {
        try (FileOutputStream dest = new FileOutputStream(into, true)) {

            FileChannel dc = dest.getChannel();// the final big file.
            for (int i = 0; i < files.size(); i++) {
                File partFile = files.get(i);
                if (!partFile.exists()) {
                    break;
                }
                try (FileInputStream part = new FileInputStream(partFile)) {
                    FileChannel pc = part.getChannel();
                    pc.transferTo(0, pc.size(), dc);// combine.
                }
                partFile.delete();
            }
            System.out.println("Merge OK!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] a) throws IOException {
        List<File> files = splitFile(new File("F:/app/aaa.jpg"));
        mergeFiles(files, new File("F:/app/aaa-merged.jpg"));
    }
}
