package com.saperrpg.RPG;

/**
 * Created by Djelu on 14.02.2017.
 */

public class RPG {
    private Stats stats;
    private Stats[] loot;

    public RPG() {
        this.stats = new Stats();
        this.loot = new Stats[14];
    }

    public Stats getStats() {
        return stats;
    }

    public Stats[] getLoot() {
        return loot;
    }
}
