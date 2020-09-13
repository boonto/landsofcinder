package de.loc.tools;

import com.badlogic.gdx.files.FileHandle;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * http://www.mkyong.com/java/how-to-compress-files-in-zip-format/
 */
@SuppressWarnings("Duplicates")
public class ZipHelper {

    private static final boolean LOG = false;

    private static List<String> generateFileList(File node, List<String> fileList, String sourceFolder) {
        if ( node.isFile() ) {
            fileList.add(generateZipEntry(node.getAbsolutePath(), sourceFolder));
        }

        if ( node.isDirectory() ) {
            String[] subNote = node.list();
            for ( String filename : subNote ) {
                generateFileList(new File(node, filename), fileList, sourceFolder);
            }
        }

        return fileList;
    }

    private static String generateZipEntry(String file, String sourceFolder) {
        return file.substring(sourceFolder.length() + 1);
    }

    public static void zip(FileHandle input, FileHandle output) {
        List<String> fileList = new ArrayList<>();

        byte[] buffer = new byte[1024];

        ZipOutputStream zos = null;
        FileInputStream fis = null;

        try {
            if ( !input.exists() ) {
                throw new IOException("Folder does not exist!");
            }

            generateFileList(input.file(), fileList, input.file().getAbsolutePath());

            zos = new ZipOutputStream(new FileOutputStream(output.path()));

            if ( LOG ) {
                System.out.println("Output to Zip: " + output.path());
            }

            for ( String file : fileList ) {
                if ( LOG ) {
                    System.out.println("File Added: " + file);
                }

                ZipEntry ze = new ZipEntry(file);
                zos.putNextEntry(ze);

                fis = new FileInputStream(input.path() + File.separator + file);

                int len;
                while ( (len = fis.read(buffer)) > 0 ) {
                    zos.write(buffer, 0, len);
                }

                fis.close();
            }

            if ( LOG ) {
                System.out.println("Done");
            }
        } catch ( IOException e ) {
            e.printStackTrace();
        } finally {
            try {
                if ( zos != null ) {
                    zos.closeEntry();
                    zos.close();
                }
                if ( fis != null ) {
                    fis.close();
                }
            } catch ( IOException e ) {
                e.printStackTrace();
            }
        }
    }

    public static void unzip(FileHandle input, FileHandle output) {

        byte[] buffer = new byte[1024];

        ZipInputStream zis = null;
        FileOutputStream fos = null;

        try {
            zis = new ZipInputStream(new FileInputStream(input.file()));
            ZipEntry ze = zis.getNextEntry();

            File folder = output.file();
            if ( !folder.exists() ) {
                folder.mkdir();
            }

            while ( ze != null ) {
                String fileName = ze.getName();

                File newFile = new File(output.file().getAbsolutePath() + File.separator + fileName);

                if ( LOG ) {
                    System.out.println("file unzip: " + newFile.getAbsoluteFile());
                }

                newFile.getParentFile().mkdirs();

                if ( !ze.isDirectory() ) {

                    fos = new FileOutputStream(newFile);

                    int len;
                    while ( (len = zis.read(buffer)) > 0 ) {
                        fos.write(buffer, 0, len);
                    }

                    fos.close();
                }
                ze = zis.getNextEntry();
            }

            if ( LOG ) {
                System.out.println("Done");
            }

        } catch ( IOException e ) {
            e.printStackTrace();
        } finally {
            try {
                if ( zis != null ) {
                    zis.closeEntry();
                    zis.close();
                }
                if ( fos != null ) {
                    fos.close();
                }
            } catch ( IOException e ) {
                e.printStackTrace();
            }
        }
    }
}
