package com.pizza.tools.qrcode;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.Result;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.pizza.tools.ImageTool;

import java.util.Hashtable;
import java.util.Vector;

/**
 * 二维码工具类
 */
public class QrCodeTool {
    /**
     * 解析图片中的 二维码 或者 条形码
     *
     * @param photo 待解析的图片
     * @return Result 解析结果，解析识别时返回NULL
     */
    public static Result decodeFromPhoto(Bitmap photo) {
        Result rawResult = null;
        if (photo != null) {
            // 为防止原始图片过大导致内存溢出，这里先缩小原图显示，然后释放原始Bitmap占用的内存
            Bitmap smallBitmap = ImageTool.zoomBitmap(photo, photo.getWidth() / 2, photo.getHeight() / 2);
            // 释放原始图片占用的内存，防止out of memory异常发生
            photo.recycle();
            MultiFormatReader multiFormatReader = new MultiFormatReader();

            // 解码的参数
            Hashtable<DecodeHintType, Object> hints = new Hashtable<>(2);
            // 可以解析的编码类型
            Vector<BarcodeFormat> decodeFormats = new Vector<>();
            Vector<BarcodeFormat> productFormats = new Vector<>(5);
            productFormats.add(BarcodeFormat.UPC_A);
            productFormats.add(BarcodeFormat.UPC_E);
            productFormats.add(BarcodeFormat.EAN_13);
            productFormats.add(BarcodeFormat.EAN_8);
            // PRODUCT_FORMATS.add(BarcodeFormat.RSS14);
            Vector<BarcodeFormat> oneFormats = new Vector<>(productFormats.size() + 4);
            oneFormats.addAll(productFormats);
            oneFormats.add(BarcodeFormat.CODE_39);
            oneFormats.add(BarcodeFormat.CODE_93);
            oneFormats.add(BarcodeFormat.CODE_128);
            oneFormats.add(BarcodeFormat.ITF);
            Vector<BarcodeFormat> qrCodeFormats = new Vector<>(1);
            qrCodeFormats.add(BarcodeFormat.QR_CODE);
            Vector<BarcodeFormat> dataMatrixFormats = new Vector<>(1);
            dataMatrixFormats.add(BarcodeFormat.DATA_MATRIX);
            // 这里设置可扫描的类型，我这里选择了都支持
            decodeFormats.addAll(oneFormats);
            decodeFormats.addAll(qrCodeFormats);
            decodeFormats.addAll(dataMatrixFormats);
            hints.put(DecodeHintType.POSSIBLE_FORMATS, decodeFormats);
            // 设置继续的字符编码格式为UTF8
            // hints.put(DecodeHintType.CHARACTER_SET, "UTF8");
            // 设置解析配置参数
            multiFormatReader.setHints(hints);

            // 开始对图像资源解码
            try {
                rawResult = multiFormatReader.decodeWithState(
                        new BinaryBitmap(
                                new HybridBinarizer(
                                        new BitmapLuminanceSource(
                                                smallBitmap
                                        )
                                )
                        )
                );
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return rawResult;
    }

    /**
     * 生成二维码图片
     * @param text
     * @param w
     * @param h
     * @param logo
     * @return
     */
    public static Bitmap createQRImage(String text, int w, int h, Bitmap logo) {
        if (TextUtils.isEmpty(text)) {
            return null;
        }
        try {
            Bitmap scaleLogo = getScaleLogo(logo, w, h);
            int offsetX = w / 2;
            int offsetY = h / 2;
            int scaleWidth = 0;
            int scaleHeight = 0;
            scaleWidth = scaleLogo.getWidth();
            scaleHeight = scaleLogo.getHeight();
            offsetX = (w - scaleWidth) / 2;
            offsetY = (h - scaleHeight) / 2;
            Hashtable<EncodeHintType, Object> hints = new Hashtable<>();
            hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
            //容错级别
            hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
            //设置空白边距的宽度
            hints.put(EncodeHintType.MARGIN, 0);
            BitMatrix bitMatrix = new QRCodeWriter().encode(text, BarcodeFormat.QR_CODE, w, h, hints);
            int[] pixels = new int[w * h];
            for (int y = 0; y < h; y++) {
                for (int x = 0; x < w; x++) {
                    if (x >= offsetX && x < offsetX + scaleWidth && y >= offsetY && y < offsetY + scaleHeight) {
                        if (scaleLogo != null) {
                            int pixel = scaleLogo.getPixel(x - offsetX, y - offsetY);
                            if (pixel == 0) {
                                if (bitMatrix.get(x, y)) {
                                    pixel = -0x1000000;
                                } else {
                                    pixel = -0x1;
                                }
                            }
                            pixels[y * w + x] = pixel;
                        }
                    } else {
                        if (bitMatrix.get(x, y)) {
                            pixels[y * w + x] = -0x1000000;
                        } else {
                            pixels[y * w + x] = -0x1;
                        }
                    }
                }
            }
            Bitmap bitmap = Bitmap.createBitmap(
                    w, h,
                    Bitmap.Config.ARGB_8888
            );
            bitmap.setPixels(pixels, 0, w, 0, 0, w, h);
            return bitmap;
        } catch (WriterException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static Bitmap getScaleLogo(Bitmap logo, int w, int h) {
        if (logo == null) {
            return null;
        }
        Matrix matrix = new Matrix();
        float scaleFactor = Math.min(w * 1.0f / 5 / logo.getWidth(), h * 1.0f / 5 / logo.getHeight());
        matrix.postScale(scaleFactor, scaleFactor);
        return Bitmap.createBitmap(logo, 0, 0, logo.getWidth(), logo.getHeight(), matrix, true);
    }

    private static BitMatrix deleteWhite(BitMatrix matrix) {
        int[] rec = matrix.getEnclosingRectangle();
        int whiteSize = 3;
        int resWidth = rec[2] + whiteSize * 2;
        int resHeight = rec[3] + whiteSize * 2;
        BitMatrix resMatrix = new BitMatrix(resWidth, resHeight);
        resMatrix.clear();
        for (int i = 0; i < resWidth; i++) {
            for (int j = 0; i < resHeight; i++) {
                if (matrix.get(i + rec[0] - whiteSize, j + rec[1] - whiteSize)) {
                    resMatrix.set(i, j);
                }
            }
        }
        return resMatrix;
    }

    /**
     * 生成条形码
     *
     * @param context
     * @param contents      需要生成的内容
     * @param desiredWidth  生成条形码的宽带
     * @param desiredHeight 生成条形码的高度
     * @param displayCode   是否在条形码下方显示内容
     *
     * @return
     */
    public static Bitmap createBarcode(
            Context context,
            String contents,
            int desiredWidth,
            int desiredHeight,
            boolean displayCode
    ) {
        Bitmap resultBitmap = null;
        // 图片两端所保留的空白的宽度
        int marginW = 20;
        // 条形码的编码类型
        BarcodeFormat barcodeFormat = BarcodeFormat.CODE_128;
        if (displayCode) {
            Bitmap barcodeBitmap = encodeAsBitmap(
                    contents,
                    barcodeFormat,
                    desiredWidth,
                    desiredHeight
            );
            Bitmap codeBitmap = createCodeBitmap(
                    contents,
                    desiredWidth + 2 * marginW,
                    desiredHeight,
                    context
            );
            resultBitmap = mixtureBitmap(
                    barcodeBitmap, codeBitmap,
                    new PointF(0f, desiredHeight)
            );
        } else {
            resultBitmap = encodeAsBitmap(
                    contents,
                    barcodeFormat,
                    desiredWidth,
                    desiredHeight
            );
        }
        return resultBitmap;
    }

    /**
     * 生成条形码的Bitmap
     *
     * @param contents      需要生成的内容
     * @param format        编码格式
     * @param desiredWidth
     * @param desiredHeight
     *
     * @return
     *
     * @throws WriterException
     */
    private static Bitmap encodeAsBitmap(
            String contents,
            BarcodeFormat format,
            int desiredWidth,
            int desiredHeight
    ) {
        int WHITE = -0x1;
        int BLACK = -0x1000000;
        MultiFormatWriter writer = new MultiFormatWriter();
        BitMatrix result = null;
        try {
            result = writer.encode(
                    contents, format, desiredWidth,
                    desiredHeight, null
            );
            int width = result.getWidth();
            int height = result.getHeight();
            int[] pixels = new int[width * height];
            // All are 0, or black, by default
            for (int y = 0; y < height; y++) {
                int offset = y * width;
                for (int x = 0; x < width; x++) {
                    if (result.get(x, y)) {
                        pixels[offset + x] = BLACK;
                    } else {
                        pixels[offset + x] = WHITE;
                    }
                }
            }
            Bitmap bitmap = Bitmap.createBitmap(
                    width, height,
                    Bitmap.Config.ARGB_8888
            );
            bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
            return bitmap;
        } catch (WriterException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 生成显示编码的Bitmap
     *
     * @param contents
     * @param width
     * @param height
     * @param context
     *
     * @return
     */
    public static Bitmap createCodeBitmap(
            String contents,
            int width,
            int height,
            Context context
    ) {
        TextView tv = new TextView(context);
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT
        );
        tv.setLayoutParams(layoutParams);
        tv.setText(contents);
        tv.setHeight(height);
        tv.setGravity(Gravity.CENTER_HORIZONTAL);
        tv.setWidth(width);
        tv.setDrawingCacheEnabled(true);
        tv.setTextColor(Color.BLACK);
        tv.measure(
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        );
        tv.layout(0, 0, tv.getMeasuredWidth(), tv.getMeasuredHeight());
        tv.buildDrawingCache();
        return tv.getDrawingCache();
    }

    /**
     * 将两个Bitmap合并成一个
     *
     * @param first
     * @param second
     * @param fromPoint 第二个Bitmap开始绘制的起始位置（相对于第一个Bitmap）
     *
     * @return
     */
    private static Bitmap mixtureBitmap(
            Bitmap first,
            Bitmap second,
            PointF fromPoint
    ) {
        if (first == null || second == null || fromPoint == null) {
            return null;
        }
        int marginW = 20;
        Bitmap newBitmap = Bitmap.createBitmap(
                first.getWidth() + second.getWidth() + marginW,
                first.getHeight() + second.getHeight(), Bitmap.Config.ARGB_4444
        );
        Canvas cv = new Canvas(newBitmap);
        cv.drawBitmap(first, marginW, 0f, null);
        cv.drawBitmap(second, fromPoint.x, fromPoint.y, null);
        cv.save();
        cv.restore();
        return newBitmap;
    }
}
