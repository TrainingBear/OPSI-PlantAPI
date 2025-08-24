package com.tbear9.plants_api2;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tensorflow.*;
import org.tensorflow.ndarray.FloatNdArray;
import org.tensorflow.ndarray.NdArraySequence;
import org.tensorflow.ndarray.Shape;
import org.tensorflow.ndarray.buffer.FloatDataBuffer;
import org.tensorflow.types.TFloat32;

import java.nio.FloatBuffer;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicReference;

public final class TFService {
    public static final Logger log = LoggerFactory.getLogger("TFService");
    public final static String[] soil = {"01-Aluvial", "02-Andosol", "03-Entisol", "04-Humus", "05-Inceptisol", "06-Laterit", "07-Kapur", "08-Pasir"};

    /**
     * @param image gambar dalam bentuk byte buffer
     * @return SoftMax[i] where i = 1 to 8
     */
    public static float[] process(byte[] image){
        TensorI
        try(SavedModelBundle model = SavedModelBundle.load("model", "serve");
            FloatDataBuffer inputBuffer = FloatDataBuffers.of(image);
            Tensor<TFloat32> input = TFloat32.tensorOf(Shape.of(1, 224, 224, 3), inputBuffer)
            Session session = model.session();
            Result result = session.runner()
                        .feed("serve_input_layer:0", input)
                        .fetch("StatefulPartitionedCall:0")
                        .run();
            TFloat32 output = (TFloat32) result.get(0);
            ) {
            /* SOFT MAX
            * SoftMax(i) = (e[Z[i]]) / sum(exp(logits))) */
            double[] logits = new double[soil.length];
            NdArraySequence<FloatNdArray> scalars = output.scalars();
            final float[] max = {Float.MIN_VALUE};
            scalars.forEachIndexed((i, scalar) -> {
                max[0] = Math.max(scalar.getFloat(), max[0]);
            });
            AtomicReference<Double> sumExp = new AtomicReference<>(0d);
            scalars.forEachIndexed((i, scalar) -> {
                logits[(int) i[1]] = (float) Math.exp(scalar.getFloat() - max[0]);
                sumExp.updateAndGet((d) -> d + logits[(int) i[1]]);
            });

            float[] Z = new float[soil.length];
            for(int i = 0; i < logits.length; i++){
                Z[i] = (float) (logits[i] / sumExp.get());
            }

            return Z;
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
