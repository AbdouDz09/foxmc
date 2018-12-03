package me.rellynn.foxmc.gameapiv2.games;

import lombok.Getter;
import me.rellynn.foxmc.api.matches.MatchData;
import me.rellynn.foxmc.api.matches.packets.MatchChangeStatePacket;
import me.rellynn.foxmc.api.matches.packets.MatchCreatePacket;
import me.rellynn.foxmc.api.matches.packets.MatchEndPacket;
import me.rellynn.foxmc.api.matches.packets.MatchUpdatePlayersPacket;
import me.rellynn.foxmc.api.protocol.FProtocolManager;
import me.rellynn.foxmc.bukkitapi.api.FoxAPI;
import me.rellynn.foxmc.bukkitapi.utils.BossBarUtils;
import me.rellynn.foxmc.bukkitapi.utils.PacketUtils;
import me.rellynn.foxmc.bukkitapi.utils.PlayerUtils;
import me.rellynn.foxmc.bukkitapi.utils.Scoreboard;
import me.rellynn.foxmc.gameapiv2.GameAPIv2;
import me.rellynn.foxmc.gameapiv2.arenas.Arena;
import me.rellynn.foxmc.gameapiv2.arenas.Team;
import me.rellynn.foxmc.gameapiv2.games.events.*;
import me.rellynn.foxmc.gameapiv2.games.phases.EndingPhase;
import me.rellynn.foxmc.gameapiv2.games.phases.Phase;
import me.rellynn.foxmc.gameapiv2.games.phases.RunningPhase;
import me.rellynn.foxmc.gameapiv2.games.phases.WaitingPhase;
import me.rellynn.foxmc.gameapiv2.menus.GameHotbar;
import me.rellynn.foxmc.gameapiv2.utils.BroadcastType;
import net.minecraft.server.v1_8_R3.Packet;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * Created by gwennaelguich on 27/05/2017.
 * FoxMC Network.
 */
public abstract class Game {
    private static int nextId = 1;
    private static FoxAPI foxAPI = FoxAPI.get();

    private String gameType;
    private String gameTitle;
    @Getter private Arena arena;
    @Getter private MatchData match;
    private GameHotbar hotbar;
    private int phase = -1;
    @Getter private Phase currentPhase;
    private List<Phase> phases = new ArrayList<>();
    private Set<UUID> allowedPlayers = new HashSet<>();
    @Getter private Set<Player> players = new HashSet<>();
    @Getter private Set<Player> spectators = new HashSet<>();
    private Map<UUID, Scoreboard> boards = new HashMap<>();
    private Map<UUID, String> displayNames = new HashMap<>();
    private Map<UUID, Map<String, Float>> givenCurrencies = new HashMap<>();
    private Map<UUID, Map<String, Integer>> givenStatistics = new HashMap<>();
    private float gameBooster;

    public Game(String gameType, String gameTitle, Arena arena) {
        Set<String> filters = new HashSet<>();
        filters.add("game:" + gameType);
        filters.add("map:" + arena.getMap());
        filters.add("mode:" + arena.getMode());
        this.gameType = gameType;
        this.gameTitle = gameTitle;
        this.arena = arena;
        this.match = new MatchData(nextId++, foxAPI.getServerData(), gameTitle, arena.getMap(), "§cStarting", 0, arena.getMaxPlayers(), false, filters);
        this.hotbar = new GameHotbar(this);
        onLoad();
        GameAPIv2.get().getGamesManager().storeGame(this);
    }

    void register() {
        FProtocolManager.get().broadcast(new MatchCreatePacket(match));
        foxAPI.getServerData().addMatch(match);
        foxAPI.getServersHandler().save();
        nextPhase();
    }

    void unregister() {
        FProtocolManager.get().broadcast(new MatchEndPacket(match));
        foxAPI.getServerData().getMatches().remove(match);
        foxAPI.getServersHandler().save();
        destroyAndClean();
    }

    private void destroyAndClean() {
        // Clean memory as much as possible
        hotbar.unregister();
        allowedPlayers.clear();
        players.clear();
        spectators.clear();
        phase = -1;
        currentPhase = null;
        phases.clear();
        boards.forEach((uuid, scoreboard) -> scoreboard.destroy());
        boards.clear();
        displayNames.clear();
        givenCurrencies.clear();
        givenStatistics.keySet().forEach(this::saveStatistics);
    }

    /*
    Phases
     */
    protected abstract void onLoad();

    protected void useDefaultPhases() {
        addPhase(new WaitingPhase());
        addPhase(new RunningPhase());
        addPhase(new EndingPhase());
    }

    protected void addPhase(Phase phase) {
        phase.setGame(this);
        phases.add(phase);
    }

    private void setPhase(int phaseIndex) {
        if (currentPhase != null) {
            currentPhase.disable();
        }
        phase = phaseIndex;
        currentPhase = phaseIndex < phases.size() ? phases.get(phaseIndex) : null;
        if (currentPhase == null) {
            GameAPIv2.get().getGamesManager().removeGame(this);
        } else {
            currentPhase.enable();
            match.setState(currentPhase.getState().getName());
            match.setJoinable(currentPhase.isJoinable());
            FProtocolManager.get().broadcast(new MatchChangeStatePacket(match));
        }
    }

    public boolean isPhase(Class<? extends Phase> phaseClass) {
        return currentPhase != null && currentPhase.getClass() == phaseClass;
    }

    public boolean isState(GameState state) {
        return currentPhase != null && currentPhase.isState(state);
    }

    public void nextPhase() {
        setPhase(++phase);
    }

    public void setPhase(Class<? extends Phase> phaseClass) {
        for (int index = 0; index < phases.size(); index++) {
            if (phases.get(index).getClass() == phaseClass) {
                setPhase(index);
                return;
            }
        }
        throw new IllegalStateException("Phase doesn't exist!");
    }

    public void previousPhase() {
        setPhase(--phase);
    }

    /*
    Players
     */
    public void allowPlayerToJoin(UUID uuid) {
        allowedPlayers.add(uuid);
    }

    public boolean isAllowedToJoin(Player player) {
        return allowedPlayers.contains(player.getUniqueId());
    }

    public boolean isPlayer(Player player) {
        return players.contains(player);
    }

    public boolean isSpectator(Player player) {
        return spectators.contains(player);
    }

    public Set<Player> getAllPlayers() {
        Set<Player> allPlayers = new HashSet<>();
        allPlayers.addAll(spectators);
        allPlayers.addAll(players);
        return allPlayers;
    }

    public boolean contains(Player player) {
        return isAllowedToJoin(player) || isPlayer(player) || isSpectator(player);
    }

    public void destroyScoreboard(Player player) {
        Scoreboard last = boards.remove(player.getUniqueId());
        if (last != null && !last.isDestroyed())
            last.destroy();
    }

    public Scoreboard createScoreboard(Player player, String title) {
        destroyScoreboard(player);
        Scoreboard board = new Scoreboard(player, title);
        boards.put(player.getUniqueId(), board);
        return board;
    }

    public void updateScoreboards(Consumer<Scoreboard> action) {
        boards.forEach((uuid, scoreboard) -> action.accept(scoreboard));
    }

    public Scoreboard getScoreboard(Player player) {
        return boards.get(player.getUniqueId());
    }

    public boolean hasScoreboard(Player player) {
        return boards.containsKey(player.getUniqueId());
    }

    private void createWaitingBoard(Player player) {
        Scoreboard board = createScoreboard(player, "§e" + gameTitle);
        board.setLine(0, "");
        board.setLine(1, "§8» §7Players: §c" + players.size() + "/" + arena.getMaxPlayers());
        board.setLine(2, "§8» §7Map: §b" + arena.getMap());
        board.setLine(3, "");
        board.setLine(4, "§7Waiting players...");
        // Fix the scoreboard width
        board.setLine(5, StringUtils.repeat(" ", 27));
        board.setLine(6, "§eplay.foxmc.net");
    }

    public String getDisplayName(UUID uuid) {
        return displayNames.getOrDefault(uuid, "???");
    }

    public String getDisplayName(Player player) {
        return getDisplayName(player.getUniqueId());
    }

    private void giveAmount(Player player, String currencyId, String currencyDisplay, float amount, String reason) {
        if (amount <= 0.0F) {
            return;
        }
        foxAPI.runAsync(() -> {
            float finalBooster = Math.min(1.5F, gameBooster);
            float newAmount = amount * (1 + finalBooster);
            player.sendMessage(currencyDisplay + " §7+§6" + newAmount + (reason == null ? "" : " §7(§e" + reason + "§7)") + (finalBooster > 0 ? " §7(§dBooster §e" + (int) (finalBooster * 100) + "%§7)" : ""));
            FoxAPI.getPlayer(player).increaseAmount(currencyId, newAmount, (balance, diff, err) -> {
                if (err != null) {
                    player.sendMessage("§cError while adding your coins, please take a screenshot and contact an administrator.");
                    return;
                }
                givenCurrencies.compute(player.getUniqueId(), (uuid, values) -> {
                    if (values == null) {
                        values = new HashMap<>();
                    }
                    values.put(currencyId, values.getOrDefault(currencyId, 0.0F) + newAmount);
                    return values;
                });
            });
        });
    }

    public void giveCoins(Player player, float amount, String reason) {
        giveAmount(player, "coins", "§6FoxCoins", amount, reason);
    }

    public void giveTails(Player player, float amount, String reason) {
        giveAmount(player, "tails", "§aTails", amount, reason);
    }

    private int getGivenAmount(Player player, String currency) {
        Map<String, Float> values = givenCurrencies.get(player.getUniqueId());
        return values != null ? values.getOrDefault(currency, 0.0F).intValue() : 0;
    }

    public int getGivenCoins(Player player) {
        return getGivenAmount(player, "coins");
    }

    public int getGivenTails(Player player) {
        return getGivenAmount(player, "tails");
    }

    public void increaseStatistic(Player player, String stat) {
        if (isPlayer(player)) {
            Map<String, Integer> stats = givenStatistics.computeIfAbsent(player.getUniqueId(), uuid -> new HashMap<>());
            stats.put(stat, stats.getOrDefault(stat, 0) + 1);
        }
    }

    public int getGivenStatistic(UUID uuid, String stat) {
        Map<String, Integer> stats = givenStatistics.get(uuid);
        return stats == null ? 0 : stats.getOrDefault(stat, 0);
    }

    public int getGivenStatistic(Player player, String stat) {
        return getGivenStatistic(player.getUniqueId(), stat);
    }

    public void saveStatistics(UUID uuid) {
        Map<String, Integer> values = givenStatistics.remove(uuid);
        if (values != null) {
            Map<String, Integer> statistics = new HashMap<>();
            values.forEach((statistic, value) -> statistics.put(gameType + "." + statistic, value));
            foxAPI.getPlayersHandler().getPlayer(uuid).batchIncreaseStatistics(statistics);
        }
    }

    public void saveStatistics(Player player) {
        saveStatistics(player.getUniqueId());
    }

    public List<UUID> getTopStatistics(String stat) {
        return givenStatistics.keySet().stream().sorted(Comparator.comparingInt(uuid -> getGivenStatistic((UUID) uuid, stat)).reversed()).collect(Collectors.toList());
    }

    /*
    Game
     */
    private void updatePlayers() {
        match.setPlayers(players.size());
        FProtocolManager.get().broadcast(new MatchUpdatePlayersPacket(match));
        foxAPI.getServersHandler().save();
    }

    private void forcePhase(GameState state) {
        while (phase < phases.size() && !phases.get(phase).isState(state)) {
            phase++;
        }
        setPhase(phase);
    }

    public boolean isUsingTeams() {
        return !arena.getTeams().isEmpty() && arena.getMode().matches("teams|solo");
    }

    public boolean canChooseTeam(Player player) {
        return true;
    }

    public void start() {
        boolean usingTeams = isUsingTeams();
        getAllPlayers().forEach(player -> {
            destroyScoreboard(player);
            increaseStatistic(player, "games_played");
            // Force alone players to join a team
            if (usingTeams && isPlayer(player) && !arena.hasTeam(player)) {
                arena.getBestTeam().join(this, player);
            }
            // Save player display name
            displayNames.put(player.getUniqueId(), player.getDisplayName());
        });
        forcePhase(GameState.RUNNING);
        Bukkit.getPluginManager().callEvent(new GameStartEvent(this));
    }

    public void end() {
        players.forEach(player -> spectators.forEach(player::showPlayer));
        forcePhase(GameState.ENDING);
        Bukkit.getPluginManager().callEvent(new GameEndEvent(this));
    }

    public void broadcast(BroadcastType type, Object value) {
        switch (type) {
            case TITLE:
                getAllPlayers().forEach(player -> PacketUtils.sendTitle(value.toString(), player));
                break;
            case SUBTITLE:
                getAllPlayers().forEach(player -> PacketUtils.sendSubTitle(value.toString(), player));
                break;
            case CHAT:
                getAllPlayers().forEach(player -> player.sendMessage(value.toString()));
                break;
            case PACKET:
                getAllPlayers().forEach(player -> PacketUtils.sendPacket(player, (Packet) value));
                break;
            case SOUND:
                getAllPlayers().forEach(player -> player.playSound(player.getEyeLocation(), (Sound) value, 1.0F, 1.0F));
                break;
            case ACTIONBAR:
                getAllPlayers().forEach(player -> PacketUtils.sendActionBar(value.toString(), player));
                break;
        }
    }

    public void broadcast(String message) {
        broadcast(BroadcastType.CHAT, message);
    }

    private void updateWaitingBoards() {
        boards.values().forEach(scoreboard -> scoreboard.setLine(1, "§8» §7Players: §c" + players.size() + "/" + arena.getMaxPlayers()));
    }

    private void initPlayer(Player player) {
        arena.getTeams().forEach(team -> PacketUtils.sendPacket(player, team.getVirtualTeam().create()));
        if (isState(GameState.WAITING)) {
            createWaitingBoard(player);
        }
    }

    public void setSpectator(Player spectator) {
        if (players.contains(spectator)) {
            leave(spectator, false);
            PlayerUtils.resetPlayer(spectator);
            spectators.forEach(spectator::showPlayer);
        }
        initPlayer(spectator);
        players.forEach(player -> player.hidePlayer(spectator));
        spectators.add(spectator);
        spectator.setGameMode(GameMode.SPECTATOR);
        spectator.setPlayerListName("§o§8[SPEC] §7§o" + spectator.getName());
        spectator.sendMessage("§7You are in §fSPECTATOR §7mode, type §e/leave §7to leave.");
        spectator.sendMessage("§7Only spectators see your messages.");
        Bukkit.getPluginManager().callEvent(new PlayerSpectateGameEvent(this, spectator));
    }

    public void join(Player player) {
        allowedPlayers.remove(player.getUniqueId());
        // Hide every players not in the same game
        Bukkit.getOnlinePlayers().forEach((Consumer<Player>) online -> {
            if (!contains(online)) {
                player.hidePlayer(online);
                online.hidePlayer(player);
            }
        });
        // Join/spectate the game
        if (!isPlayer(player) && (currentPhase == null || !currentPhase.canJoin(player))) {
            setSpectator(player);
        } else {
            initPlayer(player);
            players.add(player);
            spectators.forEach(player::hidePlayer);
            updatePlayers();
            BossBarUtils.setBar(player, "You are playing §l" + gameTitle.toUpperCase() + "§r on §eFOXMC");
            float playerBooster = FoxAPI.getPlayer(player).getRank().getCoinsBooster();
            float newBooster = gameBooster + playerBooster;
            if (newBooster != gameBooster) {
                broadcast("§7[§dBooster§7] " + player.getDisplayName() + " §aincreased the bonus to §e" + (int) (Math.min(1.5F, newBooster) * 100) + "%");
                gameBooster = newBooster;
            }
            if (currentPhase.isState(GameState.WAITING)) {
                hotbar.applyInventory(player);
                broadcast(player.getDisplayName() + " §7has joined! §a(" + players.size() + "/" + arena.getMaxPlayers() + ")");
                updateWaitingBoards();
            } else if (isUsingTeams()) {
                Team team = arena.getPlayerTeam(player);
                if (team != null) {
                    // Join again to send packets/setup display name
                    team.join(this, player);
                }
            }
            Bukkit.getPluginManager().callEvent(new PlayerJoinGameEvent(this, player));
        }
    }

    public void sendToHub(Player player) {
        foxAPI.getNetworkHandler().sendPlayer(player, "failover");
    }

    public void leave(Player player, boolean callEvent) {
        destroyScoreboard(player);
        arena.getTeams().forEach(team -> PacketUtils.sendPacket(player, team.getVirtualTeam().destroy()));
        if (!players.remove(player)) {
            spectators.remove(player);
            player.setPlayerListName(player.getDisplayName());
            player.setGameMode(Bukkit.getDefaultGameMode());
        } else {
            updatePlayers();
            BossBarUtils.removeBar(player);
            if (currentPhase != null && !currentPhase.isState(GameState.ENDING)) {
                // Decrease booster
                float playerBooster = FoxAPI.getPlayer(player).getRank().getCoinsBooster();
                float newBooster = gameBooster - playerBooster;
                if (gameBooster != newBooster) {
                    broadcast("§7[§dBooster§7] §cThe bonus decreased to §e" + (int) (Math.min(1.5F, newBooster) * 100) + "%");
                    gameBooster = newBooster;
                }
                if (currentPhase.isState(GameState.WAITING)) {
                    PlayerUtils.resetPlayer(player);
                    if (isUsingTeams()) {
                        Team team = arena.getPlayerTeam(player);
                        if (team != null)
                            team.leave(this, player);
                    }
                    broadcast(player.getDisplayName() + " §7has left! §c(" + players.size() + "/" + arena.getMaxPlayers() + ")");
                    updateWaitingBoards();
                }
            }
            if (callEvent) {
                Bukkit.getPluginManager().callEvent(new PlayerLeaveGameEvent(this, player));
            }
        }
    }
}
