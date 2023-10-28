package com.pizza.tools.view;

import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;

import androidx.annotation.ArrayRes;
import androidx.annotation.ColorRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.StringRes;
import androidx.core.graphics.drawable.DrawableCompat;

import com.pizza.tools.ToolInit;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

/**
 * @author Kyle
 * 资源文件工具类
 */
public class ResTool {
    // ------------------------------String------------------------------------------
    public static String getString(@StringRes int resId) {
        return ToolInit.getApplicationContext().getResources().getString(resId);
    }

    public static String getString(@StringRes int resId, Object... os) {
        return ToolInit.getApplicationContext().getResources().getString(resId, os);
    }

    public static String[] getStringArray(@ArrayRes int resId) {
        return ToolInit.getApplicationContext().getResources().getStringArray(resId);
    }

    // -----------------------------Color--------------------------------------------
    public static int getColor(@ColorRes int resId) {
        return ToolInit.getApplicationContext().getResources().getColor(resId);
    }

    public static ColorStateList getColorStateList(int id) {
        Context context = ToolInit.getApplicationContext();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return context.getResources().getColorStateList(id, context.getTheme());
        } else {
            try {
                XmlResourceParser xpp = Resources.getSystem().getXml(id);
                return ColorStateList.createFromXml(context.getResources(), xpp);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    // -----------------------------Drawable-----------------------------------------

    public static Drawable getDrawable(@DrawableRes int resId) {
        Context context = ToolInit.getApplicationContext();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            return context.getResources().getDrawable(resId, context.getTheme());
        } else {
            return context.getResources().getDrawable(resId);
        }
    }

    public static Drawable getDrawable(@DrawableRes int resId, float size) {
        return getDrawable(
                resId,
                size,
                size
        );
    }

    public static Drawable getDrawable(
            @DrawableRes int resId,
            float size,
            ColorStateList colors
    ) {
        return getDrawable(
                resId, size, size, colors
        );
    }

    public static Drawable getDrawable(
            @DrawableRes int resId,
            float width,
            float height
    ) {
        return getDrawable(
                resId,
                new Rect(
                        0,
                        0,
                        DensityTool.dp2px(width),
                        DensityTool.dp2px(height)
                )
        );
    }

    public static Drawable getDrawable(
            @DrawableRes int resId,
            float width,
            float height,
            ColorStateList colors
    ) {
        return getDrawable(
                resId,
                colors,
                new Rect(
                        0,
                        0,
                        DensityTool.dp2px(width),
                        DensityTool.dp2px(height)
                )
        );
    }

    public static Drawable getDrawable(@DrawableRes int resId, Rect rect) {
        return getDrawable(resId, null, rect);
    }

    public static Drawable getDrawable(@DrawableRes int resId, ColorStateList colors, Rect rect) {
        Drawable drawable = ToolInit.getApplicationContext().getResources().getDrawable(resId);
        drawable.setBounds(rect);
        if (colors != null) {
            Drawable wrapDrawable = DrawableCompat.wrap(drawable).mutate();
            DrawableCompat.setTintList(wrapDrawable, colors);
            return wrapDrawable;
        }
        return drawable;
    }

    public static Drawable getDrawable(@DrawableRes int resId, ColorStateList colors) {
        Drawable drawable = ToolInit.getApplicationContext().getResources().getDrawable(resId);
        if (colors != null) {
            Drawable wrapDrawable = DrawableCompat.wrap(drawable).mutate();
            DrawableCompat.setTintList(wrapDrawable, colors);
            return wrapDrawable;
        }
        return drawable;
    }

    public static Drawable drawableResize(Drawable image, int resizeWidth, int resizeHeight) {
        int size = image.getIntrinsicHeight();
        if (size <= DensityTool.dp2px(resizeHeight)) {
            return image;
        }
        Bitmap b = ((BitmapDrawable) image).getBitmap();
        Bitmap bitmapResized = Bitmap.createScaledBitmap(
                b,
                DensityTool.dp2px(resizeWidth),
                DensityTool.dp2px(resizeHeight),
                false
        );
        return new BitmapDrawable(
                ToolInit.getApplicationContext().getResources(),
                bitmapResized
        );
    }

    public static String getAssetsText(String fileName) {
        // 将json数据变成字符串
        StringBuilder stringBuilder = new StringBuilder();
        try {
            // 获取assets资源管理器
            AssetManager assetManager = ToolInit.getApplicationContext().getAssets();
            // 通过管理器打开文件并读取
            BufferedReader bf = new BufferedReader(
                    new InputStreamReader(assetManager.open(fileName))
            );
            String line;
            while ((line = bf.readLine()) != null) {
                stringBuilder.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stringBuilder.toString();
    }

    public static File getAssetsFile(String filePath) {
        File file =
                new File(ToolInit.getApplicationContext().getExternalCacheDir().toString() + "/" + filePath);
        if (!file.exists()) {
            File parentFile = file.getParentFile();
            if (parentFile != null && !parentFile.exists()) {
                parentFile.mkdir();
            }
        }
        AssetManager am = ToolInit.getApplicationContext().getAssets();
        InputStream inputStream = null;
        OutputStream outputStream = null;
        try {
            inputStream = am.open(filePath);
            outputStream = new FileOutputStream(file);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }
        } catch (Exception e) {
            e.printStackTrace();
            file = null;
        } finally {
            try {
                if (outputStream != null) {
                    outputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return file;
    }

    public static Bitmap getAssetsBitmap(Context context, String fileName) {
        Bitmap image = null;
        AssetManager am = context.getAssets();
        try {
            InputStream is = am.open(fileName);
            image = BitmapFactory.decodeStream(is);
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return image;
    }

    public static Drawable getAssetsDrawable(Context ctx, String fileName) {
        try {
            InputStream inputStream = ctx.getResources().getAssets().open(fileName);
            return Drawable.createFromStream(inputStream, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 根据资源名称获取资源 id
     * 不提倡使用这个方法获取资源,比其直接获取ID效率慢
     * 例如
     * getResources().getIdentifier("ic_launcher", "mipmap", getPackageName());
     *
     * @param name
     * @param defType
     * @return
     */
    public static int getIdByName(String name, String defType) {
        return ToolInit.getApplicationContext().getResources()
                .getIdentifier(
                        name, defType,
                        ToolInit.getApplicationContext().getPackageName()
                );
    }

    public static int getMipmapResForName(String name) {
        return getIdByName(
                name.replace(".png", "")
                        .replace(".jpg", ""),
                "mipmap"
        );
    }

    public static int getRawForName(String name) {
        return getIdByName(name, "raw");
    }

    public static int getDimensForName(String name) {
        return getIdByName(name, "dimen");
    }
}
