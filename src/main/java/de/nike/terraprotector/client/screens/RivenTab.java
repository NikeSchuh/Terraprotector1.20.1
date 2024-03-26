package de.nike.terraprotector.client.screens;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.vertex.BufferBuilder;
import de.nike.terraprotector.TerraProtector;
import de.nike.terraprotector.client.ClientTickHandler;
import de.nike.terraprotector.client.render.RenderHelper;
import de.nike.terraprotector.client.shaders.TShaders;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.DisplayInfo;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.advancements.AdvancementWidget;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;
import java.awt.*;
import java.util.Map;
import java.util.Objects;

public class RivenTab {

    private final Minecraft minecraft;
    private final RivenScreen screen;
    private final RivenTabRenderType type;
    private final int index;
    private final RivenSlotInfo slotInfo;
    private final ItemStack icon;
    private final Component title;
    private final RivenWidget root;
    private final Map<RivenSlotInfo, RivenWidget> widgets = Maps.newLinkedHashMap();
    private double scrollX;
    private double scrollY;
    private int minX = Integer.MAX_VALUE;
    private int minY = Integer.MAX_VALUE;
    private int maxX = Integer.MIN_VALUE;
    private int maxY = Integer.MIN_VALUE;
    private float fade;
    private boolean centered;
    private int page;

    public RivenTab(Minecraft minecraft, RivenScreen rivenScreen, RivenTabRenderType tabRenderType, int index, RivenSlotInfo slotInfo) {
        this.minecraft = minecraft;
        this.screen = rivenScreen;
        this.type = tabRenderType;
        this.index = index;
        this.slotInfo =slotInfo;
        this.icon = slotInfo.getItem();
        this.title = slotInfo.getTitle();
        this.root = new RivenWidget(this, minecraft, slotInfo);
        this.addWidget(this.root, slotInfo);
    }

    public RivenTab(Minecraft mc, RivenScreen screen, RivenTabRenderType type, int index, int page, RivenSlotInfo slotInfo) {
        this(mc, screen, type, index, slotInfo);
        this.page = page;
    }

    public int getPage() {
        return page;
    }

    public RivenTabRenderType getType() {
        return this.type;
    }

    public int getIndex() {
        return this.index;
    }

    public RivenSlotInfo getSlotInfo() {
        return this.slotInfo;
    }

    public Component getTitle() {
        return this.title;
    }

    public RivenWidget getRoot() {
        return root;
    }

    public void drawTab(GuiGraphics p_282671_, int p_282721_, int p_282964_, boolean p_283052_) {
        this.type.draw(p_282671_, p_282721_, p_282964_, p_283052_, this.index);
    }

    public void drawIcon(GuiGraphics p_282895_, int p_283419_, int p_283293_) {
        this.type.drawIcon(p_282895_, p_283419_, p_283293_, this.index, this.icon);
    }

    public void drawContents(GuiGraphics graphics, int p_282962_, int p_281511_) {
        if (!this.centered) {
            this.scrollX = (double)(117 - (this.maxX + this.minX) / 2);
            this.scrollY = (double)(56 - (this.maxY + this.minY) / 2);
            this.centered = true;
        }

        // Backround
        graphics.enableScissor(p_282962_, p_281511_, p_282962_ + 234, p_281511_ + 113);
        graphics.pose().pushPose();
        graphics.pose().translate((float)p_282962_, (float)p_281511_, 0.0F);
        ResourceLocation resourcelocation = Objects.requireNonNullElse(new ResourceLocation(TerraProtector.MODID, "gui/test.png"), TextureManager.INTENTIONAL_MISSING_TEXTURE);
        int i = Mth.floor(this.scrollX);
        int j = Mth.floor(this.scrollY);
       /*/ int k = i % 16;
        int l = j % 16;

        for(int i1 = -1; i1 <= 15; ++i1) {
            for(int j1 = -1; j1 <= 8; ++j1) {
                p_282728_.blit(resourcelocation, k + 16 * i1, l + 16 * j1, 0.0F, 0.0F, 16, 16, 16, 16);
            }
        }/*/

        BufferBuilder testBuffer = RenderHelper.setupRectangle(TShaders.galaxyShader);

        screen.getTimeUniform().set(ClientTickHandler.time / 160F);
        screen.getResolutionUniform().set(1920F, 1080F);
        float baseValue = ClientTickHandler.frameNum / 500F;
        screen.getColorSettingsUniform().set((float) (((Math.sin(baseValue * 0.25F) + 1.0F) / 2.0F) * 0.5F),
                (float) (((Math.sin(baseValue + 0.5F) + 1.0F) / 2.0F) * 0.5F),
                (float) (((Math.sin(baseValue * 1.1F) + 1.0F) / 2.0F) * 0.5F),
                (float) (((Math.sin(baseValue * 1.5F) + 1.0F) / 2.0F) * 0.5F));
        screen.getOffsetUniform().set((float)scrollX, 0F, 0F);

        RenderHelper.rectangle(testBuffer, graphics.pose().last().pose(), 0, 0, 300, 300, Color.RED.getRGB());
        RenderHelper.endRectangle(testBuffer);

        this.root.drawConnectivity(graphics, i, j, true);
        this.root.drawConnectivity(graphics, i, j, false);
        this.root.draw(graphics, i, j);
        graphics.pose().popPose();
        graphics.disableScissor();
    }

    public void drawTooltips(GuiGraphics p_282892_, int p_283658_, int p_282602_, int p_282652_, int p_283595_) {
        p_282892_.pose().pushPose();
        p_282892_.pose().translate(0.0F, 0.0F, -200.0F);
        p_282892_.fill(0, 0, 234, 113, Mth.floor(this.fade * 255.0F) << 24);
        boolean flag = false;
        int i = Mth.floor(this.scrollX);
        int j = Mth.floor(this.scrollY);
        if (p_283658_ > 0 && p_283658_ < 234 && p_282602_ > 0 && p_282602_ < 113) {
            for(RivenWidget advancementwidget : this.widgets.values()) {
                if (advancementwidget.isMouseOver(i, j, p_283658_, p_282602_)) {
                    flag = true;
                    advancementwidget.drawHover(p_282892_, i, j, this.fade, p_282652_, p_283595_);
                    break;
                }
            }
        }

        p_282892_.pose().popPose();
        if (flag) {
            this.fade = Mth.clamp(this.fade + 0.02F, 0.0F, 0.3F);
        } else {
            this.fade = Mth.clamp(this.fade - 0.04F, 0.0F, 1.0F);
        }

    }

    public Map<RivenSlotInfo, RivenWidget> getWidgets() {
        return widgets;
    }

    public double getScrollX() {
        return scrollX;
    }

    public double getScrollY() {
        return scrollY;
    }

    public boolean isMouseOver(int p_97155_, int p_97156_, double p_97157_, double p_97158_) {
        return this.type.isMouseOver(p_97155_, p_97156_, this.index, p_97157_, p_97158_);
    }

    @Nullable
    public static RivenTab create(Minecraft p_97171_, RivenScreen p_97172_, int p_97173_, RivenSlotInfo p_97174_) {
        if (p_97174_ == null) {
            return null;
        } else {
            for(RivenTabRenderType advancementtabtype : RivenTabRenderType.values()) {
                if ((p_97173_ % RivenTabRenderType.MAX_TABS) < advancementtabtype.getMax()) {
                    return new RivenTab(p_97171_, p_97172_, advancementtabtype, p_97173_ % RivenTabRenderType.MAX_TABS, p_97173_ / RivenTabRenderType.MAX_TABS, p_97174_);
                }

                p_97173_ -= advancementtabtype.getMax();
            }

            return null;
        }
    }

    public void scroll(double p_97152_, double p_97153_) {
        if (this.maxX - this.minX > 234) {
            this.scrollX = Mth.clamp(this.scrollX + p_97152_, (double)(-(this.maxX - 234)), 0.0D);
        }

        if (this.maxY - this.minY > 113) {
            this.scrollY = Mth.clamp(this.scrollY + p_97153_, (double)(-(this.maxY - 113)), 0.0D);
        }

    }

    public void addAdvancement(RivenSlotInfo p_97179_) {
        if (p_97179_ != null) {
            RivenWidget rivenWidget = new RivenWidget(this, this.minecraft, p_97179_);
            this.addWidget(rivenWidget, p_97179_);
        }
    }

    private void addWidget(RivenWidget p_97176_, RivenSlotInfo p_97177_) {
        this.widgets.put(p_97177_, p_97176_);
        int i = p_97176_.getX();
        int j = i + 28;
        int k = p_97176_.getY();
        int l = k + 27;
        this.minX = Math.min(this.minX, i);
        this.maxX = Math.max(this.maxX, j);
        this.minY = Math.min(this.minY, k);
        this.maxY = Math.max(this.maxY, l);

        for(RivenWidget rivenWidget : this.widgets.values()) {
            rivenWidget.attachToParent();
        }

    }

    @Nullable
    public RivenWidget getWidget(RivenSlotInfo r  ) {
        return this.widgets.get(r);
    }

    public RivenScreen getScreen() {
        return this.screen;
    }
}
