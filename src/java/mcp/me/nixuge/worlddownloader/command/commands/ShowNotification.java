package mcp.me.nixuge.worlddownloader.command.commands;


import mcp.me.nixuge.worlddownloader.command.AbstractCommand;
import mcp.me.nixuge.worlddownloader.command.MessageBuilder;
import mcp.wdl.gui.notifications.Level;
import mcp.wdl.gui.notifications.Notification;
import mcp.wdl.gui.notifications.NotificationManager;
import net.minecraft.command.ICommandSender;

import java.util.ArrayList;
import java.util.List;

public class ShowNotification extends AbstractCommand {

    public ShowNotification() {
        super("sn");
    }

    @Override
    public List<String> getCommandAliases() {
        ArrayList<String> al = new ArrayList<>();
        al.add("shownotification");
        return al;
    }

    @Override
    public void onCommand(final ICommandSender sender, final String[] args) {
        String finalStr = "";
        for (int i = 0; i < args.length; i++) {
            if (i == 0) continue;
            finalStr += args[i] + " ";
        }
        for (Level lvl : Level.values()) {
            if (lvl.toString().toLowerCase().equals(args[0].toLowerCase())) {
                tell(new MessageBuilder("No notification of type " + lvl.toString() + " sent."));
                NotificationManager.getInstance().addNotification(new Notification(lvl, finalStr, 200000));
                return;
            }
        }
        tell(new MessageBuilder("No notification type specified"));
    }
}
