package net.additionz;

import net.additionz.network.AdditionClientPacket;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class AdditionClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        AdditionClientPacket.init();

    }

}
