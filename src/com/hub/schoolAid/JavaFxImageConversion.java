package com.hub.schoolAid;
import javax.imageio.ImageIO;
import java.awt.image.*;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import javafx.scene.image.Image;

public class JavaFxImageConversion {
    public Image getJavaFXImage(byte[] rawPixels, int width, int height) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            ImageIO.write((RenderedImage) createBufferedImage(rawPixels, width, height), "png", out);
            out.flush();
        } catch (Exception e) {
//            Logger.getLogger(ImageUtils.class.getName()).log(Level.SEVERE, null, ex);
            e.printStackTrace();

        }
        ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
        return new javafx.scene.image.Image(in);
    }

    private BufferedImage createBufferedImage(byte[] pixels, int width, int height) {
        SampleModel sm = getIndexSampleModel(width, height);
        DataBuffer db = new DataBufferByte(pixels, width*height, 0);
        WritableRaster raster = Raster.createWritableRaster(sm, db, null);
        IndexColorModel cm = getDefaultColorModel();
        BufferedImage image = new BufferedImage(cm, raster, false, null);
//        return java.awt.image;
        return  image;
    }

    private SampleModel getIndexSampleModel(int width, int height) {
        IndexColorModel icm = getDefaultColorModel();
        WritableRaster wr = icm.createCompatibleWritableRaster(1, 1);
        SampleModel sampleModel = wr.getSampleModel();
        sampleModel = sampleModel.createCompatibleSampleModel(width, height);
        return sampleModel;
    }

    private IndexColorModel getDefaultColorModel() {
        byte[] r = new byte[256];
        byte[] g = new byte[256];
        byte[] b = new byte[256];
        for(int i=0; i<256; i++) {
            r[i]=(byte)i;
            g[i]=(byte)i;
            b[i]=(byte)i;
        }
        IndexColorModel defaultColorModel = new IndexColorModel(8, 256, r, g, b);
        return defaultColorModel;
    }

    public void findMinAndMax(short[] pixels, int width, int height) {
        int size = width*height;
        int value, min = 65535, max = 0;
        for (int i=0; i<size; i++) {
            value = pixels[i]&0xffff;
            if (value<min)
                min = value;
            if (value>max)
                max = value;
        }
    }
}
