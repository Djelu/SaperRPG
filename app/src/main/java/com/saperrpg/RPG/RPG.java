package com.saperrpg.RPG;

public class RPG {
    private Stats stats;
    private Stats[] loot;

    public RPG() {
        this.stats = new Stats();
        this.loot = new Stats[14];
    }

    public Stats getAllStats(){return stats;}

    public Stats getStats() {
        return stats;
    }

    public Stats[] getLoot() {
        return loot;
    }
}
