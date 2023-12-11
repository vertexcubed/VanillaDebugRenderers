package com.vivi.vanilladebugrenderers;

import com.mojang.logging.LogUtils;
import com.vivi.vanilladebugrenderers.command.VDRCommand;
import net.minecraftforge.client.event.RegisterClientCommandsEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import org.slf4j.Logger;

@Mod(VanillaDebugRenderers.MOD_ID)
public class VanillaDebugRenderers {
    public static final String MOD_ID = "vanilladebugrenderers";
    public static final Logger LOGGER = LogUtils.getLogger();


    public VanillaDebugRenderers() {

        MinecraftForge.EVENT_BUS.addListener(this::registerCommands);
    }


    public void registerCommands(RegisterClientCommandsEvent event) {
        VDRCommand.register(event.getDispatcher(), event.getBuildContext());
    }
}
