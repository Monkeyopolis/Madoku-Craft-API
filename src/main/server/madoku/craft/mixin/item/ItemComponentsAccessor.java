package madoku.craft.mixin.item;

import net.minecraft.core.component.DataComponentMap;
import net.minecraft.world.item.Item;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Item.class)
public interface ItemComponentsAccessor {
	@Mutable
	@Accessor("components")
	void madokuCraft$bindComponents(DataComponentMap components);
}

