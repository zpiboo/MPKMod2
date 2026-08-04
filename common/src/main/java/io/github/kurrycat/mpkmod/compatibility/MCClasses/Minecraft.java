package io.github.kurrycat.mpkmod.compatibility.MCClasses;

import io.github.kurrycat.mpkmod.compatibility.API;
import io.github.kurrycat.mpkmod.events.Event;
import io.github.kurrycat.mpkmod.gui.MPKGuiScreen;
import io.github.kurrycat.mpkmod.gui.infovars.InfoString;
import io.github.kurrycat.mpkmod.util.input.TickInput;
import io.github.kurrycat.mpknetapi.common.network.packet.MPKPacket;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Optional;

@SuppressWarnings("unused")
@InfoString.AccessInstance
public class Minecraft {
    private static Interface impl;

    public static void setImpl(Interface impl) {
        if (Minecraft.impl != null)
            throw new IllegalStateException("Cannot set impl twice!");

        Minecraft.impl = impl;
    }

    public static String version;
    public static String vfpVersion = null;
    public static WorldState worldState = WorldState.MENU;
    public static PlayState playState = PlayState.ACTIVE;
    public static boolean sprintToggled = false;
    public static int ping = -1;

    @InfoString.Getter
    public static String getIp() {
        if (isSingleplayer()) return "Singleplayer";

        String ip = impl.getIP();

        if (ip == null) return "Multiplayer";
        return ip;
    }

    @InfoString.Getter
    public static boolean isSingleplayer() {
        return worldState == WorldState.SINGLE_PLAYER;
    }

    @InfoString.Getter
    public static String getFps() {
        return impl.getFPS();
    }

    @InfoString.Getter
    public static int getPing() {
        return impl.getPing();
    }

    @InfoString.Getter
    public static String getMcVersion() {
        return vfpVersion == null ? version : vfpVersion + " (VFP)";
    }

    @InfoString.Getter
    public static String getUsername() {
        return impl.getUserName();
    }

    public static String getCurrentGuiScreen() {
        return impl.getCurrentGuiScreen();
    }

    @InfoString.Getter
    public static String getTime() {
        return new SimpleDateFormat("HH:mm:ss").format(Calendar.getInstance().getTime());
    }

    @InfoString.Getter
    public static String getDate() {
        return new SimpleDateFormat("dd/MM/yy").format(Calendar.getInstance().getTime());
    }

    public static void updateWorldState(Event.EventType type, boolean isLocal) {
        if (type == Event.EventType.SERVER_CONNECT) {
            if (isLocal) worldState = WorldState.SINGLE_PLAYER;
            else worldState = WorldState.MULTI_PLAYER;
        } else worldState = WorldState.MENU;
    }

    public static void displayGuiScreen(MPKGuiScreen screen) {
        impl.displayGuiScreen(screen);
    }

    public static void copyToClipboard(String str) {
        impl.copyToClipboard(str);
    }

    public static boolean setInputs(TickInput inputs) {
        return setInputs(inputs.getYaw(), true,
                inputs.getPitch(), true,
                inputs.getKeyInputs(), ~inputs.getKeyInputs(),
                inputs.getL(), inputs.getR());
    }

    public static boolean setInputs(Float yaw, boolean relYaw, Float pitch, boolean relPitch, int pressedInputs, int releasedInputs, int L, int R) {
        if (!Minecraft.isSingleplayer()) return false;
        return impl.setInputs(yaw, relYaw, pitch, relPitch, pressedInputs, releasedInputs, L, R);
    }

    public static boolean isF3Enabled() {
        return impl.isF3Enabled();
    }

    @InfoString.Getter
    public static boolean isSprintToggled() {
        return sprintToggled;
    }

    public static void toggleSprint() {
        sprintToggled = !sprintToggled;
    }


    public static void sendPacket(MPKPacket packet) {
        impl.sendPacket(packet);
    }


    public enum WorldState {
        MENU,
        SINGLE_PLAYER,
        MULTI_PLAYER;
    }

    public enum PlayState {
        ACTIVE,
        AFK;
    }

    public interface Interface {
        String getIP();

        String getFPS();

        int getPing();

        void displayGuiScreen(MPKGuiScreen screen);

        String getCurrentGuiScreen();

        String getUserName();

        void copyToClipboard(String content);

        boolean setInputs(Float yaw, boolean relYaw, Float pitch, boolean relPitch, int pressedInputs, int releasedInputs, int L, int R);

        boolean isF3Enabled();

        void sendPacket(MPKPacket packet);
    }
}
