package com.yd.photoeditor.imageprocessing.filter;

import android.graphics.Bitmap;
import android.opengl.GLES20;
import com.yd.photoeditor.imageprocessing.temp.ImageFilter6;
import com.yd.photoeditor.imageprocessing.util.OpenGlUtils;
import com.yd.photoeditor.imageprocessing.util.TextureRotationUtil;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class TwoInputRender extends ImageRender {
    private static final String VERTEX_SHADER = "precision highp float;\nattribute vec4 position;\nattribute vec4 inputTextureCoordinate;\nattribute vec4 inputTextureCoordinate2;\n \nvarying vec2 textureCoordinate;\nvarying vec2 textureCoordinate2;\n \nvoid main()\n{\n    gl_Position = position;\n    textureCoordinate = inputTextureCoordinate.xy;\n    textureCoordinate2 = inputTextureCoordinate2.xy;\n}";
    public int filterInputTextureUniform2;
    public int filterSecondTextureCoordinateAttribute;
    public int filterSourceTexture2;
    private Bitmap mBitmap;
    private boolean mRecycleBitmap;
    private ByteBuffer mTexture2CoordinatesBuffer;

    public TwoInputRender(String str) {
        this(VERTEX_SHADER, str);
    }

    public TwoInputRender(String str, String str2) {
        super(str, str2);
        this.filterSourceTexture2 = -1;
        this.mRecycleBitmap = true;
        setRotation(ImageFilter6.NORMAL, false, false);
    }

    public void onInit() {
        super.onInit();
        this.filterSecondTextureCoordinateAttribute = GLES20.glGetAttribLocation(getProgram(), "inputTextureCoordinate2");
        this.filterInputTextureUniform2 = GLES20.glGetUniformLocation(getProgram(), "inputImageTexture2");
        GLES20.glEnableVertexAttribArray(this.filterSecondTextureCoordinateAttribute);
        Bitmap bitmap = this.mBitmap;
        if (bitmap != null && !bitmap.isRecycled()) {
            setBitmap(this.mBitmap);
        }
    }

    public void setBitmap(final Bitmap bitmap) {
        this.mBitmap = bitmap;
        runOnDraw(new Runnable() {
            public void run() {
                if (TwoInputRender.this.filterSourceTexture2 == -1) {
                    GLES20.glActiveTexture(33987);
                    TwoInputRender.this.filterSourceTexture2 = OpenGlUtils.loadTexture(bitmap, -1, false);
                }
            }
        });
    }

    public void onDestroy() {
        super.onDestroy();
        GLES20.glDeleteTextures(1, new int[]{this.filterSourceTexture2}, 0);
        this.filterSourceTexture2 = -1;
        Bitmap bitmap = this.mBitmap;
        if (bitmap != null && !bitmap.isRecycled() && this.mRecycleBitmap) {
            this.mBitmap.recycle();
            this.mBitmap = null;
        }
    }

    /* access modifiers changed from: protected */
    public void onDrawArraysPre() {
        GLES20.glEnableVertexAttribArray(this.filterSecondTextureCoordinateAttribute);
        GLES20.glActiveTexture(33987);
        GLES20.glBindTexture(3553, this.filterSourceTexture2);
        GLES20.glUniform1i(this.filterInputTextureUniform2, 3);
        this.mTexture2CoordinatesBuffer.position(0);
        GLES20.glVertexAttribPointer(this.filterSecondTextureCoordinateAttribute, 2, 5126, false, 0, this.mTexture2CoordinatesBuffer);
    }

    public void setRotation(ImageFilter6 imageFilter6, boolean z, boolean z2) {
        float[] rotation2 = TextureRotationUtil.getRotation(imageFilter6, z, z2);
        ByteBuffer order = ByteBuffer.allocateDirect(32).order(ByteOrder.nativeOrder());
        FloatBuffer asFloatBuffer = order.asFloatBuffer();
        asFloatBuffer.put(rotation2);
        asFloatBuffer.flip();
        this.mTexture2CoordinatesBuffer = order;
    }

    public void setRecycleBitmap(boolean z) {
        this.mRecycleBitmap = z;
    }

    public boolean isRecycleBitmap() {
        return this.mRecycleBitmap;
    }

    public Bitmap getBitmap() {
        return this.mBitmap;
    }

    public void recycleBitmap() {
        Bitmap bitmap = this.mBitmap;
        if (bitmap != null && !bitmap.isRecycled()) {
            this.mBitmap.recycle();
            setBitmap(null);
        }
    }
}
