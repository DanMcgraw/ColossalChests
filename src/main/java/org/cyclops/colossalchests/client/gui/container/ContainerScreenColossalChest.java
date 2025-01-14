package org.cyclops.colossalchests.client.gui.container;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import org.cyclops.colossalchests.ColossalChests;
import org.cyclops.colossalchests.Reference;
import org.cyclops.colossalchests.inventory.container.ContainerColossalChest;
import org.cyclops.colossalchests.network.packet.ClickWindowPacketOverride;
import org.cyclops.cyclopscore.client.gui.component.WidgetScrollBar;
import org.cyclops.cyclopscore.client.gui.component.button.ButtonArrow;
import org.cyclops.cyclopscore.client.gui.container.ContainerScreenScrolling;
import org.cyclops.cyclopscore.helper.L10NHelpers;

/**
 * GUI for the {@link org.cyclops.colossalchests.block.ColossalChest}.
 * @author rubensworks
 *
 */
public class ContainerScreenColossalChest extends ContainerScreenScrolling<ContainerColossalChest> {

    private static final int TEXTUREWIDTH = 240;
    private static final int TEXTUREHEIGHT = 255;
    private WidgetScrollBar scrollbar2;

    public ContainerScreenColossalChest(ContainerColossalChest container, PlayerInventory inventory, ITextComponent title) {
        super(container, inventory, title);
    }

    @Override
    public void init() {
        super.init();
        addButton(new ButtonArrow(this.guiLeft + 242, this.guiTop + 7, new TranslationTextComponent("gui.cyclopscore.up"), (button) -> scrollRelative(1), ButtonArrow.Direction.NORTH));
        addButton(new ButtonArrow(this.guiLeft + 242, this.guiTop + 164, new TranslationTextComponent("gui.cyclopscore.down"), (button) -> scrollRelative(-1), ButtonArrow.Direction.SOUTH));
    }

    protected void scrollRelative(int direction) {
        int multiplier = Minecraft.getInstance().player.isCrouching() ? 9 : 1;
        this.getScrollbar().scrollRelative(direction * multiplier);
    }

    @Override
    protected boolean isSearchEnabled() {
        return false;
    }

    @Override
    protected boolean isSubsetRenderSlots() {
        return true;
    }

    @Override
    protected ResourceLocation constructGuiTexture() {
        return new ResourceLocation(Reference.MOD_ID, "textures/gui/colossal_chest.png");
    }

    @Override
    protected int getBaseXSize() {
        return TEXTUREWIDTH;
    }

    @Override
    protected int getBaseYSize() {
        return TEXTUREHEIGHT;
    }

    protected void drawForgegroundString(MatrixStack matrixStack) {
        font.drawString(matrixStack, getTitle().getString(), 8 + offsetX, 0 + offsetY, 4210752);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(MatrixStack matrixStack, int mouseX, int mouseY) {
        drawForgegroundString(matrixStack);
        //super.drawGuiContainerForegroundLayer(matrixStack, mouseX, mouseY);
    }

    @Override
    protected void handleMouseClick(Slot slotIn, int slotId, int clickedButton, ClickType clickType) {
        if (slotIn != null) {
            slotId = slotIn.slotNumber;
        }
        // Send our own packet, to avoid C0EPacketClickWindow to be sent to the server what would trigger an overflowable S30PacketWindowItems
        windowClick(this.container.windowId, slotId, clickedButton, clickType, this.getMinecraft().player);
    }

    // Adapted from PlayerController#windowClick
    protected ItemStack windowClick(int windowId, int slotId, int mouseButtonClicked, ClickType p_78753_4_, PlayerEntity playerIn) {
        short short1 = playerIn.openContainer.getNextTransactionID(playerIn.inventory);
        ItemStack itemstack = playerIn.openContainer.slotClick(slotId, mouseButtonClicked, p_78753_4_, playerIn);
        // Original: this.netClientHandler.addToSendQueue(new C0EPacketClickWindow(windowId, slotId, mouseButtonClicked, p_78753_4_, itemstack, short1));
        ColossalChests._instance.getPacketHandler().sendToServer(
                new ClickWindowPacketOverride(windowId, slotId, mouseButtonClicked, p_78753_4_, itemstack, short1));
        return itemstack;
    }
}
