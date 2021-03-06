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

package de.edgelord.saltyengine.emitter;

import de.edgelord.saltyengine.core.Component;
import de.edgelord.saltyengine.core.annotations.DefaultPlacement;
import de.edgelord.saltyengine.core.event.CollisionEvent;
import de.edgelord.saltyengine.core.graphics.SaltyGraphics;
import de.edgelord.saltyengine.core.stereotypes.ComponentContainer;
import de.edgelord.saltyengine.emitter.prc.PlainColorParticleRenderContext;
import de.edgelord.saltyengine.gameobject.Components;
import de.edgelord.saltyengine.transform.Coordinates2f;
import de.edgelord.saltyengine.transform.Dimensions;
import de.edgelord.saltyengine.utils.ColorUtil;
import de.edgelord.saltyengine.utils.GeneralUtil;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A {@link Component} that emits {@link Particle}s from its {@link de.edgelord.saltyengine.gameobject.GameObject} parent.
 * It emits instances of {@link #particle}, whose dimensions can be manipulated with {@link #fixedParticleDimensions} or
 * {@link #fixedMinParticleDimensions} and {@link #fixedMaxParticleDimensions}.
 */
@DefaultPlacement(method = DefaultPlacement.Method.PARENT)
public abstract class EmitterComponent extends Component<ComponentContainer> {

    /**
     * The amount of particles to spawn in one wave
     */
    private float amount;

    /**
     * The amount of ticks after which a new wave of Particles should be emitted
     */
    private int waveDuration;

    /**
     * Determines after how many fixed ticks the particles of one wave should disappear again.
     */
    private int lifespan = 1000;

    /**
     * The number of the current wave. This value is incremented after spawning each wave, so the first spawned wave has the number 0.
     */
    private int currentWave = 0;

    /**
     * A fixed min dimensions for new {@link Particle}s, being set after the initialization of each particle.
     * <p>
     * Special cases:
     * If {@link #fixedParticleDimensions} is not <code>null</code>, {@link #fixedParticleDimensions} will be set as the particle's dimensions
     * If either this or {@link #fixedMaxParticleDimensions} are <code>null</code>, the particles dimensions stays untouched.
     */
    private Dimensions fixedMinParticleDimensions = null;

    /**
     * A fixed max dimensions for new {@link Particle}s, being set after the initialization of each particle.
     * <p>
     * Special cases:
     * If {@link #fixedParticleDimensions} is not <code>null</code>, {@link #fixedParticleDimensions} will be set as the particle's dimensions
     * If either this or {@link #fixedMaxParticleDimensions} are <code>null</code>, the particles dimensions stays untouched.
     */
    private Dimensions fixedMaxParticleDimensions = null;

    /**
     * If this is not <code>null</code>, every {@link Particle} that is emitted by this {@link EmitterComponent} will
     * have that exact size.
     */
    private Dimensions fixedParticleDimensions = null;

    private int ticks = 0;
    private int ticks2 = 0;

    /**
     * The {@link ParticleRenderContext} that is used to render the particles.
     * By default, it is a {@link PlainColorParticleRenderContext} with a {@link ColorUtil#BLACK} color.
     */
    private ParticleRenderContext renderContext = new PlainColorParticleRenderContext(ColorUtil.BLACK);

    /**
     * A thread-safe {@link List} containing all the current {@link Particle}s
     */
    private List<Particle> currentParticles = Collections.synchronizedList(new ArrayList<>());

    /**
     * The {@link Class} object of the particle to be emitted.
     */
    private Class<? extends Particle> particle;

    /**
     * The constructor.
     *
     * @param parent       the {@link de.edgelord.saltyengine.gameobject.GameObject} that owns this {@link Component}
     * @param name         the id-name of the component
     * @param particle     the particle to be emitted. obtained via {@link Object#getClass()}
     * @param amount       the amount of emitted particles per wave
     * @param waveDuration the time to be passed between each wave
     */
    public EmitterComponent(ComponentContainer parent, String name, Class<? extends Particle> particle, float amount, int waveDuration) {
        super(parent, name, Components.EMITTER_COMPONENT);
        this.particle = particle;
        this.amount = amount;
        this.waveDuration = waveDuration;
    }

    @Override
    public final void initialize() {
    }

    /**
     * Use to initialize the emitter after the constructor. This method is called within {@link #initialize()}.
     */
    public abstract void initializeEmitter();

    /**
     * Spawns a single new particle with the following steps:
     * <p>
     * 1. Create a {@link Particle} using {@link #createParticle()}
     * 2. Set a emitter-specific position using {@link Particle#setPosition(Coordinates2f)}
     * 3. return it
     *
     * @return the spawned particle to be added to the list
     */
    public abstract Particle spawnParticle();

    /**
     * Moves each particle separately. This is called every fixed tick for every particle within {@link #currentParticles}.
     *
     * @param particle the <code>Particle</code> to be moved
     */
    public abstract void moveParticle(Particle particle);

    @Override
    public void onCollision(CollisionEvent event) {
    }

    /**
     * Calls {@link #spawnParticle()} every {@link #waveDuration} fixed ticks for {@link #amount} times and calls {@link #moveParticle(Particle)} every fixed tick for every entry in {@link #currentParticles}.
     */
    @Override
    public final void onFixedTick() {

        if (ticks2 >= lifespan) {
            ticks2 = 0;
            currentParticles.removeIf(particle -> particle.getWaveNumber() == currentWave - 1);
        } else {
            ticks2++;
        }

        if (ticks >= waveDuration) {
            ticks = 0;
            for (int i = 0; i < amount; i++) {
                addParticle(spawnParticle());
            }
            currentWave++;
        } else {
            ticks++;
        }

        for (int i = 0; i < currentParticles.size(); i++) {
            moveParticle(currentParticles.get(i));
        }
    }

    /**
     * Draws all {@link Particle}s within {@link #currentParticles} by calling {@link Particle#draw(SaltyGraphics)}.
     *
     * @param saltyGraphics the graphics to render the particles, this is internally passed in.
     */
    @Override
    public void draw(SaltyGraphics saltyGraphics) {
        for (int i = 0; i < currentParticles.size(); i++) {
            Particle particle = currentParticles.get(i);
            renderContext.nextParticleRenderConfig(saltyGraphics, particle);
            particle.draw(saltyGraphics);
        }
    }

    /**
     * Returns a new instance of {@link #particle} with the {@link #currentWave}
     *
     * @return a new particle
     */
    public Particle createParticle() {
        try {

            Particle particle = this.particle.getConstructor(Integer.class).newInstance(currentWave);

            if (fixedParticleDimensions != null) {
                particle.setDimensions(fixedParticleDimensions);
            } else if (fixedMinParticleDimensions != null && fixedMaxParticleDimensions != null) {
                particle.setDimensions(GeneralUtil.randomDimensions(fixedMinParticleDimensions.getWidth(), fixedMaxParticleDimensions.getWidth(), fixedMinParticleDimensions.getHeight(), fixedMaxParticleDimensions.getHeight()));
            }

            return particle;
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * The only way to add a {@link Particle} to the {@link #currentParticles}.
     *
     * @param particle the <code>Particle</code> to be added.
     */
    private void addParticle(Particle particle) {
        currentParticles.add(particle);
    }

    public float getAmount() {
        return amount;
    }

    public void setAmount(float amount) {
        this.amount = amount;
    }

    public int getWaveDuration() {
        return waveDuration;
    }

    public void setWaveDuration(int waveDuration) {
        this.waveDuration = waveDuration;
    }

    public void setRenderContext(ParticleRenderContext renderContext) {
        this.renderContext = renderContext;
    }

    public Dimensions getFixedMinParticleDimensions() {
        return fixedMinParticleDimensions;
    }

    public void setFixedMinParticleDimensions(Dimensions fixedMinParticleDimensions) {
        this.fixedMinParticleDimensions = fixedMinParticleDimensions;
    }

    public Dimensions getFixedMaxParticleDimensions() {
        return fixedMaxParticleDimensions;
    }

    public void setFixedMaxParticleDimensions(Dimensions fixedMaxParticleDimensions) {
        this.fixedMaxParticleDimensions = fixedMaxParticleDimensions;
    }

    public Dimensions getFixedParticleDimensions() {
        return fixedParticleDimensions;
    }

    public void setFixedParticleDimensions(Dimensions fixedParticleDimensions) {
        this.fixedParticleDimensions = fixedParticleDimensions;
    }
}
