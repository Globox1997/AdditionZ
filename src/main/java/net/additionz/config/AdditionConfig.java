package net.additionz.config;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.Comment;
import net.additionz.AdditionMain;

@Config(name = "additionz")
@Config.Gui.Background("minecraft:textures/block/stone.png")
public class AdditionConfig implements ConfigData {

    public boolean feather_falling_trample = true;
    public boolean enderman_particles = true;
    @ConfigEntry.Gui.RequiresRestart
    public boolean block_pearcing_enchantment = true;
    @ConfigEntry.Gui.RequiresRestart
    public boolean stampede_enchantment = true;
    @ConfigEntry.Gui.RequiresRestart
    public boolean eagle_eyed_enchantment = true;
    @ConfigEntry.Gui.RequiresRestart
    public boolean dexterity_enchantment = true;
    @Comment("Disable when LevelZ mod installed")
    @ConfigEntry.Gui.RequiresRestart
    public boolean inaccuracy_curse_enchantment = AdditionMain.isLevelzLoaded ? false : true;
    public boolean polar_star = true;
    @Comment("Only visible through spy glass")
    public boolean other_stars = true;
    public boolean custom_item_name_non_despawn = true;
    @ConfigEntry.Gui.RequiresRestart
    public boolean totem_of_non_breaking = true;
    @Comment("0.1 = 10% chance")
    public float evoker_use_totem_chance = 0.1F;
    public boolean not_look_at_invisible = true;
    public boolean chainmail_spike_protection = true;
    public float charged_creeper_spawn_chance = 0.005F;
    public boolean creeper_on_fire = true;
    public boolean fast_oxidization = true;
    public boolean spectral_arrow_light = true;
    public boolean iron_golem_repair_friendly = true;
    public boolean skeleton_bow_damaged = true;
    public float break_skeleton_bow_chance = 0.005F;
    public boolean path_block_speed_boost = true;
    @Comment("Ticks: 20 = 1 second")
    public int disable_elytra_on_damage_time = 40;
    public boolean disable_elytra_underwater = false;
    @ConfigEntry.Gui.RequiresRestart
    public boolean husk_drops_sand = true;
    @ConfigEntry.Gui.RequiresRestart
    public boolean trident_buried_treasure = true;
    public boolean villager_sleeping_eyes = true;
    @Comment("No trading when villager has no space")
    public boolean villager_needs_space = true;
    public boolean path_block_under_gates = true;
    public boolean shovel_undo_farmland = true;
    @Comment("If top is banner, player has to wear shield with same pattern")
    public boolean beacon_shield_pattern = true;
    @Comment("0 = disabled")
    public int max_spawner_count = 0;
    @Comment("90 = 3 bubbles")
    public int botte_air_amount = 90;
    public boolean flame_arrow_burn = true;
    @Comment("Set entity on fire")
    public int magma_cube_fire_ticks = 20;
    @Comment("20 ticks = 1 second")
    public int shield_blocking_cooldown = 60;
    @Comment("Heal passive entity 2hp every ticks")
    public int heal_passive_entity_over_time_ticks = 1200;
    @Comment("Change slot highlight to texture")
    public boolean change_slot_highlight = false;
    public boolean fletching_table_use = true;
    public boolean show_nbt_tooltip = false;

    public boolean passive_entity_modifications = true;

    @ConfigEntry.Gui.CollapsibleObject
    public PassiveEntityConfig passiveEntityConfig = new PassiveEntityConfig();

    public static class PassiveEntityConfig {
        @Comment("Default = -24000")
        public int baby_to_adult_time = -24000;
        @Comment("PassiveAgeTime : this = Age")
        public int passive_age_calculation = 24000;
        @Comment("Baby = Age 0")
        public int passive_max_age = 3;
    }

}
