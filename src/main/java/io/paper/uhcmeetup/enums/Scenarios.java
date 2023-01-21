package io.paper.uhcmeetup.enums;

import org.bukkit.Material;

public enum Scenarios {
    Default(false, 0, Material.TORCH),
    Bowless(false, 0, Material.BOW),
    NoClean(false, 0, Material.DIAMOND_SWORD),
    Rodless(false, 0, Material.FISHING_ROD),
    Fireless(false, 0, Material.FIRE),
    TimeBomb(false, 0, Material.TNT),
    Soup(false, 0, Material.MUSHROOM_SOUP);

    private boolean enabled;
    private int votes;
    private Material scenarioItem;

    private Scenarios(boolean enabled, int votes, Material scenarioItem) {
        this.enabled = enabled;
        this.votes = votes;
        this.scenarioItem = scenarioItem;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public void addVote() {
        ++this.votes;
    }

    public void removeVote() {
        --this.votes;
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    public int getVotes() {
        return this.votes;
    }

    public Material getScenarioItem() {
        return this.scenarioItem;
    }
}
