package net.additionz.block.screen;

import com.mojang.blaze3d.systems.RenderSystem;

import net.additionz.AdditionMain;
import net.additionz.network.AdditionClientPacket;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerListener;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class ChunkLoaderScreen extends HandledScreen<ChunkLoaderScreenHandler> implements ScreenHandlerListener {

    private static final Identifier TEXTURE = new Identifier("additionz:textures/gui/chunk_loader.png");
    private final ChunkLoaderScreen.ChunkButton[] chunkButtons = new ChunkLoaderScreen.ChunkButton[9];

    public ChunkLoaderScreen(ChunkLoaderScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
    }

    @Override
    protected void init() {
        super.init();
        this.handler.addListener(this);

        int k = 0;
        int o = 0;
        for (int l = 0; l < 9; ++l) {
            if (l != 0 && l % 3 == 0) {
                k = 0;
                o += 18;
            }
            final int chunkId = l;
            this.chunkButtons[l] = this.addDrawableChild(new ChunkLoaderScreen.ChunkButton(this.x + 16 + k, this.y + 16 + o, (button) -> {
                if (button instanceof ChunkButton chunkButton) {
                    if (chunkButton.enabled) {
                        if (chunkId != 4) {
                            AdditionClientPacket.writeC2SChunkLoaderPacket(this.handler.getChunkLoaderEntity().getPos(), chunkId, false);
                            this.handler.getChunkLoaderEntity().removeChunk(chunkId);
                        }
                    } else if (chunkButton.canActivate) {
                        if (this.handler.getChunkLoaderEntity().getChunkList().size() < this.handler.getChunkLoaderEntity().getMaxChunksLoaded()) {
                            AdditionClientPacket.writeC2SChunkLoaderPacket(this.handler.getChunkLoaderEntity().getPos(), chunkId, true);
                            this.handler.getChunkLoaderEntity().addChunk(chunkId);
                        }
                    }
                }
            }));
            if (this.handler.getExistingForcedChunkIds().contains(l)) {
                this.chunkButtons[l].loadedByOtherChunkLoader = true;
            }
            k += 18;
        }
    }

    @Override
    public void handledScreenTick() {
        super.handledScreenTick();
        for (int l = 0; l < 9; l++) {
            if (this.chunkButtons[l].loadedByOtherChunkLoader) {
                continue;
            }
            if (this.handler.getChunkLoaderEntity().getBurnTime() > 0) {
                if (this.handler.getChunkLoaderEntity().getChunkList().contains(l)) {
                    if (!this.chunkButtons[l].canActivate || !this.chunkButtons[l].enabled) {
                        this.chunkButtons[l].canActivate = true;
                        this.chunkButtons[l].enabled = true;
                    }
                } else if (this.handler.getChunkLoaderEntity().getMaxChunksLoaded() != 1
                        && this.handler.getChunkLoaderEntity().getChunkList().size() < this.handler.getChunkLoaderEntity().getMaxChunksLoaded()) {
                    if (!this.chunkButtons[l].canActivate) {
                        this.chunkButtons[l].canActivate = true;
                    }
                    this.chunkButtons[l].enabled = false;
                } else if (this.chunkButtons[l].canActivate || this.chunkButtons[l].enabled) {
                    this.chunkButtons[l].canActivate = false;
                    this.chunkButtons[l].enabled = false;
                }
            } else if (!this.handler.getChunkLoaderEntity().isActive() && this.chunkButtons[l].canActivate) {
                this.chunkButtons[l].canActivate = false;
            }
        }
    }

    @Override
    public void removed() {
        super.removed();
        this.handler.removeListener(this);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context);
        super.render(context, mouseX, mouseY, delta);
        if (this.handler.getChunkLoaderEntity().getBurnTime() > 0) {
            float remainingBurn = (float) this.handler.getChunkLoaderEntity().getBurnTime() / (float) AdditionMain.CONFIG.chunk_loader_fuel_time;
            context.drawTexture(TEXTURE, this.x + 118, this.y + 9 + 20 - (int) (remainingBurn * 20.0f), 176, 20 - (int) (remainingBurn * 20.0f), 12, (int) (remainingBurn * 20.0f));
        }

        this.drawMouseoverTooltip(context, mouseX, mouseY);
    }

    @Override
    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
        context.drawTexture(TEXTURE, this.x, this.y, 0, 0, this.backgroundWidth, this.backgroundHeight);
    }

    @Override
    protected void drawForeground(DrawContext context, int mouseX, int mouseY) {
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public void onSlotUpdate(ScreenHandler var1, int var2, ItemStack var3) {
    }

    @Override
    public void onPropertyUpdate(ScreenHandler var1, int var2, int var3) {
    }

    public class ChunkButton extends ButtonWidget {

        private boolean enabled = false;
        private boolean canActivate = false;
        private boolean loadedByOtherChunkLoader = false;

        public ChunkButton(int x, int y, ButtonWidget.PressAction onPress) {
            super(x, y, 18, 18, Text.of(""), onPress, DEFAULT_NARRATION_SUPPLIER);
        }

        @Override
        public void renderButton(DrawContext context, int mouseX, int mouseY, float delta) {
            RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, this.alpha);
            int i = this.getTextureY();
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            RenderSystem.enableDepthTest();
            context.drawTexture(TEXTURE, this.getX(), this.getY(), i * 18, 166, this.width, this.height);
            RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            if (!this.canActivate || this.loadedByOtherChunkLoader) {
                return false;
            }
            return super.mouseClicked(mouseX, mouseY, button);
        }

        private int getTextureY() {
            int i = 0;
            if (this.loadedByOtherChunkLoader) {
                return 4;
            } else if (!this.canActivate) {
                return 3;
            } else if (this.enabled) {
                i = 1;
            } else if (this.isHovered()) {
                i = 2;
            }
            return i;
        }

    }

}
