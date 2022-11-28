package fade.concauldron;

import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
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
                .forEach(this::transmuteItem);
    }

    /**
     * Maps the concrete powder materials to the respective non-powdered versions
     *
     * @return null, if provided material is not powdered concrete
     */
    private @Nullable ConcreteData convertConcreteMaterial(@NotNull Material material) {
        return switch (material) {
            case WHITE_CONCRETE_POWDER -> new ConcreteData(Material.WHITE_CONCRETE, DyeColor.WHITE);
            case ORANGE_CONCRETE_POWDER -> new ConcreteData(Material.ORANGE_CONCRETE, DyeColor.ORANGE);
            case MAGENTA_CONCRETE_POWDER -> new ConcreteData(Material.MAGENTA_CONCRETE, DyeColor.MAGENTA);
            case LIGHT_BLUE_CONCRETE_POWDER -> new ConcreteData(Material.LIGHT_BLUE_CONCRETE, DyeColor.LIGHT_BLUE);
            case YELLOW_CONCRETE_POWDER -> new ConcreteData(Material.YELLOW_CONCRETE, DyeColor.YELLOW);
            case LIME_CONCRETE_POWDER -> new ConcreteData(Material.LIME_CONCRETE, DyeColor.LIME);
            case PINK_CONCRETE_POWDER -> new ConcreteData(Material.PINK_CONCRETE, DyeColor.PINK);
            case GRAY_CONCRETE_POWDER -> new ConcreteData(Material.GRAY_CONCRETE, DyeColor.GRAY);
            case LIGHT_GRAY_CONCRETE_POWDER -> new ConcreteData(Material.LIGHT_GRAY_CONCRETE, DyeColor.LIGHT_GRAY);
            case CYAN_CONCRETE_POWDER -> new ConcreteData(Material.CYAN_CONCRETE, DyeColor.CYAN);
            case PURPLE_CONCRETE_POWDER -> new ConcreteData(Material.PURPLE_CONCRETE, DyeColor.PURPLE);
            case BLUE_CONCRETE_POWDER -> new ConcreteData(Material.BLUE_CONCRETE, DyeColor.BLUE);
            case BROWN_CONCRETE_POWDER -> new ConcreteData(Material.BROWN_CONCRETE, DyeColor.BROWN);
            case GREEN_CONCRETE_POWDER -> new ConcreteData(Material.GREEN_CONCRETE, DyeColor.GREEN);
            case RED_CONCRETE_POWDER -> new ConcreteData(Material.RED_CONCRETE, DyeColor.RED);
            case BLACK_CONCRETE_POWDER -> new ConcreteData(Material.BLACK_CONCRETE, DyeColor.BLACK);
            default -> null;
        };
    }

    private void transmuteItem(@NotNull Item item) {
        ItemStack itemStack = item.getItemStack();

        Material itemStackType = itemStack.getType();
        ConcreteData concreteData = this.convertConcreteMaterial(itemStackType);
        if (concreteData == null) return;

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

        this.createParticleEffect(itemLocation, concreteData);

        itemStack.setType(concreteData.material());
        item.setItemStack(itemStack);

    }

    private void createParticleEffect(@NotNull Location location, @NotNull ConcreteData concreteData) {
        final double offset = 0.55d;

        Particle.REDSTONE.builder()
                .count(50)
                .offset(offset, offset, offset)
                .color(concreteData.color().getColor())
                .location(location)
                .spawn();
    }
}
