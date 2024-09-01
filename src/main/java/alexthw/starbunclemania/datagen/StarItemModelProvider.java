package alexthw.starbunclemania.datagen;

import alexthw.starbunclemania.StarbuncleMania;
import alexthw.starbunclemania.registry.ModRegistry;
import net.minecraft.core.Direction;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.client.model.generators.ItemModelBuilder;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.registries.DeferredHolder;

import static alexthw.starbunclemania.StarbuncleMania.prefix;

public class StarItemModelProvider extends ItemModelProvider {
    public StarItemModelProvider(DataGenerator generator, ExistingFileHelper existingFileHelper) {
        super(generator.getPackOutput(), StarbuncleMania.MODID, existingFileHelper);
    }

    private static final ResourceLocation GENERATED = ResourceLocation.withDefaultNamespace("item/generated");

    @Override
    protected void registerModels() {
        directionScroll();
        generatedItem(ModRegistry.FLUID_SCROLL_D);
        generatedItem(ModRegistry.FLUID_SCROLL_A);
    }

    private void directionScroll() {

        String path = ModRegistry.DIRECTION_SCROLL.getId().getPath();
        withExistingParent(path, GENERATED).texture("layer0", modLoc("item/blank_parchment"));

        ItemModelBuilder builder = getBuilder(path);

        for (Direction side : Direction.values()) {
            builder.override().predicate(prefix("side"), side.ordinal()).model(
                    singleTexture("item/ds/" + path + '_' + side.ordinal(), GENERATED, "layer0", modLoc("item/ds/" + path + '_' + side))
            ).end();
        }
    }


    private void generatedItem(DeferredHolder<Item,Item> i) {
        String name = i.getId().getPath();
        withExistingParent(name, GENERATED).texture("layer0", prefix("item/" + name));
    }

    private void blockGeneratedItem(DeferredHolder<Item,Item> i) {
        String name = i.getId().getPath();
        withExistingParent(name, GENERATED).texture("layer0", prefix("block/" + name));
    }

    private void blockItem(DeferredHolder<Item,Item> i) {
        String name = i.getId().getPath();
        getBuilder(name).parent(new ModelFile.UncheckedModelFile(prefix("block/" + name)));
    }
}
