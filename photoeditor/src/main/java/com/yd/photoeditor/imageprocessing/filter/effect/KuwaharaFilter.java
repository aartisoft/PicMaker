package com.yd.photoeditor.imageprocessing.filter.effect;

import android.opengl.GLES20;
import com.yd.photoeditor.imageprocessing.filter.ImageFilter;

public class KuwaharaFilter extends ImageFilter {
    public static final String KUWAHARA_FRAGMENT_SHADER = "varying highp vec2 textureCoordinate;\nuniform sampler2D inputImageTexture;\nuniform int radius;\n\nprecision highp float;\n\nconst vec2 src_size = vec2 (1.0 / 768.0, 1.0 / 1024.0);\n\nvoid main (void) \n{\nvec2 uv = textureCoordinate;\nfloat n = float((radius + 1) * (radius + 1));\nint i; int j;\nvec3 m0 = vec3(0.0); vec3 m1 = vec3(0.0); vec3 m2 = vec3(0.0); vec3 m3 = vec3(0.0);\nvec3 s0 = vec3(0.0); vec3 s1 = vec3(0.0); vec3 s2 = vec3(0.0); vec3 s3 = vec3(0.0);\nvec3 c;\n\nfor (j = -radius; j <= 0; ++j)  {\nfor (i = -radius; i <= 0; ++i)  {\nc = texture2D(inputImageTexture, uv + vec2(i,j) * src_size).rgb;\nm0 += c;\ns0 += c * c;\n}\n}\n\nfor (j = -radius; j <= 0; ++j)  {\nfor (i = 0; i <= radius; ++i)  {\nc = texture2D(inputImageTexture, uv + vec2(i,j) * src_size).rgb;\nm1 += c;\ns1 += c * c;\n}\n}\n\nfor (j = 0; j <= radius; ++j)  {\nfor (i = 0; i <= radius; ++i)  {\nc = texture2D(inputImageTexture, uv + vec2(i,j) * src_size).rgb;\nm2 += c;\ns2 += c * c;\n}\n}\n\nfor (j = 0; j <= radius; ++j)  {\nfor (i = -radius; i <= 0; ++i)  {\nc = texture2D(inputImageTexture, uv + vec2(i,j) * src_size).rgb;\nm3 += c;\ns3 += c * c;\n}\n}\n\n\nfloat min_sigma2 = 1e+2;\nm0 /= n;\ns0 = abs(s0 / n - m0 * m0);\n\nfloat sigma2 = s0.r + s0.g + s0.b;\nif (sigma2 < min_sigma2) {\nmin_sigma2 = sigma2;\ngl_FragColor = vec4(m0, 1.0);\n}\n\nm1 /= n;\ns1 = abs(s1 / n - m1 * m1);\n\nsigma2 = s1.r + s1.g + s1.b;\nif (sigma2 < min_sigma2) {\nmin_sigma2 = sigma2;\ngl_FragColor = vec4(m1, 1.0);\n}\n\nm2 /= n;\ns2 = abs(s2 / n - m2 * m2);\n\nsigma2 = s2.r + s2.g + s2.b;\nif (sigma2 < min_sigma2) {\nmin_sigma2 = sigma2;\ngl_FragColor = vec4(m2, 1.0);\n}\n\nm3 /= n;\ns3 = abs(s3 / n - m3 * m3);\n\nsigma2 = s3.r + s3.g + s3.b;\nif (sigma2 < min_sigma2) {\nmin_sigma2 = sigma2;\ngl_FragColor = vec4(m3, 1.0);\n}\n}\n";
    private int mRadius;
    private int mRadiusLocation;

    public KuwaharaFilter() {
        this(6);
    }

    public KuwaharaFilter(int i) {
        super(ImageFilter.NO_FILTER_VERTEX_SHADER, KUWAHARA_FRAGMENT_SHADER);
        this.mRadius = i;
    }

    public void onInit() {
        super.onInit();
        this.mRadiusLocation = GLES20.glGetUniformLocation(getProgram(), "radius");
    }

    public void onInitialized() {
        super.onInitialized();
        setRadius(this.mRadius);
    }

    public void setRadius(int i) {
        this.mRadius = i;
        setInteger(this.mRadiusLocation, i);
    }
}
