package de.me.edgelord.sjgl.gameobjectComponent;

import de.me.edgelord.sjgl.gameobject.GameObject;

import java.awt.*;

public abstract class Component {

    private GameObject parent;
    private String name;
    private boolean enabled = true;

    public Component(GameObject parent) {
        this.parent = parent;
    }

    public GameObject getParent() {
        return parent;
    }

    public abstract void onFixedTick();
    public abstract void draw(Graphics2D graphics);
    public abstract void onCollision(GameObject other);

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void enable(){
        enabled = true;
    }

    public void disable(){
        enabled = false;
    }

    public boolean isEnabled() {
        return enabled;
    }
}
