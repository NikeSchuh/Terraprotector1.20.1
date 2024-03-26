package de.nike.terraprotector.client.screens;

import net.minecraft.advancements.FrameType;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

public class RivenSlotInfo {

    private ItemStack item;
    private Component title;
    private Component lore;
    private float x;
    private float y;
    private FrameType frameType;
    private RivenSlotInfo parent;
    private boolean hidden = false;
    private final boolean isSlot;
    private final int slotIndex;

    public RivenSlotInfo(ItemStack item, Component title, Component lore, float x, float y, FrameType frameType, RivenSlotInfo parent, boolean isSlot, int slotIndex) {
        this.item = item;
        this.title = title;
        this.lore = lore;
        this.x = x;
        this.y = y;
        this.frameType = frameType;
        this.parent = parent;
        this.isSlot = isSlot;
        this.slotIndex = slotIndex;
    }

    public boolean isSlot() {
        return isSlot;
    }

    public int getSlotIndex() {
        return slotIndex;
    }

    public boolean isHidden() {
        return hidden;
    }

    public void setHidden(boolean hidden) {
        this.hidden = hidden;
    }

    public RivenSlotInfo getParent() {
        return parent;
    }

    public void setParent(RivenSlotInfo parent) {
        this.parent = parent;
    }

    public FrameType getFrameType() {
        return frameType;
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public ItemStack getItem() {
        return item;
    }

    public void setItem(ItemStack item) {
        this.item = item;
    }

    public Component getTitle() {
        return title;
    }

    public void setTitle(Component title) {
        this.title = title;
    }

    public Component getLore() {
        return lore;
    }

    public void setLore(Component lore) {
        this.lore = lore;
    }
}
