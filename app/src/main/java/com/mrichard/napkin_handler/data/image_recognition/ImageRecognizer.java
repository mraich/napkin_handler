package com.mrichard.napkin_handler.data.image_recognition;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;

import org.tensorflow.lite.DataType;
import org.tensorflow.lite.Interpreter;
import org.tensorflow.lite.support.common.TensorProcessor;
import org.tensorflow.lite.support.common.ops.NormalizeOp;
import org.tensorflow.lite.support.image.ImageProcessor;
import org.tensorflow.lite.support.image.TensorImage;
import org.tensorflow.lite.support.image.ops.ResizeOp;
import org.tensorflow.lite.support.label.TensorLabel;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ImageRecognizer
{

    private Context context;

    private Interpreter interpreter;
    private ImageProcessor imageProcessor;

    private List<String> labels;
    private TensorProcessor probabilityProcessor;
    private TensorBuffer probabilityBuffer;

    // The next attributes are protected as I can inherit this class
    // in the future with a different neural net too.

    // Information on neural net from assets.
    protected String NEURAL_NET_TFLITE = "image_recognizer/napkin.tflite";
    // Information on neural net output from assets.
    // It has to have the same line count as the NEURAL_NET_TFILE file output.
    protected String NEURAL_NET_TFLITE_LABELS = "image_recognizer/napkin_labels.txt";

    // Information on input determined by NEURAL_NET_TFLITE file.
    protected int INPUT_IMAGE_WIDTH = 224;
    protected int INPUT_IMAGE_HEIGHT = 224;

    // Information on output determined by NEURAL_NET_TFLITE file.
    protected int OUTPUT_COUNT = 6;
    protected DataType OUTPUT_DATA_TYPE = DataType.FLOAT32;

    public ImageRecognizer(Context context) {
        this.context = context;

        MappedByteBuffer modelFile = loadModelFile();
        if (modelFile != null) {
            interpreter = new Interpreter(modelFile, null);

            imageProcessor =
                new ImageProcessor.Builder()
                    .add(new ResizeOp(INPUT_IMAGE_HEIGHT, INPUT_IMAGE_WIDTH, ResizeOp.ResizeMethod.BILINEAR))
                    .build();
            }

        labels = readLabels();
        // Post-processor which dequantize the result
        probabilityProcessor =
                new TensorProcessor.Builder().add(new NormalizeOp(0, 255)).build();
        probabilityBuffer =
                TensorBuffer.createFixedSize(new int[] { 1, OUTPUT_COUNT }, OUTPUT_DATA_TYPE);

    }

    public String recognize(Bitmap image) {
        try {
            DataType imageDataType = interpreter.getInputTensor(0).dataType();
            TensorImage tensorImage = new TensorImage(imageDataType);
            tensorImage.load(image);
            tensorImage = imageProcessor.process(tensorImage);


            // Running inference.
            if (interpreter != null) {
                interpreter.run(tensorImage.getBuffer(), probabilityBuffer.getBuffer());

                if (labels != null) {
                    // Map of labels and their corresponding probability.
                    TensorLabel tensorLabels = new TensorLabel(readLabels(),
                            probabilityProcessor.process(probabilityBuffer));

                    // Create a map to access the result based on label.
                    Map<String, Float> floatMap = tensorLabels.getMapWithFloatValue();

                    Map.Entry<String, Float> maxEntry = null;
                    for (Map.Entry<String, Float> entry : floatMap.entrySet())
                    {
                        if (maxEntry == null || entry.getValue().compareTo(maxEntry.getValue()) > 0)
                        {
                            maxEntry = entry;
                        }
                    }
                    return maxEntry.getKey();
                }
            }
        } catch (Exception e) {
        }
        return "-";
    }

    private MappedByteBuffer loadModelFile()
    {
        try {
            AssetFileDescriptor assetFileDescriptor = context.getAssets().openFd(NEURAL_NET_TFLITE);
            FileInputStream fileInputStream = new FileInputStream(assetFileDescriptor.getFileDescriptor());
            FileChannel fileChannel = fileInputStream.getChannel();

            long startOffset = assetFileDescriptor.getStartOffset();
            long len = assetFileDescriptor.getLength();

            return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, len);
        } catch (IOException e) {
        }
        return null;
    }

    private ArrayList<String> readLabels() {
        ArrayList<String> result  = new ArrayList<>();
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(
                    new InputStreamReader(context.getAssets().open(NEURAL_NET_TFLITE_LABELS)));

            String line;
            while ((line = reader.readLine()) != null) {
                result.add(line);
            }
        } catch (IOException e) {
            //log the exception
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                }
            }
        }
        return result;
    }

}
