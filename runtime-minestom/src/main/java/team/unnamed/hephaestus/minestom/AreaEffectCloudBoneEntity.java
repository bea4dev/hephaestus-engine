/*
 * This file is part of hephaestus-engine, licensed under the MIT license
 *
 * Copyright (c) 2021-2023 Unnamed Team
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package team.unnamed.hephaestus.minestom;

import net.kyori.adventure.text.Component;
import net.minestom.server.color.Color;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.EntityCreature;
import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.Player;
import net.minestom.server.entity.metadata.other.AreaEffectCloudMeta;
import net.minestom.server.entity.metadata.other.ArmorStandMeta;
import net.minestom.server.instance.Instance;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import net.minestom.server.item.metadata.LeatherArmorMeta;
import net.minestom.server.network.packet.server.play.SetPassengersPacket;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import team.unnamed.creative.base.Vector3Float;
import team.unnamed.hephaestus.Bone;
import team.unnamed.hephaestus.Minecraft;

import java.util.Collections;
import java.util.concurrent.CompletableFuture;

/**
 * Represents a {@link Bone} holder entity,
 * it is an armor stand with a LEATHER_HORSE_ARMOR
 * item as helmet using a custom model data to
 * apply the bone model
 *
 * @since 1.0.0
 */
final class AreaEffectCloudBoneEntity extends GenericBoneEntity {

    private static final ItemStack BASE_HELMET =
            ItemStack.builder(Material.LEATHER_HORSE_ARMOR)
                    .meta(new LeatherArmorMeta.Builder()
                            .color(new Color(0xFFFFFF))
                            .build())
                    .build();

    private final ModelEntity view;
    private final Bone bone;

    private final EntityCreature armorstand;

    // cached height offset, either SMALL_OFFSET
    // or LARGE_OFFSET
    private final float offset;

    public AreaEffectCloudBoneEntity(
            ModelEntity view,
            Bone bone
    ) {
        super(EntityType.AREA_EFFECT_CLOUD);
        this.view = view;
        this.bone = bone;
        this.offset = bone.small()
                ? Minecraft.ARMOR_STAND_SMALL_VERTICAL_OFFSET
                : Minecraft.ARMOR_STAND_DEFAULT_VERTICAL_OFFSET;

        this.armorstand = new EntityCreature(EntityType.ARMOR_STAND);
        initialize();
    }

    private void initialize() {
        setNoGravity(true);
        setInvulnerable(true);
        setSilent(true);
        setInvulnerable(true);

        AreaEffectCloudMeta cloudMeta = (AreaEffectCloudMeta) getEntityMeta();
        cloudMeta.setRadius(0);

        ArmorStandMeta meta = (ArmorStandMeta) armorstand.getEntityMeta();
        meta.setSilent(true);
        meta.setHasNoGravity(true);
        meta.setSmall(bone.small());
        meta.setInvisible(true);

        // set helmet with custom model data from our bone
        armorstand.setHelmet(BASE_HELMET.withMeta(itemMeta ->
                itemMeta.customModelData(bone.customModelData())));

        armorstand.setAutoViewable(false);
    }

    /**
     * Returns the holder view
     *
     * @return The view for this bone entity
     */
    public ModelEntity view() {
        return view;
    }

    @Override
    public Bone bone() {
        return bone;
    }

    @Override
    public void customName(Component displayName) {
        super.setCustomName(displayName);
    }

    @Override
    public Component customName() {
        return super.getCustomName();
    }

    @Override
    public void customNameVisible(boolean visible) {
        super.setCustomNameVisible(visible);
    }

    @Override
    public boolean customNameVisible() {
        return super.isCustomNameVisible();
    }

    /**
     * Colorizes this bone entity using
     * the specified color
     *
     * @param color The new bone color
     */
    @Override
    public void colorize(Color color) {
        armorstand.setHelmet(armorstand.getHelmet().withMeta(LeatherArmorMeta.class, meta -> meta.color(color)));
    }

    @Override
    public void colorize(int r, int g, int b) {
        colorize(new Color(r, g, b));
    }

    @Override
    public void colorize(int rgb) {
        colorize(new Color(rgb));
    }

    @Override
    public void position(Vector3Float position) {
        teleport(view.getPosition().add(
                position.x(),
                position.y() - offset,
                position.z()
        ));
    }

    @Override
    public void rotation(Vector3Float rotation) {
        ArmorStandMeta meta = (ArmorStandMeta) armorstand.getEntityMeta();
        meta.setHeadRotation(new Vec(
                rotation.x(),
                rotation.y(),
                rotation.z()
        ));
    }

    @Override
    public @NotNull CompletableFuture<Void> teleport(@NotNull Pos position, long @Nullable [] chunks) {
        this.armorstand.teleport(position, chunks);
        return super.teleport(position.sub(0, offset, 0), chunks);
    }

    @Override
    public CompletableFuture<Void> setInstance(@NotNull Instance instance, @NotNull Pos pos) {
        return super.setInstance(instance, pos.sub(0, offset, 0)).thenAccept(ignored -> {
            armorstand.setInstance(instance, pos).join();
        });
    }

    @Override
    public void updateNewViewer(@NotNull Player player) {
        super.updateNewViewer(player);
        this.scheduleNextTick(entity -> player.sendPacket(
                new SetPassengersPacket(getEntityId(), Collections.singletonList(this.armorstand.getEntityId()))
        ));
    }

    @Override
    public void setAutoViewable(boolean autoViewable) {
        super.setAutoViewable(autoViewable);
        if (this.armorstand != null) {
            this.armorstand.setAutoViewable(autoViewable);
        }
    }

    @Override
    public void kill() {
        this.armorstand.kill();
        super.kill();
    }
}
