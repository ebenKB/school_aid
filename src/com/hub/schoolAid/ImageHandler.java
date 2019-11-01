package com.hub.schoolAid;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.commons.io.FileUtils;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Blob;

import javax.imageio.IIOException;
import javax.imageio.ImageIO;
import javax.rmi.CORBA.Util;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

/**
 * This program demonstrates how to resize an image.
 *
 * @author www.codejava.net
 *
 */
public class ImageHandler {

    /**
     * Resizes an image to a absolute width and height (the image may not be
     * proportional)
     * @param inputImagePath Path of the original image
     * @param outputImagePath Path to save the resized image
     * @param scaledWidth absolute width in pixels
     * @param scaledHeight absolute height in pixels
     * @throws IOException
     */
    public static void resize(String inputImagePath,
                              String outputImagePath, int scaledWidth, int scaledHeight)
            throws IOException {
        // reads input image
        File inputFile = new File(inputImagePath);
        BufferedImage inputImage = ImageIO.read(inputFile);

        // creates output image
        BufferedImage outputImage = new BufferedImage(scaledWidth,
                scaledHeight, inputImage.getType());

        // scales the input image to the output image
        Graphics2D g2d = outputImage.createGraphics();
        g2d.drawImage(inputImage, 0, 0, scaledWidth, scaledHeight, null);
        g2d.dispose();

        // extracts extension of output file
        String formatName = outputImagePath.substring(outputImagePath
                .lastIndexOf(".") + 1);

        // writes to output file
        ImageIO.write(outputImage, formatName, new File(outputImagePath));
    }

    /**
     * Resizes an image by a percentage of original size (proportional).
     * @param inputImagePath Path of the original image
     * @param outputImagePath Path to save the resized image
     * @param percent a double number specifies percentage of the output image
     * over the input image.
     * @throws IOException
     */
    public static void resize(String inputImagePath,
                              String outputImagePath, double percent) throws IOException {
        File inputFile = new File(inputImagePath);
        BufferedImage inputImage = ImageIO.read(inputFile);
        int scaledWidth = (int) (inputImage.getWidth() * percent);
        int scaledHeight = (int) (inputImage.getHeight() * percent);
        resize(inputImagePath, outputImagePath, scaledWidth, scaledHeight);
    }

    public  static  void saveImage(Long imageSize, Path source, Path newdir) throws IOException {
        //resize bigger image
        if(imageSize <= 250000){
            Files.copy(source, newdir.resolve(source.getFileName()), REPLACE_EXISTING);
        }else if(imageSize <= 1000000){
            ImageHandler.resize(source.toString(),newdir.resolve(source.getFileName()).toString() ,0.4);
        }else if (imageSize <= 2000000){
            ImageHandler.resize(source.toString(),newdir.resolve(source.getFileName()).toString() ,0.25);
        }else if(imageSize <= 5000000){
            ImageHandler.resize(source.toString(),newdir.resolve(source.getFileName()).toString() ,0.15);
        }else{
            ImageHandler.resize(source.toString(),newdir.resolve(source.getFileName()).toString() ,150,200);
        }
    }

    public static URI openImageFile(ImageView image){
        URI path =null;
        FileChooser fileChooser=new FileChooser();
        javafx.stage.Stage stage = new javafx.stage.Stage();
        try {
            path=fileChooser.showOpenDialog(stage).toURI();

        }catch (NullPointerException e){
           Notification.getNotificationInstance().notifyError("You didn't select any image","Empty selection");
        }catch (Exception e) {
            System.out.println("an error has occurred..."+ e);
        }

        if(path==null){
            return null;
        }
        image.setImage(new Image(path.toString()));
        image.setVisible(true);
        System.out.println("this is the path"+path.toString());
       return path;
    }

    public static  URI getImagePath(){
        FileChooser fileChooser = new FileChooser();
        try {
            return fileChooser.showOpenDialog(new Stage()).toURI();
        }catch (Exception e) {
         return null;
        }
    }

    public static byte[] changeToBLOB(URI filePath) throws IOException {
        System.out.println("This is the file path"+ filePath);
        byte[] fileContent = null;
        try {
            fileContent = FileUtils.readFileToByteArray(new File(filePath.getPath()));
        } catch (IIOException e) {
            throw new IOException ("Unable to convert file to byte array. " +
                    e.getMessage());
        }
        return fileContent;
    }


    public static  void setImage(byte[] imageBytes, ImageView imageView) {
        ByteArrayInputStream inputStream = new ByteArrayInputStream(imageBytes);
        imageView.setImage(new Image(inputStream));
    }


//    @Inject public void setSelection(@Optional final Contact contact){
//        if (contact != null) {
//            writableValue.setValue(contact);
//            String jpegString=contact.getJpegString();
//            byte[] imageBytes=Base64.decode(jpegString.getBytes());
//            ByteArrayInputStream is=new ByteArrayInputStream(imageBytes);
//            imageView.setImage(new Image(is));
//        }
//    }

    public void writeBLOBToFile(Blob blob){
//        Path newdir = Paths.get(getClass().getResource(Utils.studentImgPath).toURI());
    }
//    public  void setStudentImage(Student std,URI path){
//        if(path != null){
//            try {
//                Path source = Paths.get(path);
//                Path newdir = Paths.get(getClass().getResource(Utils.studentImgPath).toURI());
//                FileChannel fileChannel = FileChannel.open(source);
//                Long imageSize = fileChannel.size();
//
//                saveImage(imageSize,source,newdir);
//
//                std.setImage(String.valueOf(source.getFileName()));
//
//
//            } catch (IOException e) {
//                e.printStackTrace();
//            } catch (URISyntaxException e) {
//                e.printStackTrace();
//            }
//        }
//    }


    public  void setStudentImage(StudentDetails details,URI path){
        if(path != null){
            try {
                Path source = Paths.get(path);
                Path newdir = Paths.get(getClass().getResource(Utils.studentImgPath).toURI());
                FileChannel fileChannel = FileChannel.open(source);
                Long imageSize = fileChannel.size();
                saveImage(imageSize,source,newdir);
                details.setImage(String.valueOf(source.getFileName()));
            } catch (IOException e) {
                e.printStackTrace();
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        }
    }

    public static Boolean deleteOldImage(String image){
        try {
            Files.delete(Paths.get(Utils.studentImgPath+image));
            return true;
        } catch (IOException e) {
//            e.printStackTrace();
            return false;
        }
    }
}

