/*
 * Copyright (c) by Malte Dostal
 * Germany, 8.2018
 * All rights reserved
 */

package de.edgelord.saltyengine.ui.elements;

import de.edgelord.saltyengine.graphics.SaltyGraphics;
import de.edgelord.saltyengine.transform.Vector2f;

import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

public abstract class TexturedButton extends Button {

    private boolean drawText = true;
    private BufferedImage texture;

    public TexturedButton(String text, Vector2f position, int width, int height, BufferedImage texture) {
        super(text, position, width, height);

        this.texture = texture;
    }

    @Override
    public void drawText(SaltyGraphics saltyGraphics) {
        if (drawText) {
            super.drawText(saltyGraphics);
        } else {
            return;
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
