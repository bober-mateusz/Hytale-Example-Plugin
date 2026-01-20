package com.example.exampleplugin;

import com.hypixel.hytale.builtin.portals.components.voidevent.config.InvasionPortalConfig;
import com.hypixel.hytale.component.*;
import com.hypixel.hytale.component.dependency.Dependency;
import com.hypixel.hytale.component.dependency.RootDependency;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.EcsEvent;
import com.hypixel.hytale.component.system.EntityEventSystem;
import com.hypixel.hytale.math.vector.Vector3f;
import com.hypixel.hytale.protocol.GameMode;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.event.events.ecs.BreakBlockEvent;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.modules.entity.item.ItemComponent;
import com.hypixel.hytale.server.core.modules.interaction.BlockHarvestUtils;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class BreakOreEventSystem extends EntityEventSystem<EntityStore, BreakBlockEvent> {
    protected BreakOreEventSystem() {
        super(BreakBlockEvent.class);
    }

    @Override
    public void handle(int index, @NotNull ArchetypeChunk<EntityStore> archetypeChunk, @NotNull Store<EntityStore> store, @NotNull CommandBuffer<EntityStore> commandBuffer, @NotNull BreakBlockEvent breakBlockEvent) {
        Ref<EntityStore> ref = archetypeChunk.getReferenceTo(index);
        Player player = store.getComponent(ref, Player.getComponentType());
        var blockPosition = breakBlockEvent.getTargetBlock();
        var blockType = breakBlockEvent.getBlockType();

        if (player == null) return;
        if(!blockType.getId().contains("Ore")) {
            return;
        }
        player.sendMessage(Message.raw("You Broke Ore: " + blockType.getId()));
        ItemStack coalItem = new ItemStack("Ingredient_Charcoal", 2);
        var itemEntityHolder = ItemComponent.generateItemDrop(store,coalItem,blockPosition.toVector3d().add(.5, .5, .5),Vector3f.ZERO, 0, 0, 0);
        Objects.requireNonNull(player.getWorld()).execute(() -> {
            assert itemEntityHolder != null;
            player.getWorld().getEntityStore().getStore().addEntity(itemEntityHolder, AddReason.SPAWN);
        });
    }

    @Override
    public @Nullable Query<EntityStore> getQuery() {
        return PlayerRef.getComponentType();
    }

    @NonNullDecl
    @Override
    public Set<Dependency<EntityStore>> getDependencies() {
        return Collections.singleton(RootDependency.first());
    }
}
