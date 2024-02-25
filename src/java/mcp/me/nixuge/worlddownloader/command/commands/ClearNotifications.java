package mcp.me.nixuge.worlddownloader.command.commands;

import mcp.me.nixuge.worlddownloader.command.AbstractCommand;
import mcp.me.nixuge.worlddownloader.command.MessageBuilder;
import mcp.wdl.gui.notifications.NotificationManager;
import net.minecraft.command.ICommandSender;

import java.util.ArrayList;
import java.util.List;

public class ClearNotifications extends AbstractCommand {

    public ClearNotifications() {
        super("cn");
    }

    @Override
    public List<String> getCommandAliases() {
        ArrayList<String> al = new ArrayList<>();
        al.add("clearnotifications");
        return al;
    }

    @Override
    public void onCommand(final ICommandSender sender, final String[] args) {
        NotificationManager.getInstance().removeAll();
        tell(new MessageBuilder("Notifications cleared."));
    }
}
