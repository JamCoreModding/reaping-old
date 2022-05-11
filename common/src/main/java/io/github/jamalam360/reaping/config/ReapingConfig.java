package io.github.jamalam360.reaping.config;

/**
 * @author Jamalam360
 */
public interface ReapingConfig {
    boolean enableDispenserBehavior();

    boolean damageAnimals();

    boolean dropXp();

    boolean reapBabies();

    boolean reapPlayers();

    int deathChance();
}
