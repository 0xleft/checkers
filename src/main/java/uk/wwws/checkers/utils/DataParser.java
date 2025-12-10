package uk.wwws.checkers.utils;

import java.util.Scanner;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.wwws.checkers.net.PacketAction;
import uk.wwws.checkers.ui.CommandAction;

public interface DataParser {
    default @Nullable String getNext(@NotNull Scanner data) {
        try {
            return data.next();
        } catch (Exception e) {
            return null;
        }
    }

    default @Nullable Integer getNextInt(@NotNull Scanner data) {
        try {
            return data.nextInt();
        } catch (Exception e) {
            return null;
        }
    }

    default @Nullable CommandAction getNextCommandAction(@NotNull Scanner data) {
        try {
            return CommandAction.valueOf(getNext(data).toUpperCase());
        } catch (Exception e) {
            return null;
        }
    }

    default @Nullable PacketAction getNextPacketAction(@NotNull Scanner data) {
        try {
            return PacketAction.valueOf(getNext(data).toUpperCase());
        } catch (Exception e) {
            return null;
        }
    }
}
