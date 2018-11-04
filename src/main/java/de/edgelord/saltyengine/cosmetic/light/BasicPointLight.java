/*
 * This software was published under the MIT License.
 * The full LICENSE file can be found here: https://github.com/edgelord314/salty-enigne/tree/master/LICENSE
 *
 * Copyright (c) since 2018 by the Salty Engine developers,
 * Maintained by Malte Dostal
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 */

package de.edgelord.saltyengine.cosmetic.light;

import java.awt.*;
import java.awt.image.BufferedImage;

public class BasicPointLight extends Light {

    private BufferedImage image;
    private int x;
    private int y;

    public BasicPointLight(int x, int y, int radius, float luminosity) {
        //Recommended luminosity between 1 and 2
        this.x = x;
        this.y = y;
        image = new BufferedImage(radius * 2, radius * 2, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = (Graphics2D) image.getGraphics();

        for(int i = 0; i < radius; i++) {
            double luma = 1.0D - ((i + 0.001) / radius);
            int alpha = Math.min((int)(255.0D * luma * luminosity), 255);
            g2.setColor(new Color(0, 0, 0, alpha));
            g2.setStroke(new BasicStroke(2));
            g2.drawOval(radius - i, radius - i, i * 2, i * 2);
        }
    }

    public void render(Graphics2D g2) {
        g2.drawImage(image, x - image.getWidth() / 2, y - image.getHeight() / 2, image.getWidth(), image.getHeight(), null);
    }
    @Override
    public void draw(Graphics2D graphics) {
        render(graphics);
    }
}
