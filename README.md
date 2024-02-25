# WorldDownloader-MCP 1.8.9
This is the MCP (ModCodePack) version of WorldDownloader, which currently only supports Minecraft 1.8.9.

# Usage

#### When initializing your client, call `McMod.getInstance().init();`

#### EntityRenderer.java:
    EventManager.call(new EventGame2DRender(partialTicks));
    ......>
    this.frameFinish();

#### GuiIngameMenu.java:

    public void initGui() {
        this.buttonList.clear();
        WDLHooks.injectWDLButtons(this, buttonList, new AddButtonCallback(this.buttonList));
        ......
    }

    protected void actionPerformed(GuiButton button) throws IOException {
        WDLHooks.handleWDLButtonClick(this, button);
        ......
    }

#### InventoryBasic.java:
    implements INetworkNameable

    private String networkCustomName;
        
    public InventoryBasic(IChatComponent title, int slotCount)
    {
        this(title.getUnformattedText(), true, slotCount);
        if (title instanceof ChatComponentText) {
            this.networkCustomName = title.getFormattedText();
        }
    }
    
    @Override
    public String getCustomDisplayName() {
        return networkCustomName;
    }

#### NetHandlerPlayClient.java:
    implements IBaseChangesApplied

    public NetHandlerPlayClient(Minecraft mcIn, GuiScreen p_i46300_2_, NetworkManager networkManagerIn, GameProfile p_i46300_4_)
    {
        ......
        if (networkManagerIn == null) return;

        Channel channel = ReflectionUtils.findAndGetPrivateField(networkManagerIn, NetworkManager.class, Channel.class);
        if (channel.pipeline().names().contains("wdl:packet_handler")) {
        } else if (channel.pipeline().names().contains("fml:packet_handler")) {
            channel.pipeline().addBefore("fml:packet_handler", "wdl:packet_handler",
                    new PassCustomPayloadHandler(mcIn, (NetHandlerPlayClient)(Object)this, true));
        } else {
            channel.pipeline().addBefore("packet_handler", "wdl:packet_handler",
                    new PassCustomPayloadHandler(mcIn, (NetHandlerPlayClient)(Object)this, false));
        }
    }
    
    public void onDisconnect(IChatComponent reason)
    {
        WDLHooks.onNHPCDisconnect((NetHandlerPlayClient)(Object)this, reason);
        ......
    }
    
    public void handleChat(S02PacketChat packetIn)
    {
        WDLHooks.onNHPCHandleChat(this, packetIn);
        ......
    }
    
    public void handleBlockAction(S24PacketBlockAction packetIn)
    {
        WDLHooks.onNHPCHandleBlockAction(this, packetIn);
        ......
    }
    
    
    public void handleMaps(S34PacketMaps packetIn)
    {
        WDLHooks.onNHPCHandleMaps(this, packetIn);
        ......
    }
    
    public void handleCustomPayload(S3FPacketCustomPayload packetIn)
    {
        WDLHooks.onNHPCHandleCustomPayload((NetHandlerPlayClient)(Object)this, packetIn);
    }

#### WorldClient.java:
    implements IBaseChangesApplied
    
    private NotificationManager notificationManager = NotificationManager.getInstance();

    public void tick()
    {
        WDLHooks.onWorldClientTick(this);
        notificationManager.update();
        ......
    }
    
    public Entity removeEntityFromWorld(int entityID)
    {
        WDLHooks.onWorldClientRemoveEntityFromWorld(this, entityID);
    }

#### CrashReport.java:
    implements IBaseChangesApplied
    
    private void populateEnvironment()
    {
        try {
            WDLHooks.onCrashReportPopulateEnvironment(this);
        } catch (Throwable t) {
            try {
                final Logger LOGGER = LogManager.getLogger();
                LOGGER.fatal("World Downloader: Failed to add crash info", t);
                this.getCategory().addCrashSectionThrowable("World Downloader - Fatal error in crash handler (see log)", t);
            } catch (Throwable t2) {
                System.err.println("WDL: Double failure adding info to crash report!");
                t.printStackTrace();
                t2.printStackTrace();
            }
        }
    }