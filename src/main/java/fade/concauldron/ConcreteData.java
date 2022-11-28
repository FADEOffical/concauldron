package fade.concauldron;

import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

public record ConcreteData(@NotNull Material material, @NotNull DyeColor color) {}
