package com.yd.photoeditor.imageprocessing.filter.colour;

import com.yd.photoeditor.imageprocessing.filter.TwoInputFilter;

public class OneDimenLookupFilter extends TwoInputFilter {
    public static final String LOOKUP_FRAGMENT_SHADER = "precision highp float;\nvarying highp vec2 textureCoordinate;\n varying highp vec2 textureCoordinate2; // TODO: This is not used\n \n uniform sampler2D inputImageTexture;\n uniform sampler2D inputImageTexture2; // lookup texture\n \n void main()\n {\n     lowp vec4 textureColor = texture2D(inputImageTexture, textureCoordinate);\nif(textureColor.r < 0.003 && textureColor.g < 0.003 && textureColor.b < 0.003 && textureColor.w < 0.003)\ngl_FragColor = textureColor;\nelse{\n\t  vec3 outColor;\t  outColor.r = texture2D(inputImageTexture2, vec2(textureColor.r, 0.0)).r;\n\t  outColor.g = texture2D(inputImageTexture2, vec2(textureColor.g, 0.5)).g;\n\t  outColor.b = texture2D(inputImageTexture2, vec2(textureColor.b, 1.0)).b;\n     gl_FragColor = vec4(outColor, textureColor.w);\n}\n }";

    public OneDimenLookupFilter() {
        super(LOOKUP_FRAGMENT_SHADER);
    }
}
