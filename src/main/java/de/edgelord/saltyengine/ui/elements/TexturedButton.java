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

package de.edgelord.saltyengine.ui.elements;

import de.edgelord.saltyengine.core.annotations.DefaultPlacement;
import de.edgelord.saltyengine.core.graphics.SaltyGraphics;
import de.edgelord.saltyengine.transform.Coordinates2f;

import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

@DefaultPlacement(method = DefaultPlacement.Method.TOP_LEFT_CORNER)
public abstract class TexturedButton extends Button {

    private boolean drawText = true;
    private BufferedImage texture;

    public TexturedButton(String text, Coordinates2f position, int width, int height, BufferedImage texture) {
        super(text, position, width, height);

        this.texture = texture;
    }

    @Override
    public void drawText(SaltyGraphics saltyGraphics) {
        if (drawText) {
            super.drawText(saltyGraphics);
        }
    }

    @Override
    public void drawButton(SaltyGraphics saltyGraphics) {
        saltyGraphics.drawImage(texture, getX(), getY(), getWidth(), getHeight());
    }

    public abstract void onClick(MouseEvent e);

    public boolean isDrawText() {
        return drawText;
    }

    public void setDrawText(boolean drawText) {
        this.drawText = drawText;
    }

    public BufferedImage getTexture() {
        return texture;
    }

    public void setTexture(BufferedImage texture) {
        this.texture = texture;
    }
}
