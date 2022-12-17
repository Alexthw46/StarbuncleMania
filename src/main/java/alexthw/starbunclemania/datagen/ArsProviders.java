package alexthw.starbunclemania.datagen;

import alexthw.starbunclemania.ArsNouveauRegistry;
import alexthw.starbunclemania.StarbuncleMania;
import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.ArsNouveauAPI;
import com.hollingsworth.arsnouveau.api.enchanting_apparatus.EnchantingApparatusRecipe;
import com.hollingsworth.arsnouveau.api.familiar.AbstractFamiliarHolder;
import com.hollingsworth.arsnouveau.api.spell.AbstractCastMethod;
import com.hollingsworth.arsnouveau.api.spell.AbstractEffect;
import com.hollingsworth.arsnouveau.api.spell.AbstractSpellPart;
import com.hollingsworth.arsnouveau.common.crafting.recipes.GlyphRecipe;
import com.hollingsworth.arsnouveau.common.crafting.recipes.ImbuementRecipe;
import com.hollingsworth.arsnouveau.common.datagen.*;
import com.hollingsworth.arsnouveau.common.datagen.patchouli.*;
import com.hollingsworth.arsnouveau.common.lib.LibEntityNames;
import com.hollingsworth.arsnouveau.setup.APIRegistry;
import com.hollingsworth.arsnouveau.setup.ItemsRegistry;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.common.Tags;

import java.io.IOException;
import java.nio.file.Path;

import static alexthw.starbunclemania.registry.ModRegistry.*;
import static com.hollingsworth.arsnouveau.api.RegistryHelper.getRegistryName;

public class ArsProviders {

    static final String root = StarbuncleMania.MODID;

    public static class GlyphProvider extends GlyphRecipeProvider {

        public GlyphProvider(DataGenerator generatorIn) {
            super(generatorIn);
        }

        @Override
        public void run(CachedOutput cache) throws IOException {

            Path output = this.generator.getOutputFolder();

            for (GlyphRecipe recipe : recipes) {
                Path path = getScribeGlyphPath(output, recipe.output.getItem());
                DataProvider.saveStable(cache, recipe.asRecipe(), path);
            }

        }

        protected static Path getScribeGlyphPath(Path pathIn, Item glyph) {
            return pathIn.resolve("data/" + root + "/recipes/" + getRegistryName(glyph).getPath() + ".json");
        }

        @Override
        public String getName() {
            return "Starbunclemania Glyph Recipes";
        }
    }

    public static class EnchantingAppProvider extends ApparatusRecipeProvider {

        public EnchantingAppProvider(DataGenerator generatorIn) {
            super(generatorIn);
        }

        @Override
        public void run(CachedOutput cache) throws IOException {

            recipes.add(builder().withReagent(Items.BOOK)
                    .withResult(ArsNouveauAPI.getInstance().getFamiliarItem(new ResourceLocation(ArsNouveau.MODID, LibEntityNames.FAMILIAR_BOOKWYRM)))
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

            Path output = this.generator.getOutputFolder();
            for (EnchantingApparatusRecipe g : recipes) {
                if (g != null) {
                    Path path = getRecipePath(output, g.getId().getPath());
                    DataProvider.saveStable(cache, g.asRecipe(), path);
                }
            }

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
        public void run(CachedOutput cache) throws IOException {

            Path output = generator.getOutputFolder();
            for (ImbuementRecipe g : recipes) {
                Path path = getRecipePath(output, g.getId().getPath());
                DataProvider.saveStable(cache, g.asRecipe(), path);
            }

        }

        protected Path getRecipePath(Path pathIn, String str) {
            return pathIn.resolve("data/" + root + "/recipes/" + str + ".json");
        }

        @Override
        public String getName() {
            return "Starbunclemania Imbuement";
        }

    }

    public static class StarPatchouliProvider extends PatchouliProvider {

        public StarPatchouliProvider(DataGenerator generatorIn) {
            super(generatorIn);
        }

        @Override
        public void run(CachedOutput cache) throws IOException {

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
                    ,getPath(FAMILIARS, "cosmetic"));
            addBasicItem(PROFHAT.get(), AUTOMATION, new CraftingPage(PROFHAT.get()));
            addBasicItem(STARBUCKET.get(), AUTOMATION, new ApparatusPage(STARBUCKET.get()));
            addBasicItem(STARBALLON.get(), AUTOMATION, new ApparatusPage(STARBALLON.get()));
            addBasicItem(STARTRASH.get(), AUTOMATION, new CraftingPage(STARTRASH.get()));
            addBasicItem(STARBATTERY.get(), AUTOMATION, new CraftingPage(STARBATTERY.get()));
            addBasicItem(STARSADDLE.get(), AUTOMATION, new ApparatusPage(STARSADDLE.get()));

            for (PatchouliPage patchouliPage : pages) {
                DataProvider.saveStable(cache, patchouliPage.build(), patchouliPage.path());
            }

        }

        @Override
        public void addBasicItem(ItemLike item, ResourceLocation category, IPatchouliPage recipePage) {
            PatchouliBuilder builder = new PatchouliBuilder(category, item.asItem().getDescriptionId())
                    .withIcon(item.asItem())
                    .withPage(new TextPage(root + ".page." + getRegistryName(item.asItem()).getPath()))
                    .withPage(recipePage);
            this.pages.add(new PatchouliPage(builder, getPath(category, getRegistryName(item.asItem()).getPath())));
        }

        public void addFamiliarPage(AbstractFamiliarHolder familiarHolder) {
            PatchouliBuilder builder = new PatchouliBuilder(FAMILIARS, "entity." + root + "." + familiarHolder.getRegistryName().getPath())
                    .withIcon(root + ":" + familiarHolder.getRegistryName().getPath())
                    .withTextPage(root + ".familiar_desc." + familiarHolder.getRegistryName().getPath())
                    .withPage(new EntityPage(familiarHolder.getRegistryName().toString()));
            this.pages.add(new PatchouliPage(builder, getPath(FAMILIARS, familiarHolder.getRegistryName().getPath())));
        }

        public void addGlyphPage(AbstractSpellPart spellPart) {
            ResourceLocation category = switch (spellPart.getTier().value) {
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

        @Override
        public Path getPath(ResourceLocation category, String fileName) {
            return this.generator.getOutputFolder().resolve("data/" + root + "/patchouli_books/starbunclemania/en_us/entries/" + category.getPath() + "/" + fileName + ".json");
        }

        ImbuementPage ImbuementPage(ItemLike item) {
            return new ImbuementPage(root + ":imbuement_" + getRegistryName(item.asItem()).getPath());
        }

    }

}
