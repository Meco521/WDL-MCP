package mcp.me.nixuge.worlddownloader;

import lombok.Getter;
import lombok.Setter;
import mcp.me.nixuge.worlddownloader.events.RenderOverlayEventHandler;
import mcp.wdl.WDL;
import mcp.wdl.config.Configuration;

@Getter
@Setter
public class McMod {
    public static final String MOD_ID = "worlddownloader";
    public static final String NAME = "World Downloader";
    public static final String VERSION = "1.0.6";
    public static final String ORIGINAL_VERSION = "4.1.1.1-SNAPSHOT";
    public static WDL wdl;

    @Getter
    private static McMod instance = new McMod();
    
    private Configuration configuration;
    private String configDirectory;

    public static String getFullVersionString() {
        return VERSION + " (Nixuge/WorldDownloaderForge), " + ORIGINAL_VERSION + " (Pokechu22/WorldDownloader)";
    }

    public void init() {
        //ClientCommandHandler.instance.registerCommand(new ShowNotification());
        //ClientCommandHandler.instance.registerCommand(new ClearNotifications());
        new RenderOverlayEventHandler();
    }
}