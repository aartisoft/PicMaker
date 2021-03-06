package com.yd.photoeditor.imageprocessing.filter.effect;

import android.opengl.GLES20;
import com.yd.photoeditor.imageprocessing.filter.ImageRender;

public class F1234 extends ImageRender {
    public static final String POSTERIZE_FRAGMENT_SHADER = "precision highp float;\nvarying highp vec2 textureCoordinate;\n\nuniform sampler2D inputImageTexture;\nuniform highp float colorLevels;\n\nvoid main()\n{\n   highp vec4 textureColor = texture2D(inputImageTexture, textureCoordinate);\n   \n   gl_FragColor = floor((textureColor * colorLevels) + vec4(0.5)) / colorLevels;\n}";
    private int mColorLevels;
    private int mGLUniformColorLevels;

    public F1234() {
        this(10);
    }

    public F1234(int i) {
        super(ImageRender.NO_FILTER_VERTEX_SHADER, POSTERIZE_FRAGMENT_SHADER);
        this.mColorLevels = i;
    }

    public void onInit() {
        super.onInit();
        this.mGLUniformColorLevels = GLES20.glGetUniformLocation(getProgram(), "colorLevels");
        setColorLevels(this.mColorLevels);
    }

    public void setColorLevels(int i) {
        this.mColorLevels = i;
        setFloat(this.mGLUniformColorLevels, (float) i);
    }
}
