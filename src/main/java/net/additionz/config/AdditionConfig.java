package net.additionz.config;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.Comment;

@Config(name = "additionz")
@Config.Gui.Background("minecraft:textures/block/stone.png")
public class AdditionConfig implements ConfigData {

    // public int test0 = 50;
    // public float test1 = 25.0f;
    // public float test2 = 90.0f;
    // public float test3 = 0.0f;

    public boolean feather_falling_trample = true;
    public boolean enderman_particles = true;
    @ConfigEntry.Gui.RequiresRestart
    public boolean block_pearcing_enchantment = true;
    @ConfigEntry.Gui.RequiresRestart
    public boolean stampede_enchantment = true;
    @ConfigEntry.Gui.RequiresRestart
    public boolean eagle_eyed_enchantment = true;
    public boolean polar_star = true;
    @Comment("Only visible through spy glass")
    public boolean other_stars = true;
    public boolean custom_item_name_non_despawn = true;
    public boolean totem_of_non_breaking = true;
    @Comment("0.1 = 10% chance")
    public float evoker_use_totem_chance = 0.1F;
    public boolean not_look_at_invisible = true;
    public boolean chainmail_spike_protection = true;
    public float charged_creeper_spawn_chance = 0.005F;
    // public boolean iron_golem_repair_friendly = true;

}
