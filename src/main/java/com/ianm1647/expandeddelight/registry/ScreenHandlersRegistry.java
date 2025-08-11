package com.ianm1647.expandeddelight.registry;

import com.ianm1647.expandeddelight.ExpandedDelight;
import com.ianm1647.expandeddelight.screen.custom.JuicerScreen;
import com.ianm1647.expandeddelight.screen.custom.JuicerScreenHandler;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

public class ScreenHandlersRegistry {
    public static ScreenHandlerType<JuicerScreenHandler> JUICER_HANDLER =  Registry.register(Registries.SCREEN_HANDLER, Identifier.of(ExpandedDelight.MODID, "juicer"), new ExtendedScreenHandlerType<>(JuicerScreenHandler::new, BlockPos.PACKET_CODEC));

    public static void registerHandlers() {
//        JUICER_HANDLER = handler("juicer", JuicerScreenHandler::new);
    }

    public static void registerScreens() {
        HandledScreens.register(JUICER_HANDLER, JuicerScreen::new);
    }

//    private static <T extends ScreenHandler> ScreenHandlerType<T> handler(String name, ExtendedScreenHandlerType<T> handler) {
////        return Registry.register(Registries.SCREEN_HANDLER, Identifier.of(ExpandedDelight.MODID, name), new ExtendedScreenHandlerType<>(handler, BlockPos.PACKET_CODEC));
//    }
}
