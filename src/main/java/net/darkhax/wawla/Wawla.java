package net.darkhax.wawla;

import org.apache.logging.log4j.Logger;

import net.darkhax.wawla.config.WawlaConfiguration;
import net.darkhax.wawla.engine.InfoEngine;
import net.darkhax.wawla.engine.WailaEngine;
import net.darkhax.wawla.plugins.FeatureManager;
import net.darkhax.wawla.plugins.InfoProvider;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@Mod(modid = "wawla", name = "What are We Looking at", version = "@VERSION@", guiFactory = "", acceptableRemoteVersions = "*")
public class Wawla {

    public static WawlaConfiguration config;

    public static InfoEngine engine;

    public static Logger LOG = null;

    @EventHandler
    public void preInit (FMLPreInitializationEvent event) {

        LOG = event.getModLog();
        config = new WawlaConfiguration("wawla");

        FeatureManager.init(event.getAsmData());

        config.init(FeatureManager.classes);
        config.sync();

        if (Loader.isModLoaded("waila")) {
            engine = new WailaEngine();
        }
    }

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void onItemTooltip (ItemTooltipEvent event) {

        if (event.getEntityPlayer() != null && event.getEntityPlayer().world != null && event.getItemStack() != null) {
            for (final InfoProvider provider : FeatureManager.itemProviders) {
                provider.addItemInfo(event.getToolTip(), event.getItemStack(), event.getFlags(), event.getEntityPlayer());
            }
        }
    }

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void onOverlayRendered (RenderGameOverlayEvent.Text event) {

        final Minecraft mc = Minecraft.getMinecraft();

        if (mc.gameSettings.showDebugInfo && event.getLeft() != null && Wawla.engine != null) {
            event.getLeft().add("[Wawla] Info Engine: " + Wawla.engine.getName());
        }
    }
}