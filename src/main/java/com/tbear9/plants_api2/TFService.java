package com.tbear9.plants_api2;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tensorflow.*;
import org.tensorflow.ndarray.FloatNdArray;
import org.tensorflow.ndarray.NdArraySequence;
import org.tensorflow.ndarray.Shape;
import org.tensorflow.types.TFloat32;

import java.nio.FloatBuffer;

public final class TFService {
    private static final Logger log = LoggerFactory.getLogger("TFService");
    private final static String[] soil = {"01-Aluvial", "02-Andosol", "03-Entisol", "04-Humus", "05-Inceptisol", "06-Laterit", "07-Kapur", "08-Pasir"};

    public static NdArraySequence<FloatNdArray> process(byte[] image){
        try(SavedModelBundle model = SavedModelBundle.load("model", "serve");
            TFloat32 input = TFloat32.tensorOf(Shape.of(1, 320, 320, 3));
            Session session = model.session();
            Result result = session.runner()
                        .feed("input_1", input)
                        .fetch("output_1")
                        .run();
            TFloat32 output = (TFloat32) result.get(0);
            ) {
            NdArraySequence<FloatNdArray> scalars = output.scalars();
            scalars.forEachIndexed((i, scalar) -> {
                log.info("{}: {}", soil[(int) i[1]], scalar.getFloat());
            });
            return scalars;
        }
    }
        public static FloatBuffer toFloatBuffer(float[][][][] input) {
        int batch = input.length;
        int h = input[0].length;
        int w = input[0][0].length;
        int c = input[0][0][0].length;

        FloatBuffer buffer = FloatBuffer.allocate(batch * h * w * c);

        for (int b = 0; b < batch; b++) {
            for (int i = 0; i < h; i++) {
                for (int j = 0; j < w; j++) {
                    for (int k = 0; k < c; k++) {
                        buffer.put(input[b][i][j][k]);
                    }
                }
            }
        }

        buffer.rewind(); // reset pointer
        return buffer;
    }
}
