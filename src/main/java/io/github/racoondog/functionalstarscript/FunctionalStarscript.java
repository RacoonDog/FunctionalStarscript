package io.github.racoondog.functionalstarscript;

import com.mojang.logging.LogUtils;
import meteordevelopment.meteorclient.addons.MeteorAddon;
import meteordevelopment.meteorclient.utils.misc.MeteorStarscript;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.slf4j.Logger;

@Environment(EnvType.CLIENT)
public class FunctionalStarscript extends MeteorAddon {
    public static final Logger LOG = LogUtils.getLogger();

    @Override
    public void onInitialize() {
        StarscriptWrappers.init(MeteorStarscript.ss);
    }

    @Override
    public String getPackage() {
        return "io.github.racoondog.functionalstarscript";
    }
}
