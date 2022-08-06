package net.additionz.waila;

import java.util.ArrayList;
import java.util.List;

import mcp.mobius.waila.api.IRegistrar;
import mcp.mobius.waila.api.IWailaPlugin;

public class AgeWailaPlugin implements IWailaPlugin {

    private static final List<AgeFeature> features = new ArrayList<>();

    @Override
    public void register(IRegistrar registrar) {
        features.add(new AgeWailaBlockInfo());
        features.forEach(feature -> feature.initialize(registrar));
    }

}
