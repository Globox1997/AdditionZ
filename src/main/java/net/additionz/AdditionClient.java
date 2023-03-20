package net.additionz;

import net.additionz.block.JukeBoxEntityRenderer;
import net.additionz.misc.FletchingScreen;
import net.additionz.network.AdditionClientPacket;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.client.gui.screen.ingame.HandledScreens;

@Environment(EnvType.CLIENT)
public class AdditionClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        AdditionClientPacket.init();
        HandledScreens.register(AdditionMain.FLETCHING, FletchingScreen::new);
        BlockEntityRendererRegistry.register(BlockEntityType.JUKEBOX, JukeBoxEntityRenderer::new);
    }

}
