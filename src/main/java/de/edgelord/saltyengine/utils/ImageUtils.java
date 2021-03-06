/*
 * Copyright 2018 Malte Dostal
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.edgelord.saltyengine.utils;

import de.edgelord.saltyengine.core.Game;
import de.edgelord.saltyengine.core.Host;
import de.edgelord.saltyengine.core.graphics.SaltyGraphics;
import de.edgelord.saltyengine.core.interfaces.Drawable;
import de.edgelord.saltyengine.effect.geom.EnumShape;
import de.edgelord.saltyengine.effect.geom.SaltyShape;
import de.edgelord.saltyengine.resource.OuterResource;
import de.edgelord.saltyengine.transform.Coordinates2f;
import de.edgelord.saltyengine.transform.Dimensions;
import de.edgelord.saltyengine.transform.Transform;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class ImageUtils {

    public static String IMAGE_FORMAT_PNG = "png";
    public static String IMAGE_FORMAT_JPG = "jpg";
    public static String IMAGE_FORMAT_GIF = "gif";

    /**
     * Creates a new {@link BufferedImage} with the given size and draws the given {@link Drawable} onto it.
     *
     * @param drawable       what to draw onto the image
     * @param size           the size of the image
     * @param renderingHints the renderhints to use for drawing the image
     * @return a {@link BufferedImage} with the given size and the given {@link Drawable} performed on it
     */
    public static BufferedImage createPrimitiveImage(Drawable drawable, Dimensions size, RenderingHints renderingHints) {

        BufferedImage image = new BufferedImage(Math.round(size.getWidth()), Math.round(size.getHeight()), BufferedImage.TYPE_INT_ARGB);

        Graphics2D graphics2D = image.createGraphics();
        graphics2D.setRenderingHints(renderingHints);

        drawable.draw(new SaltyGraphics(graphics2D));
        graphics2D.dispose();

        return image;
    }

    /**
     * Creates a {@link BufferedImage} with the size of the given {@link SaltyShape} and draws it gradient-like into.
     * <p>
     * The given {@link Drawable} is called once before the rendering to e.g. call {@link SaltyGraphics#setColor(Color)}
     * or {@link SaltyGraphics#setPaint(Paint)} on the used {@link Graphics2D} of the image. The given {@link RenderingHints}
     * are also set to the graphics, normally you want to use {@link Host#getRenderHints()} for that.
     * <p>
     * The gradient will be radial and it'll have the given alpha in the centre. The given intensity effects how the gradient looks.
     *
     * @param shape           the {@link SaltyShape} to draw as a gradient
     * @param graphicsPrepare a {@link Drawable} to prepare e.g. the color. It can be null.
     * @param renderingHints  hints to define the quality. Most likely, you want to use {@link Host#getRenderHints()}
     *                        to have the same quality as the rest of the game
     * @param intensity       the intensity of the gradient. That effects hot it looks.
     * @param startAlpha      the alpha value of the centre
     * @return a {@link BufferedImage} containing the given {@link SaltyShape} drawn as a gradient.
     */
    public static BufferedImage createPrimitiveGradient(SaltyShape shape, Drawable graphicsPrepare, RenderingHints renderingHints, float intensity, double startAlpha) {

        BufferedImage image = new BufferedImage(Math.round(shape.getWidth()), Math.round(shape.getHeight()), BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = (Graphics2D) image.getGraphics();

        if (graphicsPrepare != null) {
            graphicsPrepare.draw(new SaltyGraphics(graphics));
        }
        graphics.setRenderingHints(renderingHints);
        int red = graphics.getColor().getRed();
        int green = graphics.getColor().getGreen();
        int blue = graphics.getColor().getBlue();

        float radius = shape.getWidth() / 2f;

        for (int i = 0; i < radius; i++) {
            double luma = 1.0D - ((i + 0.001D) / radius);
            int alpha = Math.min((int) (startAlpha * luma * intensity), 255);
            graphics.setColor(new Color(red, green, blue, alpha));
            int currentPos = Math.round(radius - i);
            int currentSize = i * 2;
            shape.setDimensions(new Dimensions(currentSize, currentSize));
            shape.setPosition(new Coordinates2f(currentPos, currentPos));
            shape.draw(new SaltyGraphics(graphics));
        }

        return image;
    }

    /**
     * Creates an image of a gradient using {@link #createPrimitiveGradient(SaltyShape, Drawable, RenderingHints, float, double)}.
     * For that, a shape is being created using {@link SaltyShape#createShape(EnumShape, Transform, float...)} with the given
     * {@link EnumShape} and the given {@link Dimensions} with a position at 0|0.
     *
     * @param shapeType       the type of the shape
     * @param graphicsPrepare a {@link Drawable} to prepare e.g. the color. It can be null
     * @param renderingHints  hints to define the quality. You normally want to use {@link Host#getRenderHints()}
     *                        for the same quality as the rest of the game
     * @param intensity       the intensity of the gradient. This defines how it looks.
     * @param startAlpha      the alpha value at the centre
     * @param size            the size of the shape and the image
     * @param arcIfRoundRect  an arc if the given {@link EnumShape} is {@link EnumShape#ROUND_RECTANGLE}
     * @return a {@link BufferedImage} containing a {@link SaltyShape} drawn as a gradient.
     */
    public static BufferedImage createPrimitiveGradient(EnumShape shapeType, Drawable graphicsPrepare, RenderingHints renderingHints, float intensity, double startAlpha, Dimensions size, float... arcIfRoundRect) {

        return createPrimitiveGradient(SaltyShape.createShape(shapeType, new Transform(0, 0, size.getWidth(), size.getHeight()), arcIfRoundRect), graphicsPrepare, renderingHints, intensity, startAlpha);
    }

    public static BufferedImage createPrimitiveGradient(EnumShape shapeType, Drawable graphicsPrepare, RenderingHints renderingHints, float intensity, Dimensions size, float... arcIfRoundRect) {
        return createPrimitiveGradient(shapeType, graphicsPrepare, renderingHints, intensity, 255D, size, arcIfRoundRect);
    }

    public static BufferedImage createPrimitiveGradient(EnumShape shapeType, RenderingHints renderingHints, float intensity, Dimensions size, float... arcIfRoundRect) {
        return createPrimitiveGradient(shapeType, saltyGraphics -> {
        }, renderingHints, intensity, 255D, size, arcIfRoundRect);
    }

    public static BufferedImage createPrimitiveGradient(EnumShape shapeType, float intensity, Dimensions size, float... arcIfRoundRect) {
        return createPrimitiveGradient(shapeType, saltyGraphics -> {
        }, Game.getHost().getRenderHints(), intensity, 255D, size, arcIfRoundRect);
    }

    /**
     * Creates and returns an image with the given in it drawn with the given Color.
     * The image is as big as the shape, but the shape is being drawn exactly in the middle, using {@link SaltyShape#drawAtZero(Graphics2D)}.
     *
     * @param shape          the shape to draw to the image
     * @param color          the color with which to draw the shape
     * @param renderingHints the quality to render the shape
     * @return a new image containing the given shape in the given color with the given quality
     */
    public static BufferedImage createShapeImage(SaltyShape shape, Color color, RenderingHints renderingHints) {
        BufferedImage image = new BufferedImage(Math.round(shape.getWidth()), Math.round(shape.getHeight()), BufferedImage.TYPE_INT_ARGB);

        Graphics2D graphics = image.createGraphics();
        graphics.setColor(color);
        graphics.setRenderingHints(renderingHints);
        shape.drawAtZero(graphics);
        graphics.dispose();

        return image;
    }

    /**
     * Creates a new {@link BufferedImage} with the size of the given {@link SaltyShape} and draws the shape onto it,
     * with having called the draw method of the given {@link Drawable} before to e.g. set the color or paint.
     *
     * @param shape           the {@link SaltyShape} to be rendered to the image.
     * @param graphicsPrepare a {@link Drawable} to prepare e.g. the Color of the shape
     * @return a new {@link BufferedImage} with the given {@link SaltyShape} drawn onto.
     */
    public static BufferedImage createShapeImage(SaltyShape shape, Drawable graphicsPrepare) {
        BufferedImage image = new BufferedImage(Math.round(shape.getWidth()), Math.round(shape.getHeight()), BufferedImage.TYPE_INT_ARGB);
        SaltyGraphics graphics = new SaltyGraphics(image.createGraphics());

        graphicsPrepare.draw(graphics);
        shape.drawAtZero(graphics.getGraphics2D());

        graphics.getGraphics2D().dispose();

        return image;
    }

    /**
     * Creates and returns an image using {@link #createShapeImage(SaltyShape, Color, RenderingHints)}.
     * The {@link SaltyShape} is made using {@link SaltyShape#createShape(EnumShape, Transform, float...)}.
     *
     * @param shape          the shape to be drawn to the image
     * @param size           the size of the shape and image
     * @param color          the color of the shape
     * @param renderingHints the quality of the render
     * @param arcIfRoundRect the ard if the given shape is {@link EnumShape#ROUND_RECTANGLE}
     * @return a new image with the given size containing the given shape in the given color with the given quality
     */
    public static BufferedImage createShapeImage(EnumShape shape, Dimensions size, Color color, RenderingHints renderingHints, float... arcIfRoundRect) {
        return createShapeImage(SaltyShape.createShape(shape, new Transform(Coordinates2f.zero(), size), arcIfRoundRect), color, renderingHints);
    }

    /**
     * Saves the given image to a file with the given name relative to the given {@link OuterResource}.
     * It saves it with the given format, use one of those three:
     * <p>
     * {@link #IMAGE_FORMAT_PNG}
     * {@link #IMAGE_FORMAT_JPG}
     * {@link #IMAGE_FORMAT_GIF}
     *
     * @param image    the {@link BufferedImage} to safe
     * @param format   the format of the file
     * @param name     the name of the file
     * @param resource the {@link OuterResource} to get the file from
     * @throws IOException when the I/O process fails
     */
    public static void saveImage(BufferedImage image, String format, String name, OuterResource resource) throws IOException {
        ImageIO.write(image, format, resource.getFileResource(name + "." + format));
    }
}
