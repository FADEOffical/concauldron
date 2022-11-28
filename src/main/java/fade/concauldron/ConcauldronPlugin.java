package fade.concauldron;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Levelled;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.BoundingBox;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class ConcauldronPlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        this.getServer().getScheduler().scheduleSyncRepeatingTask(this, this::concauldronRunnable, 1L, 5L);
    }

    private void concauldronRunnable() {
        this.getServer()
                .getWorlds()
                .stream()
                .flatMap(world -> world.getEntities().stream())
                .filter(Item.class::isInstance)
                .map(Item.class::cast)
                .forEach(item -> {
                    ItemStack itemStack = item.getItemStack();

                    Material itemStackType = itemStack.getType();
                    Material convertedConcreteMaterial = this.convertConcreteMaterial(itemStackType);
                    if (convertedConcreteMaterial == null) return;

                    Location itemLocation = item.getLocation();
                    Block block = itemLocation.getBlock();

                    // check if item is in a water cauldron
                    Material blockMaterial = block.getType();
                    if (blockMaterial != Material.WATER_CAULDRON) return;

                    // check if cauldron is in fact a cauldron (levelled block)
                    // this check is _technically_ useless, but it's in here, so I don't have
                    // to fuck around with casts much more than this
                    BlockData blockData = block.getBlockData();
                    if (!(blockData instanceof Levelled levelledData)) return;

                    // check if cauldron is full
                    if (levelledData.getLevel() != levelledData.getMaximumLevel()) return;

                    // check if the item is inside the cauldron
                    // todo: test whether this check is even necessary
                    Location blockLocation = itemLocation.toBlockLocation();
                    BoundingBox boundingBox = BoundingBox.of(blockLocation.getBlock());
                    if (!boundingBox.contains(item.getBoundingBox())) return;

                    itemStack.setType(convertedConcreteMaterial);
                    item.setItemStack(itemStack);
                });
    }

    /**
     * Maps the concrete powder materials to the respective non-powdered versions
     *
     * @return null, if provided material is not powdered concrete
     */
    private @Nullable Material convertConcreteMaterial(@NotNull Material material) {
        return switch (material) {
            case WHITE_CONCRETE_POWDER -> Material.WHITE_CONCRETE;
            case ORANGE_CONCRETE_POWDER -> Material.ORANGE_CONCRETE;
            case MAGENTA_CONCRETE_POWDER -> Material.MAGENTA_CONCRETE;
            case LIGHT_BLUE_CONCRETE_POWDER -> Material.LIGHT_BLUE_CONCRETE;
            case YELLOW_CONCRETE_POWDER -> Material.YELLOW_CONCRETE;
            case LIME_CONCRETE_POWDER -> Material.LIME_CONCRETE;
            case PINK_CONCRETE_POWDER -> Material.PINK_CONCRETE;
            case GRAY_CONCRETE_POWDER -> Material.GRAY_CONCRETE;
            case LIGHT_GRAY_CONCRETE_POWDER -> Material.LIGHT_GRAY_CONCRETE;
            case CYAN_CONCRETE_POWDER -> Material.CYAN_CONCRETE;
            case PURPLE_CONCRETE_POWDER -> Material.PURPLE_CONCRETE;
            case BLUE_CONCRETE_POWDER -> Material.BLUE_CONCRETE;
            case BROWN_CONCRETE_POWDER -> Material.BROWN_CONCRETE;
            case GREEN_CONCRETE_POWDER -> Material.GREEN_CONCRETE;
            case RED_CONCRETE_POWDER -> Material.RED_CONCRETE;
            case BLACK_CONCRETE_POWDER -> Material.BLACK_CONCRETE;
            default -> null;
        };
    }
}
