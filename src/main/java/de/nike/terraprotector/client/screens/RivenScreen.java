package de.nike.terraprotector.client.screens;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.shaders.AbstractUniform;
import com.mojang.blaze3d.systems.RenderSystem;
import de.nike.terraprotector.TerraProtector;
import de.nike.terraprotector.client.render.INikeRenderer;
import de.nike.terraprotector.client.render.NikeForgeRenderer;
import de.nike.terraprotector.client.shaders.TShaders;
import de.nike.terraprotector.items.custom.IRivenSlotProvider;
import de.nike.terraprotector.items.custom.RivenHostDataHandler;
import de.nike.terraprotector.items.custom.RivenItem;
import de.nike.terraprotector.lib.InventoryUtil;
import de.nike.terraprotector.lib.NikesMath;
import de.nike.terraprotector.network.PacketHandler;
import de.nike.terraprotector.network.packets.CMoveRivenIntoRivenHostPacket;
import de.nike.terraprotector.network.packets.CRemoveRivenFromHostPacket;
import de.nike.terraprotector.stats.RivenStats;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.advancements.FrameType;
import net.minecraft.client.GameNarrator;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.registries.ForgeRegistries;
import top.theillusivec4.curios.api.CuriosApi;

import javax.annotation.Nullable;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class RivenScreen extends Screen{
    private static final ResourceLocation WINDOW_LOCATION = new ResourceLocation("textures/gui/advancements/window.png");
    public static final ResourceLocation TABS_LOCATION = new ResourceLocation("textures/gui/advancements/tabs.png");
    private static final ResourceLocation SELECTED_ITEM = new ResourceLocation(TerraProtector.MODID, "gui/selected_item.png");
    private static final ResourceLocation RIVEN_SLOT = new ResourceLocation(TerraProtector.MODID, "gui/riven_slot.png");
    public static final int WINDOW_WIDTH = 252;
    public static final int WINDOW_HEIGHT = 140;
    private static final int WINDOW_INSIDE_X = 9;
    private static final int WINDOW_INSIDE_Y = 18;
    public static final int WINDOW_INSIDE_WIDTH = 234;
    public static final int WINDOW_INSIDE_HEIGHT = 113;
    private static final int WINDOW_TITLE_X = 8;
    private static final int WINDOW_TITLE_Y = 6;
    public static final int BACKGROUND_TILE_WIDTH = 16;
    public static final int BACKGROUND_TILE_HEIGHT = 16;
    public static final int BACKGROUND_TILE_COUNT_X = 14;
    public static final int BACKGROUND_TILE_COUNT_Y = 7;
    private static final Component VERY_SAD_LABEL = Component.translatable("advancements.sad_label");
    private static final Component NO_ADVANCEMENTS_LABEL = Component.translatable("advancements.empty");
    private static final Component TITLE = Component.translatable("terraprotector.gui.riven.title");
    private final Map<RivenSlotInfo, RivenTab> tabs = Maps.newLinkedHashMap();
    @Nullable
    INikeRenderer renderer;
    private RivenTab selectedTab;
    private boolean isScrolling;
    private static int tabPage, maxPages;
    List<ItemStack> rivenItems = new ArrayList<>();
    List<ItemStack> compatibleRivens = new ArrayList<>();
    private ItemStack selectedStack;
    private HashMap<ItemStack, ItemTrace> originMap = new HashMap<>();
    private HashMap<ItemStack, Integer> rivenSlotMap = new HashMap<>();

    AbstractUniform timeUniform;
    AbstractUniform resolutionUniform;
    AbstractUniform colorSettingsUniform;
    AbstractUniform offsetUniform;
    public RivenScreen() {
        super(GameNarrator.NO_TITLE);
        renderer = new NikeForgeRenderer();
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    protected void init() {
        this.tabs.clear();
        this.selectedTab = null;
        this.minecraft = Minecraft.getInstance();
        this.rivenItems.clear();


        LocalPlayer player = minecraft.player;
        assert player != null;
        findRivenSlotProviders(player);
        findRivenItems(player);
        filterRivenItemsForCurrentTab(player, selectedTab);

        ShaderInstance shaderInstance = TShaders.galaxyShader;

        timeUniform = shaderInstance.getUniform("time");
        resolutionUniform = shaderInstance.getUniform("resolution");
        colorSettingsUniform = shaderInstance.getUniform("colorSettings");
        offsetUniform = shaderInstance.getUniform("offset");

        if (this.tabs.size() > RivenTabRenderType.MAX_TABS) {
            int guiLeft = (this.width - 252) / 2;
            int guiTop = (this.height - 140) / 2;
            addRenderableWidget(net.minecraft.client.gui.components.Button.builder(Component.literal("<"), b -> tabPage = Math.max(tabPage - 1, 0       ))
                    .pos(guiLeft, guiTop - 50).size(20, 20).build());
            addRenderableWidget(net.minecraft.client.gui.components.Button.builder(Component.literal(">"), b -> tabPage = Math.min(tabPage + 1, maxPages))
                    .pos(guiLeft + WINDOW_WIDTH - 20, guiTop - 50).size(20, 20).build());
            maxPages = this.tabs.size() / RivenTabRenderType.MAX_TABS;
        }
    }


    public void removed() {
        RivenStats.cache.clear();
    }

    public AbstractUniform getOffsetUniform() {
        return offsetUniform;
    }

    private void findRivenItems(LocalPlayer player) {
        for(ItemStack stack : player.getInventory().items) {
            if(stack.getItem() instanceof RivenItem) {
                rivenItems.add(stack);
            }
        }

        for(int slot = 0; slot < player.getInventory().getContainerSize(); slot++) {
            ItemStack stack = player.getInventory().getItem(slot);
            if(stack != null && stack.getItem() instanceof RivenItem) {
                rivenSlotMap.put(stack, slot);
            }
        }
    }

    private void filterRivenItemsForCurrentTab(LocalPlayer player, RivenTab tab) {
        compatibleRivens = rivenItems.stream()
                .filter(itemStack -> {
                    if (!(itemStack.getItem() instanceof RivenItem)) {
                        return false;
                    }
                    RivenItem item = (RivenItem) itemStack.getItem();
                    if (selectedTab == null || selectedTab.getSlotInfo() == null || selectedTab.getSlotInfo().getItem() == null) {
                        return false;
                    }

                    if (!(selectedTab.getSlotInfo().getItem().getItem() instanceof IRivenSlotProvider)) {
                        return false;
                    }

                    IRivenSlotProvider slotProvider = (IRivenSlotProvider) selectedTab.getSlotInfo().getItem().getItem();

                    // Return whether the item is compatible with the slotProvider
                    return RivenItem.isCompatibleWith(slotProvider, item);
                })
                .collect(Collectors.toList());
    }

     private void findRivenSlotProviders(LocalPlayer player) {
        List<ItemStack> allItems = new ArrayList<>();
        allItems.addAll(player.getInventory().items);
        CuriosApi.getCuriosInventory(player).ifPresent(s -> {
            int slotAmount = s.getEquippedCurios().getSlots();
            for(int slot = 0; slot < slotAmount; slot++) {
               ItemStack stack =  s.getEquippedCurios().getStackInSlot(slot);
               if(stack.getItem() instanceof IRivenSlotProvider) {
                   originMap.put(stack, new ItemTrace(true, slot));
               }
               if(stack != null) allItems.add(stack);
            }
        });

        for(int slot = 0; slot < player.getInventory().getContainerSize(); slot++) {
            ItemStack stack = player.getInventory().getItem(slot);
            if(stack != null && stack.getItem() instanceof IRivenSlotProvider) {
                originMap.put(stack, new ItemTrace(false, slot));
            }
        }



        int index = 0;

        int startY = -1;
        int endY = 2;
        int startX = 1;

        for(ItemStack stack : allItems) {
            if(stack == null) continue;
            if(stack.getItem() instanceof IRivenSlotProvider) {
                RivenSlotInfo rootInfo = new RivenSlotInfo(stack, stack.getDisplayName(), Component.translatable("terraprotector.rivenhost." + ForgeRegistries.ITEMS.getKey(stack.getItem()).getPath()), 0, 0, FrameType.CHALLENGE, null, false, -1);
                RivenTab rivenTab = new RivenTab(minecraft, this, RivenTabRenderType.ABOVE, index, rootInfo);

                int slots = ((IRivenSlotProvider) stack.getItem()).getSlots(stack);

                int x = startX;
                int y = startY;

                ItemStackHandler rivenInventory = InventoryUtil.createVirtualInventory(slots, RivenHostDataHandler.TAG_EQUIPPED_RIVEN, stack);

                for(int i = 0; i < slots; i++) {
                    ItemStack storedRiven = rivenInventory.getStackInSlot(i);
                    boolean empty = (storedRiven.equals(ItemStack.EMPTY));
                    RivenSlotInfo slotInfo = new RivenSlotInfo(storedRiven, empty ? Component.literal("Empty Riven Slot") : storedRiven.getDisplayName(), empty ? Component.literal("Click one of the rivens displayed at the bottom and click an empty slot to equip it!") : Component.literal("Stat display has yet to be implemented! Right click to remove the riven!"), x, y, FrameType.GOAL, rootInfo, true, i);
                    rivenTab.addAdvancement(slotInfo);
                    y++;
                    if(y >= endY) {
                        y = startY;
                        x++;
                    }
                }

                tabs.put(rootInfo, rivenTab);
                if(selectedTab == null) selectedTab = rivenTab;
                index++;
            }
        }
    }

    public boolean mouseClicked(double mouseX, double mouseY, int p_97345_) {
        if (p_97345_ == 0) {
            int i = (this.width - 252) / 2;
            int j = (this.height - 140) / 2;

            for(RivenTab advancementtab : this.tabs.values()) {
                if (advancementtab.getPage() == tabPage && advancementtab.isMouseOver(i, j, mouseX, mouseY)) {
                    selectedTab = advancementtab;
                    tabSwitchedTo(selectedTab);
                    break;
                }
            }

            int curX = i-2;
            int curY = j + 146;
            int index = 0;

            for(ItemStack stack : compatibleRivens) {
                if(NikesMath.isInside((float) mouseX, (float) mouseY, curX, curY, 16, 16)) {
                    selectedStack = stack;
                    break;
                }
                curX+=20;
                index++;
                if(index >= 13) {
                    curY+=18;
                    curX = i-2;
                    index = 0;
                }
            }


           if(selectedStack != null) {

                int locX = (this.width - 252) / 2;
                int locY = (this.height - 140) / 2;

                float f1 = (float) (mouseX - locX - 9);
                float f2 = (float) (mouseY - locY - 18);

                int s1 = Mth.floor(selectedTab.getScrollX());
                int s2 = Mth.floor(selectedTab.getScrollY());

                for (RivenWidget advancementwidget : selectedTab.getWidgets().values()) {
                    if(!advancementwidget.getAdvancement().isSlot()) continue;
                    if (advancementwidget.isMouseOver(s1, s2, (int) f1, (int) f2)) {
                        int rivenSlot = rivenSlotMap.getOrDefault(selectedStack, -1);
                        ItemTrace hostTrace = originMap.getOrDefault(selectedTab.getSlotInfo().getItem(), null);
                        if(hostTrace == null) break;
                        PacketHandler.clientSendServer(new CMoveRivenIntoRivenHostPacket(rivenSlot, hostTrace.slotIndex, hostTrace.isOriginCurio, advancementwidget.getAdvancement().getSlotIndex()));
                        advancementwidget.getAdvancement().setItem(selectedStack);
                        compatibleRivens.remove(selectedStack);
                        rivenItems.remove(selectedStack);
                        selectedStack = null;
                        break;
                    }
                }
            }
        } else if(p_97345_ == 2) {
            int locX = (this.width - 252) / 2;
            int locY = (this.height - 140) / 2;

            float f1 = (float) (mouseX - locX - 9);
            float f2 = (float) (mouseY - locY - 18);

            int s1 = Mth.floor(selectedTab.getScrollX());
            int s2 = Mth.floor(selectedTab.getScrollY());

            for (RivenWidget advancementwidget : selectedTab.getWidgets().values()) {
                if(!advancementwidget.getAdvancement().isSlot()) continue;
                if (advancementwidget.isMouseOver(s1, s2, (int) f1, (int) f2)) {
                    ItemTrace hostTrace = originMap.getOrDefault(selectedTab.getSlotInfo().getItem(), null);
                    if(hostTrace == null) break;
                    PacketHandler.clientSendServer(new CRemoveRivenFromHostPacket(hostTrace.slotIndex, advancementwidget.getAdvancement().getSlotIndex(), hostTrace.isOriginCurio));
                    advancementwidget.getAdvancement().setItem(ItemStack.EMPTY);
                    break;
                }
            }
        }

        return super.mouseClicked(mouseX, mouseY, p_97345_);
    }

    private void tabSwitchedTo(RivenTab rivenTab) {
        filterRivenItemsForCurrentTab(minecraft.player, selectedTab);
    }

    public boolean keyPressed(int p_97353_, int p_97354_, int p_97355_) {
        if (this.minecraft.options.keyAdvancements.matches(p_97353_, p_97354_)) {
            this.minecraft.setScreen((Screen)null);
            this.minecraft.mouseHandler.grabMouse();
            return true;
        } else {
            return super.keyPressed(p_97353_, p_97354_, p_97355_);
        }
    }

    public void render(GuiGraphics graphics, int p_282255_, int p_283354_, float p_283123_) {
        int i = (this.width - 252) / 2;
        int j = (this.height - 140) / 2;
        this.renderBackground(graphics);
        if (maxPages != 0) {
            Component page = Component.literal(String.format("%d / %d", tabPage + 1, maxPages + 1));
            int width = this.font.width(page);
            graphics.drawString(this.font, page.getVisualOrderText(), i + (252 / 2) - (width / 2), j - 44, -1);
        }

        this.renderCompatibleRivens(graphics, i, j);
        this.renderInside(graphics, p_282255_, p_283354_, i, j);
        this.renderWindow(graphics, i, j);
        this.renderTooltips(graphics, p_282255_, p_283354_, i, j);
        super.render(graphics, p_282255_, p_283354_, p_283123_);

    }

    public void renderCompatibleRivens(GuiGraphics graphics, int x, int y) {
        int curX = x-2;
        int curY = y + 146;
        int index = 0;

        for(ItemStack stack : compatibleRivens) {
            graphics.blit(RIVEN_SLOT, curX, curY, 0.0F, 0.0F, 16, 16, 16, 16);
            graphics.renderFakeItem(stack, curX, curY);
            if(stack == selectedStack) {
                graphics.blit(SELECTED_ITEM, curX, curY, 0.0F, 0.0F, 16, 16, 16, 16);

            }
            curX+=20;
            index++;
            if(index >= 13) {
                curY+=18;
                curX = x-2;
                index = 0;
            }
        }
    }

    public boolean mouseDragged(double p_97347_, double p_97348_, int p_97349_, double p_97350_, double p_97351_) {
        if (p_97349_ != 0) {
            this.isScrolling = false;
            return false;
        } else {
            if (!this.isScrolling) {
                this.isScrolling = true;
            } else if (this.selectedTab != null) {
                this.selectedTab.scroll(p_97350_, p_97351_);
            }

            return true;
        }
    }

    private void renderInside(GuiGraphics p_282012_, int p_97375_, int p_97376_, int p_97377_, int p_97378_) {
         RivenTab advancementtab = this.selectedTab;
        if (advancementtab == null) {
            p_282012_.fill(p_97377_ + 9, p_97378_ + 18, p_97377_ + 9 + 234, p_97378_ + 18 + 113, -16777216);
            int i = p_97377_ + 9 + 117;
            p_282012_.drawCenteredString(this.font, NO_ADVANCEMENTS_LABEL, i, p_97378_ + 18 + 56 - 9 / 2, -1);
            p_282012_.drawCenteredString(this.font, VERY_SAD_LABEL, i, p_97378_ + 18 + 113 - 9, -1);
        } else {
            advancementtab.drawContents(p_282012_, p_97377_ + 9, p_97378_ + 18);
        }

    }

    public AbstractUniform getColorSettingsUniform() {
        return colorSettingsUniform;
    }

    public AbstractUniform getResolutionUniform() {
        return resolutionUniform;
    }

    public AbstractUniform getTimeUniform() {
        return timeUniform;
    }

    public void renderWindow(GuiGraphics p_283395_, int p_281890_, int p_282532_) {
        RenderSystem.enableBlend();
        p_283395_.blit(WINDOW_LOCATION, p_281890_, p_282532_, 0, 0, 252, 140);
        if (this.tabs.size() > 0) {
            for(RivenTab advancementtab : this.tabs.values()) {
                if (advancementtab.getPage() == tabPage)
                    advancementtab.drawTab(p_283395_, p_281890_, p_282532_, advancementtab == this.selectedTab);
            }

            for(RivenTab advancementtab1 : this.tabs.values()) {
                if (advancementtab1.getPage() == tabPage)
                    advancementtab1.drawIcon(p_283395_, p_281890_, p_282532_);
            }
        }

        p_283395_.drawString(this.font, TITLE, p_281890_ + 8, p_282532_ + 6, 4210752, false);
    }

    private void renderTooltips(GuiGraphics graphics, int mouseX, int mouseY, int locX, int locY) {

        int curX = locX-2;
        int curY = locY + 146;
        int index = 0;

        for(ItemStack stack : compatibleRivens) {
            if(NikesMath.isInside(mouseX, mouseY, curX, curY, 16, 16)) {
                graphics.renderTooltip(font, stack, mouseX, mouseY);
            }
            curX+=20;
            index++;
            if(index >= 13) {
                curY+=18;
                curX = locX-2;
                index = 0;
            }
        }


        if (this.selectedTab != null) {
            graphics.pose().pushPose();
            graphics.pose().translate((float)(locX + 9), (float)(locY + 18), 400.0F);
            RenderSystem.enableDepthTest();
            this.selectedTab.drawTooltips(graphics, mouseX - locX - 9, mouseY - locY - 18, locX, locY);
            RenderSystem.disableDepthTest();
            graphics.pose().popPose();
        }

        if (this.tabs.size() > 0) {
            for(RivenTab advancementtab : this.tabs.values()) {
                if (advancementtab.getPage() == tabPage && advancementtab.isMouseOver(locX, locY, (double)mouseX, (double)mouseY)) {
                 //   graphics.renderTooltip(this.font, advancementtab.getTitle(), p_283556_, p_282458_);
                    graphics.renderTooltip(font, advancementtab.getSlotInfo().getItem(), mouseX, mouseY);
                }
            }
        }

    }

    public void onAddAdvancementRoot(RivenSlotInfo p_97366_) {
        RivenTab advancementtab = RivenTab.create(this.minecraft, this, this.tabs.size(), p_97366_);
        if (advancementtab != null) {
            this.tabs.put(p_97366_, advancementtab);
        }
    }

    public void onRemoveAdvancementRoot(RivenSlotInfo p_97372_) {
    }


    public void onRemoveAdvancementTask(RivenSlotInfo p_97388_) {
    }

    public void onUpdateAdvancementProgress(RivenSlotInfo p_97368_, AdvancementProgress p_97369_) {
        RivenWidget advancementwidget = this.getAdvancementWidget(p_97368_);
        if (advancementwidget != null) {
            advancementwidget.setProgress(p_97369_);
        }

    }

    public void onSelectedTabChanged(@Nullable RivenSlotInfo p_97391_) {
        this.selectedTab = this.tabs.get(p_97391_);
    }

    public void onAdvancementsCleared() {
        this.tabs.clear();
        this.selectedTab = null;
    }

    @Nullable
    public RivenWidget getAdvancementWidget(RivenSlotInfo p_97393_) {
        RivenTab advancementtab = this.getTab(p_97393_);
        return advancementtab == null ? null : advancementtab.getWidget(p_97393_);
    }

    @Nullable
    private RivenTab getTab(RivenSlotInfo p_97395_) {
        while(p_97395_.getParent() != null) {
            p_97395_ = p_97395_.getParent();
        }

        return this.tabs.get(p_97395_);
    }

    private static class ItemTrace {

        private final boolean isOriginCurio;
        private final int slotIndex;

        private ItemTrace(boolean isOriginCurio, int slotIndex) {
            this.isOriginCurio = isOriginCurio;
            this.slotIndex = slotIndex;
        }

        public int getSlotIndex() {
            return slotIndex;
        }

        public boolean isOriginCurio() {
            return isOriginCurio;
        }

        @Override
        public String toString() {
            return "ItemTrace{" +
                    "isOriginCurio=" + isOriginCurio +
                    ", slotIndex=" + slotIndex +
                    '}';
        }
    }
}
