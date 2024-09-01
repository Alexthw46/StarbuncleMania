package alexthw.starbunclemania.datagen;

import alexthw.starbunclemania.ArsNouveauRegistry;
import alexthw.starbunclemania.StarbuncleMania;
import alexthw.starbunclemania.glyph.PickupFluidEffect;
import alexthw.starbunclemania.glyph.PlaceFluidEffect;
import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.familiar.AbstractFamiliarHolder;
import com.hollingsworth.arsnouveau.api.registry.FamiliarRegistry;
import com.hollingsworth.arsnouveau.api.spell.AbstractCastMethod;
import com.hollingsworth.arsnouveau.api.spell.AbstractEffect;
import com.hollingsworth.arsnouveau.api.spell.AbstractSpellPart;
import com.hollingsworth.arsnouveau.common.crafting.recipes.GlyphRecipe;
import com.hollingsworth.arsnouveau.common.datagen.*;
import com.hollingsworth.arsnouveau.common.datagen.patchouli.*;
import com.hollingsworth.arsnouveau.common.lib.LibEntityNames;
import com.hollingsworth.arsnouveau.setup.registry.ItemsRegistry;
import com.mojang.serialization.JsonOps;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.common.Tags;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;

import static alexthw.starbunclemania.registry.ModRegistry.*;
import static com.hollingsworth.arsnouveau.setup.registry.RegistryHelper.getRegistryName;

public class ArsProviders {

    static final String root = StarbuncleMania.MODID;

    public static class GlyphProvider extends GlyphRecipeProvider {

        public GlyphProvider(DataGenerator generatorIn) {
            super(generatorIn);
        }

        @Override
        public void collectJsons(CachedOutput cache) {

            add(get(PickupFluidEffect.INSTANCE).withItem(FLUID_JAR.get()).withItem(Items.HOPPER));
            add(get(PlaceFluidEffect.INSTANCE).withItem(ItemsRegistry.WATER_ESSENCE).withItem(Items.DISPENSER));

            Path output = this.generator.getPackOutput().getOutputFolder();
            for (GlyphRecipe recipe : recipes) {
                Path path = getScribeGlyphPath(output, recipe.output.getItem());
                saveStable(cache, GlyphRecipe.CODEC.encodeStart(JsonOps.INSTANCE, recipe).getOrThrow(), path);
            }

        }

        protected static Path getScribeGlyphPath(Path pathIn, Item glyph) {
            return pathIn.resolve("data/" + root + "/recipes/" + getRegistryName(glyph).getPath() + ".json");
        }

        @Override
        public @NotNull String getName() {
            return "Starbunclemania Glyph Recipes";
        }
    }

    public static class EnchantingAppProvider extends ApparatusRecipeProvider {

        public EnchantingAppProvider(DataGenerator generatorIn) {
            super(generatorIn);
        }

        @Override
        public void addEntries() {

            recipes.add(builder().withReagent(Items.BOOK)
                    .withResult(FamiliarRegistry.getFamiliarScriptMap().get(ResourceLocation.fromNamespaceAndPath(ArsNouveau.MODID, LibEntityNames.FAMILIAR_BOOKWYRM)))
                    .withPedestalItem(3, Items.IRON_INGOT)
                    .withPedestalItem(3, ItemTagProvider.SOURCE_GEM_TAG)
                    .build()
            );

            recipes.add(builder()
                    .withReagent(FLUID_JAR.get())
                    .withPedestalItem(4, Tags.Items.NUGGETS_GOLD)
                    .withResult(STARBUCKET.get())
                    .build()
            );

            recipes.add(builder()
                    .withReagent(Ingredient.of(ItemTags.WOOL))
                    .withPedestalItem(ItemsRegistry.AIR_ESSENCE)
                    .withPedestalItem(2, ItemsRegistry.MAGE_FIBER)
                    .withResult(STARBALLON.get())
                    .build()
            );

            recipes.add(builder()
                    .withReagent(Items.SADDLE)
                    .withPedestalItem(2, Ingredient.of(ItemTagProvider.SOURCE_GEM_TAG))
                    .withPedestalItem(2, Ingredient.of(Tags.Items.NUGGETS_GOLD))
                    .withResult(STARSADDLE.get())
                    .build()
            );

        }

        protected static Path getRecipePath(Path pathIn, String str) {
            return pathIn.resolve("data/" + root + "/recipes/" + str + ".json");
        }

        @Override
        public String getName() {
            return "Starbunclemania Apparatus";
        }
    }

    public static class ImbuementProvider extends ImbuementRecipeProvider {

        public ImbuementProvider(DataGenerator generatorIn) {
            super(generatorIn);
        }

        @Override
        public void collectJsons(CachedOutput cache) {
        }

        protected Path getRecipePath(Path pathIn, String str) {
            return pathIn.resolve("data/" + root + "/recipes/" + str + ".json");
        }

        @Override
        public @NotNull String getName() {
            return "Starbunclemania Imbuement";
        }

    }

    public static class StarPatchouliProvider extends PatchouliProvider {

        public StarPatchouliProvider(DataGenerator generatorIn) {
            super(generatorIn);
        }

        @Override
        public void collectJsons(CachedOutput cache) {

            for (AbstractSpellPart spell : ArsNouveauRegistry.registeredSpells) {
                addGlyphPage(spell);
            }

            addBasicItem(FLUID_JAR.get(), AUTOMATION, new CraftingPage(FLUID_JAR.get()));
            addBasicItem(SOURCE_CONDENSER.get(), SOURCE, new CraftingPage(SOURCE_CONDENSER.get()));
            addBasicItem(FLUID_SOURCELINK.get(), SOURCE, new CraftingPage(FLUID_SOURCELINK.get()));

            addBasicItem(DIRECTION_SCROLL.get(), AUTOMATION, new CraftingPage(DIRECTION_SCROLL.get()));

            addPage(new PatchouliBuilder(FAMILIARS, STARHAT.get()).withName("starbunclemania.cosmetic")
                            .withTextPage("starbunclemania.page.star_hat")
                            .withPage(new CraftingPage(STARHAT.get()))
                    , getPath(FAMILIARS, "cosmetic"));
            addBasicItem(PROFHAT.get(), AUTOMATION, new CraftingPage(PROFHAT.get()));
            addPage(new PatchouliBuilder(AUTOMATION, STARBUCKET.get())
                            .withTextPage("starbunclemania.page.star_bucket")
                            .withPage(new ApparatusPage(STARBUCKET.get()))
                            .withTextPage("starbunclemania.page.fluid_scroll")
                            .withPage(new CraftingPage(FLUID_SCROLL_A.get())
                                    .withRecipe2(FLUID_SCROLL_D.get()))
                    , getPath(AUTOMATION, "star_bucket"));
            addBasicItem(STARBALLON.get(), AUTOMATION, new ApparatusPage(STARBALLON.get()));
            addBasicItem(STARTRASH.get(), AUTOMATION, new CraftingPage(STARTRASH.get()));
            addBasicItem(STARBATTERY.get(), AUTOMATION, new CraftingPage(STARBATTERY.get()));
            addBasicItem(STARSADDLE.get(), AUTOMATION, new ApparatusPage(STARSADDLE.get()));

            addPage(new PatchouliBuilder(AUTOMATION, ItemsRegistry.WIXIE_CHARM.get()).withName("starbunclemania.wixie_jobs")
                            .withTextPage("starbunclemania.page.wixie_cook")
                            .withTextPage("starbunclemania.page.wixie_cut")
                    , getPath(AUTOMATION, "wixie_jobs"));

            for (PatchouliPage patchouliPage : pages) {
                saveStable(cache, patchouliPage.build(), patchouliPage.path());
            }

        }

        @Override
        public PatchouliPage addBasicItem(ItemLike item, ResourceLocation category, IPatchouliPage recipePage) {
            PatchouliBuilder builder = new PatchouliBuilder(category, item.asItem().getDescriptionId())
                    .withIcon(item.asItem())
                    .withPage(new TextPage(root + ".page." + getRegistryName(item.asItem()).getPath()))
                    .withPage(recipePage);
            var page = new PatchouliPage(builder, getPath(category, getRegistryName(item.asItem()).getPath()));
            this.pages.add(page);
            return page;
        }

        public void addFamiliarPage(AbstractFamiliarHolder familiarHolder) {
            PatchouliBuilder builder = new PatchouliBuilder(FAMILIARS, "entity." + root + "." + familiarHolder.getRegistryName().getPath())
                    .withIcon(root + ":" + familiarHolder.getRegistryName().getPath())
                    .withTextPage(root + ".familiar_desc." + familiarHolder.getRegistryName().getPath())
                    .withPage(new EntityPage(familiarHolder.getRegistryName().toString()));
            this.pages.add(new PatchouliPage(builder, getPath(FAMILIARS, familiarHolder.getRegistryName().getPath())));
        }

        public void addGlyphPage(AbstractSpellPart spellPart) {
            ResourceLocation category = switch (spellPart.defaultTier().value) {
                case 1 -> GLYPHS_1;
                case 2 -> GLYPHS_2;
                default -> GLYPHS_3;
            };
            PatchouliBuilder builder = new PatchouliBuilder(category, spellPart.getName())
                    .withName(root + ".glyph_name." + spellPart.getRegistryName().getPath())
                    .withIcon(spellPart.getRegistryName().toString())
                    .withSortNum(spellPart instanceof AbstractCastMethod ? 1 : spellPart instanceof AbstractEffect ? 2 : 3)
                    .withPage(new TextPage(root + ".glyph_desc." + spellPart.getRegistryName().getPath()))
                    .withPage(new GlyphScribePage(spellPart));
            this.pages.add(new PatchouliPage(builder, getPath(category, spellPart.getRegistryName().getPath())));
        }

        /**
         * Gets a name for this provider, to use in logging.
         */
        @Override
        public String getName() {
            return "StarbuncleMania Patchouli Datagen";
        }

        ImbuementPage ImbuementPage(ItemLike item) {
            return new ImbuementPage(root + ":imbuement_" + getRegistryName(item.asItem()).getPath());
        }

    }

}
