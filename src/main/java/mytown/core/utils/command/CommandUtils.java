package mytown.core.utils.command;

import com.esotericsoftware.reflectasm.MethodAccess;
import mytown.core.MyEssentialsCore;
import net.minecraft.command.CommandHandler;
import net.minecraft.command.ICommand;
import net.minecraft.server.MinecraftServer;

public class CommandUtils {
    private static boolean isInit = false;

    /**
     * CommandHandler of Minecraft
     */
    private static CommandHandler commandHandler;
    /**
     * MethodAccess to the CommandHandler for calling private methods
     */
    private static MethodAccess access;
    /**
     * Method index since the call occurs using indexes instead of names.
     */
    private static int method = -1;

    /**
     * Initializing the fields
     */
    private static void init() {
        try {
            CommandUtils.commandHandler = (CommandHandler) MinecraftServer.getServer().getCommandManager();
        } catch (Exception ex) {
            MyEssentialsCore.Instance.log.info("CommandHandler could not be found.");
            ex.printStackTrace();
        }
        CommandUtils.access = MethodAccess.get(CommandHandler.class);
        try {
            CommandUtils.method = CommandUtils.access.getIndex("registerCommand", ICommand.class, String.class);
        } catch (Exception e) {
            CommandUtils.method = -1;
        }

        isInit = true;
    }

    /**
     * Register a command using its permission node.
     */
    public static void registerCommand(ICommand command, String permNode, boolean enabled) {
        if(!isInit)
            init();

        if (!enabled || command == null)
            return;
        if (permNode.trim().isEmpty()) {
            permNode = command.getClass().getName();
        }

        if (CommandUtils.method == -1) {
            CommandUtils.commandHandler.registerCommand(command);
        } else {
            CommandUtils.access.invoke(CommandUtils.commandHandler, CommandUtils.method, command, permNode);
        }
    }

    /**
     * Registers a command.
     */
    public static void registerCommand(ICommand command, boolean enabled) {
        if (command == null) return;
        String permNode;
        if (command.getClass().isAnnotationPresent(Command.class)) {
            permNode = command.getClass().getAnnotation(Command.class).permission();
        } else {
            permNode = command.getClass().getName();
        }
        registerCommand(command, permNode, enabled);
    }

    /**
     * Registers a command.
     */
    public static void registerCommand(ICommand command) {
        registerCommand(command, true);
    }
}
