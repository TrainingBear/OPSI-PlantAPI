package com.tbear9.plants_api2;

import org.junit.jupiter.api.Test;
import org.tensorflow.ndarray.FloatNdArray;
import org.tensorflow.ndarray.NdArraySequence;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.ByteBuffer;

import static org.junit.jupiter.api.Assertions.*;

class TFServiceTest {

    @Test
    void process() {
        File file = new File("humus.jpg");
        try {
            BufferedImage image = ImageIO.read(file);
            Image temp = image.getScaledInstance(320, 320, BufferedImage.SCALE_SMOOTH);
            BufferedImage resized = new BufferedImage(320, 320, BufferedImage.TYPE_INT_RGB);
            Graphics2D grpic = resized.createGraphics();
            grpic.drawImage(temp, 0, 0, null);
            float[] result = TFService.process(bytes);
            for(int i = 0; i < result.length; i++){
                TFService.log.info("{}: {}", TFService.soil[i], result[i]);
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}