package net.additionz;

import net.additionz.block.JukeBoxEntityRenderer;
import net.additionz.misc.FletchingScreen;
import net.additionz.network.AdditionClientPacket;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class AdditionClient implements ClientModInitializer {

    public static final Identifier AGE_INFO = new Identifier("additionz", "age_info");

    @Override
    public void onInitializeClient() {
        AdditionClientPacket.init();
        HandledScreens.register(AdditionMain.FLETCHING, FletchingScreen::new);
        BlockEntityRendererFactories.register(BlockEntityType.JUKEBOX, JukeBoxEntityRenderer::new);
    }

}
