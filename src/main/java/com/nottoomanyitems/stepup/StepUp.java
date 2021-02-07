package com.nottoomanyitems.stepup;

import com.nottoomanyitems.stepup.Client.ClientEvents;
import com.nottoomanyitems.stepup.config.StepUpConfig;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.ExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;

import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.fml.network.FMLNetworkConstants;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;


@Mod(StepUp.MOD_ID)
public final class StepUp {
    public static final String MOD_ID = "stepupplus";
	public static final String MOD_NAME = "StepUpPlus";
	
	public StepUp() {
		// From: https://mcforge.readthedocs.io/en/latest/concepts/sides/
		// Make sure the mod being absent on the other network side does not cause the client to display the server as incompatible
		ModLoadingContext.get().registerExtensionPoint(ExtensionPoint.DISPLAYTEST, () -> Pair.of(() -> FMLNetworkConstants.IGNORESERVERONLY, (a, b) -> true));

		if (FMLEnvironment.dist != Dist.CLIENT) {
			LogManager.getLogger(StepUp.MOD_ID).log(Level.ERROR, "You tried loading " + MOD_NAME + " on a dedicated server! This is a client side only mod!");
			return;
		}

		StepUpConfig.init();
		ClientEvents.init();
    }
}
