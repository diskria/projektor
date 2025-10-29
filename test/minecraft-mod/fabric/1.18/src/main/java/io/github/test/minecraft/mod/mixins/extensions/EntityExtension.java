package io.github.test.minecraft.mod.mixins.extensions;

import io.github.test.minecraft.mod.common.EntityContract;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(Entity.class)
class EntityExtension implements EntityContract {

    @Override
    @Unique
    public boolean mod_isPlayer() {
        Entity entity = (Entity) (Object) this;
        return entity instanceof PlayerEntity;
    }
}
