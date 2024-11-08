package com.rcm.eanimify.model;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import org.tensorflow.lite.Interpreter;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class TFLiteModelHelper {
    private Interpreter tflite;
    private final int featureVectorSize;

    public TFLiteModelHelper(Context context) {
        try {
            MappedByteBuffer model = loadModelFile(context);
            tflite = new Interpreter(model);

            // Check model output shape to define featureVectorSize
            int[] outputShape = tflite.getOutputTensor(0).shape();
            featureVectorSize = outputShape[1]; // Assuming output is of shape [1, featureVectorSize]
        } catch (Exception e) {
            throw new RuntimeException("Error initializing TensorFlow Lite model", e);
        }
    }

    private MappedByteBuffer loadModelFile(Context context) throws IOException {
        try (FileInputStream inputStream = new FileInputStream(context.getAssets().openFd("animal_model.tflite").getFileDescriptor())) {
            FileChannel fileChannel = inputStream.getChannel();
            long startOffset = context.getAssets().openFd("animal_model.tflite").getStartOffset();
            long declaredLength = context.getAssets().openFd("animal_model.tflite").getDeclaredLength();
            return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
        }
    }

    public float[] classifyImage(Bitmap bitmap) {
        // Resize to model input size
        Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, 224, 224, true);
        ByteBuffer inputBuffer = convertBitmapToByteBuffer(scaledBitmap);

        // Initialize output array for feature vector
        float[][] output = new float[1][featureVectorSize];

        // Run model inference
        tflite.run(inputBuffer, output);

        return output[0];
    }

    private ByteBuffer convertBitmapToByteBuffer(Bitmap bitmap) {
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(4 * 224 * 224 * 3);
        byteBuffer.order(ByteOrder.nativeOrder());
        int[] intValues = new int[224 * 224];

        bitmap.getPixels(intValues, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());

        int pixel = 0;
        for (int i = 0; i < 224; i++) {
            for (int j = 0; j < 224; j++) {
                int val = intValues[pixel++];
                byteBuffer.putFloat(((val >> 16) & 0xFF) / 255.0f);
                byteBuffer.putFloat(((val >> 8) & 0xFF) / 255.0f);
                byteBuffer.putFloat((val & 0xFF) / 255.0f);
            }
        }
        return byteBuffer;
    }
}
