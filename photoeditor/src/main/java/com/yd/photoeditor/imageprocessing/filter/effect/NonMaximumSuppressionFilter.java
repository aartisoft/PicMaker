package com.yd.photoeditor.imageprocessing.filter.effect;

import com.yd.photoeditor.imageprocessing.filter.colour.TextureSamplingFilter;

public class NonMaximumSuppressionFilter extends TextureSamplingFilter {
    public static final String NMS_FRAGMENT_SHADER = "uniform sampler2D inputImageTexture;\n\nvarying highp vec2 textureCoordinate;\nvarying highp vec2 leftTextureCoordinate;\nvarying highp vec2 rightTextureCoordinate;\n\nvarying highp vec2 topTextureCoordinate;\nvarying highp vec2 topLeftTextureCoordinate;\nvarying highp vec2 topRightTextureCoordinate;\n\nvarying highp vec2 bottomTextureCoordinate;\nvarying highp vec2 bottomLeftTextureCoordinate;\nvarying highp vec2 bottomRightTextureCoordinate;\n\nvoid main()\n{\nlowp float bottomColor = texture2D(inputImageTexture, bottomTextureCoordinate).r;\nlowp float bottomLeftColor = texture2D(inputImageTexture, bottomLeftTextureCoordinate).r;\nlowp float bottomRightColor = texture2D(inputImageTexture, bottomRightTextureCoordinate).r;\nlowp vec4 centerColor = texture2D(inputImageTexture, textureCoordinate);\nlowp float leftColor = texture2D(inputImageTexture, leftTextureCoordinate).r;\nlowp float rightColor = texture2D(inputImageTexture, rightTextureCoordinate).r;\nlowp float topColor = texture2D(inputImageTexture, topTextureCoordinate).r;\nlowp float topRightColor = texture2D(inputImageTexture, topRightTextureCoordinate).r;\nlowp float topLeftColor = texture2D(inputImageTexture, topLeftTextureCoordinate).r;\n\n// Use a tiebreaker for pixels to the left and immediately above this one\nlowp float multiplier = 1.0 - step(centerColor.r, topColor);\nmultiplier = multiplier * 1.0 - step(centerColor.r, topLeftColor);\nmultiplier = multiplier * 1.0 - step(centerColor.r, leftColor);\nmultiplier = multiplier * 1.0 - step(centerColor.r, bottomLeftColor);\n\nlowp float maxValue = max(centerColor.r, bottomColor);\nmaxValue = max(maxValue, bottomRightColor);\nmaxValue = max(maxValue, rightColor);\nmaxValue = max(maxValue, topRightColor);\n\ngl_FragColor = vec4((centerColor.rgb * step(maxValue, centerColor.r) * multiplier), 1.0);\n}\n";

    public NonMaximumSuppressionFilter() {
        super(NMS_FRAGMENT_SHADER);
    }
}
