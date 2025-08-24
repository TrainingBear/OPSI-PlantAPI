package com.tbear9.plants_api2;

import org.junit.jupiter.api.Test;
import org.tensorflow.ndarray.FloatNdArray;
import org.tensorflow.ndarray.NdArraySequence;

import java.io.*;
import java.nio.ByteBuffer;

import static org.junit.jupiter.api.Assertions.*;

class TFServiceTest {

    @Test
    void process() {
        File file = new File("humus.jpg");
        try {
            BufferedInputStream inputStream = new BufferedInputStream(new FileInputStream(file));
            byte[] bytes = inputStream.readAllBytes();
            NdArraySequence<FloatNdArray> result = TFService.process(bytes);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}