package com.yd.photoeditor.imageprocessing.filter;

import android.opengl.GLES20;

import com.yd.photoeditor.imageprocessing.ImageRenderer;
import com.yd.photoeditor.imageprocessing.Rotation;
import com.yd.photoeditor.imageprocessing.util.TextureRotationUtil;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

public class ImageFilterGroup extends ImageFilter {
    protected List<ImageFilter> mFilters;
    private int[] mFrameBufferTextures;
    private int[] mFrameBuffers;
    private final FloatBuffer mGLCubeBuffer;
    private final FloatBuffer mGLTextureBuffer;
    private final FloatBuffer mGLTextureFlipBuffer;
    protected List<ImageFilter> mMergedFilters;

    public ImageFilterGroup() {
        this(null);
    }

    public ImageFilterGroup(List<ImageFilter> list) {
        this.mFilters = list;
        if (this.mFilters == null) {
            this.mFilters = new ArrayList();
        } else {
            updateMergedFilters();
        }
        this.mGLCubeBuffer = ByteBuffer.allocateDirect(ImageRenderer.CUBE.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
        this.mGLCubeBuffer.put(ImageRenderer.CUBE).position(0);
        this.mGLTextureBuffer = ByteBuffer.allocateDirect(TextureRotationUtil.TEXTURE_NO_ROTATION.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
        this.mGLTextureBuffer.put(TextureRotationUtil.TEXTURE_NO_ROTATION).position(0);
        float[] rotation = TextureRotationUtil.getRotation(Rotation.NORMAL, false, true);
        this.mGLTextureFlipBuffer = ByteBuffer.allocateDirect(rotation.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
        this.mGLTextureFlipBuffer.put(rotation).position(0);
    }

    public void addFilter(ImageFilter imageFilter) {
        if (imageFilter != null) {
            this.mFilters.add(imageFilter);
            updateMergedFilters();
        }
    }

    public void onInit() {
        super.onInit();
        for (ImageFilter init : this.mFilters) {
            init.init();
        }
    }

    public void onDestroy() {
        destroyFramebuffers();
        for (ImageFilter destroy : this.mFilters) {
            destroy.destroy();
        }
        super.onDestroy();
    }

    private void destroyFramebuffers() {
        int[] iArr = this.mFrameBufferTextures;
        if (iArr != null) {
            GLES20.glDeleteTextures(iArr.length, iArr, 0);
            this.mFrameBufferTextures = null;
        }
        int[] iArr2 = this.mFrameBuffers;
        if (iArr2 != null) {
            GLES20.glDeleteFramebuffers(iArr2.length, iArr2, 0);
            this.mFrameBuffers = null;
        }
    }

    public void onOutputSizeChanged(int i, int i2) {
        super.onOutputSizeChanged(i, i2);
        if (this.mFrameBuffers != null) {
            destroyFramebuffers();
        }
        int size = this.mFilters.size();
        for (int i3 = 0; i3 < size; i3++) {
            this.mFilters.get(i3).onOutputSizeChanged(i, i2);
        }
        int i4 = i;
        int i5 = i2;
        List<ImageFilter> list = this.mMergedFilters;
        if (list != null && list.size() > 0) {
            int i6 = 1;
            int size2 = this.mMergedFilters.size() - 1;
            this.mFrameBuffers = new int[size2];
            this.mFrameBufferTextures = new int[size2];
            int i7 = 0;
            while (i7 < size2) {
                GLES20.glGenFramebuffers(i6, this.mFrameBuffers, i7);
                GLES20.glGenTextures(i6, this.mFrameBufferTextures, i7);
                GLES20.glBindTexture(3553, this.mFrameBufferTextures[i7]);
                GLES20.glTexImage2D(3553, 0, 6408, i, i2, 0, 6408, 5121, null);
                GLES20.glTexParameterf(3553, 10240, 9729.0f);
                GLES20.glTexParameterf(3553, 10241, 9729.0f);
                GLES20.glTexParameterf(3553, 10242, 33071.0f);
                GLES20.glTexParameterf(3553, 10243, 33071.0f);
                GLES20.glBindFramebuffer(36160, this.mFrameBuffers[i7]);
                GLES20.glFramebufferTexture2D(36160, 36064, 3553, this.mFrameBufferTextures[i7], 0);
                GLES20.glBindTexture(3553, 0);
                GLES20.glBindFramebuffer(36160, 0);
                i7++;
                i6 = 1;
            }
        }
    }

    public void onDraw(int i, FloatBuffer floatBuffer, FloatBuffer floatBuffer2) {
        List<ImageFilter> list;
        runPendingOnDrawTasks();
        if (isInitialized() && this.mFrameBuffers != null && this.mFrameBufferTextures != null && (list = this.mMergedFilters) != null) {
            int size = list.size();
            int i2 = i;
            int i3 = 0;
            while (i3 < size) {
                ImageFilter imageFilter = this.mMergedFilters.get(i3);
                int i4 = size - 1;
                boolean z = i3 < i4;
                if (z) {
                    GLES20.glBindFramebuffer(36160, this.mFrameBuffers[i3]);
                    GLES20.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
                }
                if (i3 == 0) {
                    imageFilter.onDraw(i2, floatBuffer, floatBuffer2);
                } else if (i3 == i4) {
                    imageFilter.onDraw(i2, this.mGLCubeBuffer, size % 2 == 0 ? this.mGLTextureFlipBuffer : this.mGLTextureBuffer);
                } else {
                    imageFilter.onDraw(i2, this.mGLCubeBuffer, this.mGLTextureBuffer);
                }
                if (z) {
                    GLES20.glBindFramebuffer(36160, 0);
                    i2 = this.mFrameBufferTextures[i3];
                }
                i3++;
            }
        }
    }

    public List<ImageFilter> getFilters() {
        return this.mFilters;
    }

    public List<ImageFilter> getMergedFilters() {
        return this.mMergedFilters;
    }

    public void updateMergedFilters() {
        if (this.mFilters != null) {
            List<ImageFilter> list = this.mMergedFilters;
            if (list == null) {
                this.mMergedFilters = new ArrayList();
            } else {
                list.clear();
            }
            for (ImageFilter next : this.mFilters) {
                if (next instanceof ImageFilterGroup) {
                    ImageFilterGroup imageFilterGroup = (ImageFilterGroup) next;
                    imageFilterGroup.updateMergedFilters();
                    List<ImageFilter> mergedFilters = imageFilterGroup.getMergedFilters();
                    if (mergedFilters != null && !mergedFilters.isEmpty()) {
                        this.mMergedFilters.addAll(mergedFilters);
                    }
                } else {
                    this.mMergedFilters.add(next);
                }
            }
        }
    }
}
