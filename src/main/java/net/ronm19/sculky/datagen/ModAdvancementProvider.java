package net.ronm19.sculky.datagen;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.ronm19.sculky.SculkyMod;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;

public class ModAdvancementProvider implements DataProvider {
    private final PackOutput.PathProvider pathProvider;

    public ModAdvancementProvider(PackOutput output) {
        // 1.21+ uses singular "advancement", matching data/sculky/advancement/*.json
        this.pathProvider = output.createPathProvider(PackOutput.Target.DATA_PACK, "advancement");
    }

    @Override
    public CompletableFuture<?> run(CachedOutput output) {
        return CompletableFuture.allOf(
                // ---------------------------------------------------------------------
                // Tab 1: Regular Sculky advancements
                // ---------------------------------------------------------------------
                save(output, "root", root()),

                // Biome / exploration branch
                save(output, "find_sculk_forest", findSculkForest()),
                save(output, "kill_sculky_mob", killSculkyMob()),
                save(output, "find_sculk_wastes", findSculkWastes()),
                save(output, "find_sculk_jungle", findSculkJungle()),
                save(output, "enter_all_sculk_biomes", enterAllSculkBiomes()),

                // Shrine / core / altar / Evoker branch
                save(output, "find_shrine", findShrine()),
                save(output, "eat_sculk_apple", eatSculkApple()),
                save(output, "obtain_sculk_core", obtainSculkCore()),
                save(output, "the_core_is_calling", theCoreIsCalling()),
                save(output, "find_altar", findAltar()),
                save(output, "royal_ritual", royalRitual()),
                save(output, "false_servant", falseServant()),
                save(output, "fang_of_the_throne", fangOfTheThrone()),

                // ---------------------------------------------------------------------
                // Tab 2: Sculk Sanctum / King advancements
                // enter_sanctum has NO parent, so it becomes its own advancement tab.
                // ---------------------------------------------------------------------
                save(output, "enter_sanctum", enterSanctum()),
                save(output, "the_king_whispers", theKingWhispers()),
                save(output, "before_the_king", beforeTheKing()),
                save(output, "the_buried_throne", buriedThrone()),
                save(output, "summon_sculk_king", summonSculkKing()),
                save(output, "defeat_sculk_king", defeatSculkKing()),
                save(output, "obtain_kings_axe", obtainKingsAxe()),
                save(output, "tame_sculk_bear", tameSculkBear())
        );
    }

    private CompletableFuture<?> save(CachedOutput output, String name, JsonObject advancement) {
        Path path = this.pathProvider.json(ResourceLocation.fromNamespaceAndPath(SculkyMod.MOD_ID, name));
        return DataProvider.saveStable(output, advancement, path);
    }

    @Override
    public @NotNull String getName() {
        return "Sculky Advancements";
    }

    // ---------------------------------------------------------------------
    // Tab 1: Regular Sculky advancements
    // ---------------------------------------------------------------------

    private static JsonObject root() {
        return advancement(
                null,
                display(
                        icon("sculky:sculk_shard"),
                        trans("advancement.sculky.root.title"),
                        trans("advancement.sculky.root.desc"),
                        "minecraft:textures/block/sculk.png",
                        "task",
                        false,
                        false,
                        false
                ),
                criteria("tick", criterion("minecraft:tick")),
                requirements("tick")
        );
    }

    private static JsonObject findSculkForest() {
        return advancement(
                "sculky:root",
                display(icon("sculky:infested_sculk_grass_block", 1), trans("advancement.sculky.find_sculk_forest.title"), trans("advancement.sculky.find_sculk_forest.desc"), null, "task", true, true, false),
                criteria("find_sculk_forest", locationBiomeCriterion("sculky:sculk_forest")),
                requirements("find_sculk_forest")
        );
    }

    private static JsonObject killSculkyMob() {
        return advancement(
                "sculky:find_sculk_forest",
                display(icon("minecraft:sculk_catalyst", 1), trans("advancement.sculky.kill_sculky_mob.title"), trans("advancement.sculky.kill_sculky_mob.desc"), null, "task", true, true, false),
                criteria("kill_sculky", killedEntityTypeCriterion("#sculky:sculk_mobs")),
                requirements("kill_sculky")
        );
    }

    private static JsonObject findSculkWastes() {
        return advancement(
                "sculky:find_sculk_forest",
                display(icon("sculky:infested_sculk_sand", 1), trans("advancement.sculky.find_sculk_wastes.title"), trans("advancement.sculky.find_sculk_wastes.desc"), null, "task", true, true, false),
                criteria("find_sculk_wastes", locationBiomeCriterion("sculky:sculk_wastes")),
                requirements("find_sculk_wastes")
        );
    }

    private static JsonObject findSculkJungle() {
        return advancement(
                "sculky:find_sculk_wastes",
                display(icon("sculky:infested_sculk_podzol_block", 1), trans("advancement.sculky.find_sculk_jungle.title"), trans("advancement.sculky.find_sculk_jungle.desc"), null, "task", true, true, false),
                criteria("find_sculk_jungle", locationBiomeCriterion("sculky:sculk_jungle")),
                requirements("find_sculk_jungle")
        );
    }

    private static JsonObject enterAllSculkBiomes() {
        return advancement(
                "sculky:find_sculk_jungle",
                display(icon("minecraft:echo_shard", 1), trans("advancement.sculky.enter_all_biomes.title"), trans("advancement.sculky.enter_all_biomes.desc"), null, "challenge", true, true, false),
                criteria(
                        "sculk_forest", directLocationBiomeCriterion("sculky:sculk_forest"),
                        "sculk_wastes", directLocationBiomeCriterion("sculky:sculk_wastes"),
                        "sculk_jungle", directLocationBiomeCriterion("sculky:sculk_jungle")
                ),
                requirements("sculk_forest", "sculk_wastes", "sculk_jungle")
        );
    }

    private static JsonObject findShrine() {
        return advancement(
                "sculky:root",
                display(icon("minecraft:sculk_sensor", 1), trans("advancement.sculky.find_shrine.title"), trans("advancement.sculky.find_shrine.desc"), null, "task", true, true, false),
                criteria("find_shrine", directLocationStructureCriterion("sculky:sculk_shrine")),
                requirements("find_shrine")
        );
    }

    private static JsonObject eatSculkApple() {
        return advancement(
                "sculky:find_shrine",
                display(icon("sculky:sculk_apple"), trans("advancement.sculky.eat_apple.title"), trans("advancement.sculky.eat_apple.desc"), null, "goal", true, true, false),
                criteria("eat_apple", consumeItemCriterion("sculky:sculk_apple")),
                requirements("eat_apple")
        );
    }

    private static JsonObject obtainSculkCore() {
        return advancement(
                "sculky:find_shrine",
                display(icon("sculky:sculk_core", 1), trans("advancement.sculky.obtain_sculk_core.title"), trans("advancement.sculky.obtain_sculk_core.desc"), null, "goal", true, true, false),
                criteria("obtain_core", inventoryCriterion("sculky:sculk_core")),
                requirements("obtain_core")
        );
    }

    private static JsonObject theCoreIsCalling() {
        return advancement(
                "sculky:obtain_sculk_core",
                display(icon("sculky:sculk_core", 1), trans("advancement.sculky.core_calling.title"), trans("advancement.sculky.core_calling.desc"), null, "challenge", true, true, false),
                criteria(
                        "has_find_shrine", criterion("minecraft:impossible"),
                        "has_core", inventoryCriterion("sculky:sculk_core"),
                        "entered_all_biomes", criterion("minecraft:impossible")
                ),
                requirements("has_core")
        );
    }

    private static JsonObject findAltar() {
        return advancement(
                "sculky:the_core_is_calling",
                display(icon("sculky:ancient_sculk_bricks", 1), trans("advancement.sculky.find_altar.title"), trans("advancement.sculky.find_altar.desc"), null, "task", true, true, false),
                criteria("find_altar", directLocationStructureCriterion("sculky:sculk_altar")),
                requirements("find_altar")
        );
    }

    private static JsonObject royalRitual() {
        return advancement(
                "sculky:find_altar",
                display(icon("sculky:royal_sculk_totem", 1), trans("advancement.sculky.royal_ritual.title"), trans("advancement.sculky.royal_ritual.desc"), null, "goal", true, true, false),
                criteria("has_ritual_items", inventoryCriterion("sculky:royal_sculk_totem", "sculky:sculk_core")),
                requirements("has_ritual_items")
        );
    }

    private static JsonObject falseServant() {
        JsonObject advancement = advancement(
                "sculky:royal_ritual",
                display(icon("minecraft:sculk_catalyst", 1), trans("advancement.sculky.false_servant.title"), trans("advancement.sculky.false_servant.desc"), null, "challenge", true, true, false),
                criteria("kill_sculk_evoker", killedEntityTypeCriterion("sculky:sculk_evoker")),
                requirements("kill_sculk_evoker")
        );

        JsonObject rewards = new JsonObject();
        rewards.addProperty("experience", 50);
        advancement.add("rewards", rewards);
        return advancement;
    }

    private static JsonObject fangOfTheThrone() {
        return advancement(
                "sculky:false_servant",
                display(icon("sculky:sculk_fang_scepter", 1), trans("advancement.sculky.fang_of_the_throne.title"), trans("advancement.sculky.fang_of_the_throne.desc"), null, "goal", true, true, false),
                criteria("obtain_sculk_fang_scepter", inventoryCriterion("sculky:sculk_fang_scepter")),
                requirements("obtain_sculk_fang_scepter")
        );
    }

    // ---------------------------------------------------------------------
    // Tab 2: Sculk Sanctum / King advancements
    // ---------------------------------------------------------------------

    private static JsonObject enterSanctum() {
        JsonObject conditions = new JsonObject();
        conditions.addProperty("to", "sculky:sculk_sanctum");

        return advancement(
                null,
                display(
                        icon("sculky:sculk_sanctum_grass_block", 1),
                        trans("advancement.sculky.enter_sanctum.title"),
                        trans("advancement.sculky.enter_sanctum.desc"),
                        "minecraft:textures/block/sculk.png",
                        "challenge",
                        true,
                        true,
                        false
                ),
                criteria("entered_sanctum", criterion("minecraft:changed_dimension", conditions)),
                requirements("entered_sanctum")
        );
    }

    private static JsonObject theKingWhispers() {
        return advancement(
                "sculky:enter_sanctum",
                display(icon("sculky:royal_sculk_fragment", 1), trans("advancement.sculky.the_king_whispers.title"), trans("advancement.sculky.the_king_whispers.desc"), null, "goal", true, true, false),
                criteria("obtain_royal_sculk_fragment", inventoryCriterion("sculky:royal_sculk_fragment")),
                requirements("obtain_royal_sculk_fragment")
        );
    }

    private static JsonObject beforeTheKing() {
        return advancement(
                "sculky:the_king_whispers",
                display(icon("sculky:royal_sculk_fragment", 1), trans("advancement.sculky.before_the_king.title"), trans("advancement.sculky.before_the_king.desc"), null, "goal", true, true, false),
                criteria("throne_not_ready", criterion("minecraft:impossible")),
                requirements("throne_not_ready")
        );
    }

    private static JsonObject buriedThrone() {
        return advancement(
                "sculky:the_king_whispers",
                display(icon("sculky:royal_sculk_crownstone"), text("The Buried Throne"), text("Find the arena where the Sculk King will awaken."), null, "task", true, true, false),
                criteria("find_buried_throne", locationCheckStructureCriterion("sculky:buried_throne")),
                requirements("find_buried_throne")
        );
    }

    private static JsonObject summonSculkKing() {
        return advancement(
                "sculky:the_buried_throne",
                display(icon("sculky:royal_sculk_fragment"), text("The King Awakens"), text("Complete the ritual at the Buried Throne."), null, "goal", true, true, false),
                criteria("summon_sculk_king", criterion("minecraft:impossible")),
                requirements("summon_sculk_king")
        );
    }

    private static JsonObject defeatSculkKing() {
        return advancement(
                "sculky:summon_sculk_king",
                display(icon("sculky:royal_sculk_fragment"), text("Regicide of the Deep"), text("Defeat the Sculk King."), null, "challenge", true, true, false),
                criteria("defeat_sculk_king", entityPropertiesKilledCriterion("sculky:sculk_king")),
                requirements("defeat_sculk_king")
        );
    }

    private static JsonObject obtainKingsAxe() {
        return advancement(
                "sculky:defeat_sculk_king",
                display(icon("sculky:kings_axe"), text("The Crown's Last Command"), text("Claim the weapon of the fallen Sculk King."), null, "goal", true, true, false),
                criteria("obtain_kings_axe", inventoryCriterion("sculky:kings_axe")),
                requirements("obtain_kings_axe")
        );
    }

    private static JsonObject tameSculkBear() {
        return advancement(
                "sculky:the_buried_throne",
                display(icon("sculky:sculk_bear_spawn_egg"), text("A Beast of the Deep"), text("Tame a Sculk Bear."), null, "goal", true, true, false),
                criteria("tame_sculk_bear", tameAnimalCriterion("sculky:sculk_bear")),
                requirements("tame_sculk_bear")
        );
    }

    // ---------------------------------------------------------------------
    // JSON helpers
    // ---------------------------------------------------------------------

    private static JsonObject advancement(String parent, JsonObject display, JsonObject criteria, JsonArray requirements) {
        JsonObject advancement = new JsonObject();

        if (parent != null) {
            advancement.addProperty("parent", parent);
        }

        advancement.add("display", display);
        advancement.add("criteria", criteria);
        advancement.add("requirements", requirements);

        // Keep generated advancements matching your newer manual files.
        advancement.addProperty("sends_telemetry_event", false);

        return advancement;
    }

    private static JsonObject display(JsonObject icon, JsonObject title, JsonObject description, String background, String frame, boolean showToast, boolean announceToChat, boolean hidden) {
        JsonObject display = new JsonObject();

        display.add("icon", icon);
        display.add("title", title);
        display.add("description", description);

        if (background != null) {
            display.addProperty("background", background);
        }

        display.addProperty("frame", frame);
        display.addProperty("show_toast", showToast);
        display.addProperty("announce_to_chat", announceToChat);
        display.addProperty("hidden", hidden);

        return display;
    }

    private static JsonObject icon(String id) {
        JsonObject icon = new JsonObject();
        icon.addProperty("id", id);
        return icon;
    }

    private static JsonObject icon(String id, int count) {
        JsonObject icon = icon(id);
        icon.addProperty("count", count);
        return icon;
    }

    private static JsonObject text(String text) {
        JsonObject component = new JsonObject();
        component.addProperty("text", text);
        return component;
    }

    private static JsonObject trans(String key) {
        JsonObject component = new JsonObject();
        component.addProperty("translate", key);
        return component;
    }

    private static JsonObject criteria(Object... entries) {
        JsonObject criteria = new JsonObject();

        for (int i = 0; i < entries.length; i += 2) {
            criteria.add((String) entries[i], (JsonElement) entries[i + 1]);
        }

        return criteria;
    }

    private static JsonArray requirements(String... names) {
        JsonArray requirements = new JsonArray();

        for (String name : names) {
            JsonArray requirement = new JsonArray();
            requirement.add(name);
            requirements.add(requirement);
        }

        return requirements;
    }

    private static JsonObject criterion(String trigger) {
        JsonObject criterion = new JsonObject();
        criterion.addProperty("trigger", trigger);
        return criterion;
    }

    private static JsonObject criterion(String trigger, JsonObject conditions) {
        JsonObject criterion = criterion(trigger);
        criterion.add("conditions", conditions);
        return criterion;
    }

    private static JsonObject locationBiomeCriterion(String biome) {
        JsonObject location = new JsonObject();
        location.addProperty("biomes", biome);

        JsonObject predicate = new JsonObject();
        predicate.add("location", location);

        JsonObject entityProperties = new JsonObject();
        entityProperties.addProperty("condition", "minecraft:entity_properties");
        entityProperties.addProperty("entity", "this");
        entityProperties.add("predicate", predicate);

        JsonArray player = new JsonArray();
        player.add(entityProperties);

        JsonObject conditions = new JsonObject();
        conditions.add("player", player);

        return criterion("minecraft:location", conditions);
    }

    private static JsonObject directLocationBiomeCriterion(String biome) {
        JsonObject location = new JsonObject();
        location.addProperty("biomes", biome);

        JsonObject player = new JsonObject();
        player.add("location", location);

        JsonObject conditions = new JsonObject();
        conditions.add("player", player);

        return criterion("minecraft:location", conditions);
    }

    private static JsonObject directLocationStructureCriterion(String structure) {
        JsonObject location = new JsonObject();
        location.addProperty("structures", structure);

        JsonObject player = new JsonObject();
        player.add("location", location);

        JsonObject conditions = new JsonObject();
        conditions.add("player", player);

        return criterion("minecraft:location", conditions);
    }

    private static JsonObject locationCheckStructureCriterion(String structure) {
        JsonObject predicate = new JsonObject();
        predicate.addProperty("structures", structure);

        JsonObject locationCheck = new JsonObject();
        locationCheck.addProperty("condition", "minecraft:location_check");
        locationCheck.add("predicate", predicate);

        JsonArray player = new JsonArray();
        player.add(locationCheck);

        JsonObject conditions = new JsonObject();
        conditions.add("player", player);

        return criterion("minecraft:location", conditions);
    }

    private static JsonObject consumeItemCriterion(String itemId) {
        JsonArray items = new JsonArray();
        items.add(itemId);

        JsonObject item = new JsonObject();
        item.add("items", items);

        JsonObject conditions = new JsonObject();
        conditions.add("item", item);

        return criterion("minecraft:consume_item", conditions);
    }

    private static JsonObject inventoryCriterion(String... itemIds) {
        JsonArray items = new JsonArray();

        for (String itemId : itemIds) {
            JsonArray itemList = new JsonArray();
            itemList.add(itemId);

            JsonObject item = new JsonObject();
            item.add("items", itemList);

            items.add(item);
        }

        JsonObject conditions = new JsonObject();
        conditions.add("items", items);

        return criterion("minecraft:inventory_changed", conditions);
    }

    private static JsonObject killedEntityTypeCriterion(String entityType) {
        JsonObject entity = new JsonObject();
        entity.addProperty("type", entityType);

        JsonObject conditions = new JsonObject();
        conditions.add("entity", entity);

        return criterion("minecraft:player_killed_entity", conditions);
    }

    private static JsonObject entityPropertiesKilledCriterion(String entityType) {
        JsonObject predicate = new JsonObject();
        predicate.addProperty("type", entityType);

        JsonObject entityProperties = new JsonObject();
        entityProperties.addProperty("condition", "minecraft:entity_properties");
        entityProperties.addProperty("entity", "this");
        entityProperties.add("predicate", predicate);

        JsonArray entity = new JsonArray();
        entity.add(entityProperties);

        JsonObject conditions = new JsonObject();
        conditions.add("entity", entity);

        return criterion("minecraft:player_killed_entity", conditions);
    }

    private static JsonObject tameAnimalCriterion(String entityType) {
        JsonObject predicate = new JsonObject();
        predicate.addProperty("type", entityType);

        JsonObject entityProperties = new JsonObject();
        entityProperties.addProperty("condition", "minecraft:entity_properties");
        entityProperties.addProperty("entity", "this");
        entityProperties.add("predicate", predicate);

        JsonArray entity = new JsonArray();
        entity.add(entityProperties);

        JsonObject conditions = new JsonObject();
        conditions.add("entity", entity);

        return criterion("minecraft:tame_animal", conditions);
    }
}
