package me.rellynn.foxmc.api.players.database;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import me.rellynn.foxmc.api.CoreAPI;
import me.rellynn.foxmc.api.players.Rank;
import me.rellynn.foxmc.api.players.packets.PlayerNickChangePacket;
import me.rellynn.foxmc.api.players.packets.PlayerSetAmountPacket;
import me.rellynn.foxmc.api.players.packets.PlayerSetDataPacket;
import me.rellynn.foxmc.api.players.packets.PlayerSetRankPacket;
import me.rellynn.foxmc.api.protocol.FProtocolManager;
import me.rellynn.foxmc.api.utils.Callback;
import me.rellynn.foxmc.api.utils.FinancialCallback;
import org.javalite.activejdbc.Model;
import org.javalite.activejdbc.annotations.Table;
import redis.clients.jedis.Jedis;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by gwennaelguich on 06/04/2017.
 * FoxMC Network.
 */
@Table(value = "players")
public class PlayerData extends Model {
    private static final JsonParser JSON_PARSER = new JsonParser();

    public void setValues(Object[] namesAndValues, Callback<Void> successCb, Callback<Throwable> errorCb) {
        Object[] oldNamesAndValues = new Object[namesAndValues.length];
        int i = 0;
        while (i < namesAndValues.length) {
            String key = String.valueOf(namesAndValues[i]);
            oldNamesAndValues[i++] = key;
            oldNamesAndValues[i++] = get(key);
        }
        set(namesAndValues);
        CoreAPI.get().getSqlManager().execute(() -> {
            try {
                if (!saveIt()) {
                    // Throw an error to rollback data
                    throw new Exception("Unable to save player!");
                } else if (successCb != null) {
                    successCb.done(null);
                }
            } catch (Throwable ex) {
                CoreAPI.get().getLogger().severe("Got an error while updating player data:");
                ex.printStackTrace();
                // Rollback with old values
                set(oldNamesAndValues);
                if (errorCb != null) {
                    errorCb.done(ex);
                }
            }
        });
    }

    private void setJsonElements(String attributeName, Map<String, JsonElement> elements, Callback<Void> successCb, Callback<Throwable> errorCb) {
        JsonObject data = JSON_PARSER.parse(getString(attributeName)).getAsJsonObject();
        for (Map.Entry<String, JsonElement> entry : elements.entrySet()) {
            JsonObject current = data;
            String key = entry.getKey();
            String[] parts = key.split("\\.");
            String[] newParts = Arrays.copyOfRange(parts, 0, parts.length - 1);
            for (String part : newParts) {
                JsonElement child = current.get(part);
                if (child == null || !child.isJsonObject()) {
                    child = new JsonObject();
                    current.add(part, child);
                }
                current = child.getAsJsonObject();
            }
            String prop = parts[parts.length - 1];
            if (entry.getValue() == null) {
                current.remove(prop);
            } else {
                current.add(prop, entry.getValue());
            }
        }
        setValues(new Object[]{attributeName, data.toString()}, successCb, errorCb);
    }

    private void setJsonElement(String attributeName, String key, JsonElement value, Callback<Void> successCb, Callback<Throwable> errorCb) {
        setJsonElements(attributeName, new HashMap<String, JsonElement>() {{
            put(key, value);
        }}, successCb, errorCb);
    }

    private JsonElement getJsonElement(JsonElement current, String key) {
        if (current == null) {
            return null;
        }
        String[] parts = key.split("\\.");
        for (String part : parts) {
            if (!current.isJsonObject() || !current.getAsJsonObject().has(part)) {
                return null;
            }
            current = current.getAsJsonObject().get(part);
        }
        return current;
    }

    private JsonElement getJsonElement(String attributeName, String key) {
        return getJsonElement(JSON_PARSER.parse(getString(attributeName)), key);
    }

    public Rank getRank() {
        for (Rank value : Rank.values()) {
            if ((!value.isMod() && getVIPLevel() >= value.getLevel()) || (value.isMod() && getModLevel() >= value.getLevel())) {
                return value;
            }
        }
        return Rank.DEFAULT;
    }

    public void setRank(Rank rank) {
        Object[] updateData;
        if (rank.isMod()) {
            updateData = new Object[]{"mod_level", rank.getLevel()};
        } else {
            set("mod_level", 0);
            updateData = new Object[]{
                    "mod_level", 0,
                    "vip_level", rank.getLevel()
            };
        }
        setValues(updateData, value -> FProtocolManager.get().broadcast(new PlayerSetRankPacket(getUUID(), rank)), null);
    }

    public boolean isAtLeast(Rank value) {
        Rank rank = getRank();
        return rank.isMod() && !value.isMod() || (rank.isMod() == value.isMod() && rank.getLevel() >= value.getLevel());
    }

    public UUID getUUID() {
        return UUID.fromString(getId() + "");
    }

    public String getName() {
        return getString("name");
    }

    public String getNickName() {
        return getString("nickname");
    }

    public void setNickName(String nickName) {
        Object[] updateData = new Object[]{"nickname", nickName};
        setValues(updateData, value -> FProtocolManager.get().broadcast(new PlayerNickChangePacket(getUUID(), nickName)), null);
    }

    public boolean hasNick() {
        return getNickName() != null && !getName().equals(getNickName());
    }

    public String getDisplayName() {
        return (hasNick() ? Rank.FOX_PLUS.getPrefix() + getNickName() : getRank().getPrefix() + getName()) + "§r";
    }

    public int getVIPLevel() {
        return getInteger("vip_level");
    }

    public int getModLevel() {
        return getInteger("mod_level");
    }

    public Date getFirstLogin() {
        Long firstLogin = getLong("first_login");
        return new Date(firstLogin == null ? System.currentTimeMillis() : firstLogin);
    }

    public Date getLastLogin() {
        Long lastLogin = getLong("last_login");
        return new Date(lastLogin == null ? System.currentTimeMillis() : lastLogin);
    }

    public String getLastIP() {
        return getString("last_ip");
    }

    public boolean hasVotedToday() {
        Long lastVote = getLong("last_vote");
        return lastVote != null && System.currentTimeMillis() - lastVote <= 86400000;
    }

    public BanEntry getLastBan() {
        List<BanEntry> bans = BanEntry.find("uuid = ?", getId() + "").orderBy("date DESC").limit(1);
        return bans.isEmpty() ? null : bans.get(0);
    }

    public List<BanEntry> getBans() {
        return BanEntry.find("uuid = ?", getId() + "").orderBy("date ASC");
    }

    public List<KickEntry> getKicks() {
        return KickEntry.find("uuid = ?", getId() + "").orderBy("date ASC");
    }

    public MuteEntry getLastMute() {
        List<MuteEntry> mutes = MuteEntry.find("uuid = ?", getId() + "").orderBy("date DESC").limit(1);
        return mutes.isEmpty() ? null : mutes.get(0);
    }

    public List<MuteEntry> getMutes() {
        return MuteEntry.find("uuid = ?", getId() + "").orderBy("date ASC");
    }

    /*
    Currencies
     */
    public int getAmount(String id) {
        return getInteger(id);
    }

    public int getCoins() {
        return getAmount("coins");
    }

    public int getTails() {
        return getAmount("tails");
    }

    public void increaseAmount(String currency, float amount, FinancialCallback<Float> callback) {
        float balance = getFloat(currency) + amount;
        Object[] updateData = new Object[]{currency, balance};
        setValues(updateData, value -> {
            if (callback != null) {
                callback.done(balance, amount, null);
            }
            FProtocolManager.get().broadcast(new PlayerSetAmountPacket(getUUID(), currency, getFloat(currency)));
        }, ex -> {
            if (callback != null) {
                callback.done(balance, amount, ex);
            }
        });
    }

    public void decreaseAmount(String id, float amount, FinancialCallback<Float> callback) {
        increaseAmount(id, -amount, callback);
    }

    public void increaseCoins(float amount, FinancialCallback<Float> callback) {
        increaseAmount("coins", amount, callback);
    }

    public void decreaseCoins(float amount, FinancialCallback<Float> callback) {
        decreaseAmount("coins", amount, callback);
    }

    public void increaseTails(float amount, FinancialCallback<Float> callback) {
        increaseAmount("tails", amount, callback);
    }

    public void decreaseTails(float amount, FinancialCallback<Float> callback) {
        decreaseAmount("tails", amount, callback);
    }

    /*
    Statistics
     */
    public int getStatistic(String statistic) {
        JsonElement element = getJsonElement("stats", statistic);
        return element == null ? 0 : element.getAsInt();
    }

    public void batchIncreaseStatistics(Map<String, Integer> stats) {
        Map<String, JsonElement> elements = new HashMap<>();
        JsonElement values = JSON_PARSER.parse(getString("stats"));
        stats.forEach((statistic, incrBy) -> {
            JsonElement value = getJsonElement(values, statistic);
            elements.put(statistic, new JsonPrimitive(value == null ? incrBy : value.getAsInt() + incrBy));
        });
        setJsonElements("stats", elements, value -> FProtocolManager.get().broadcast(new PlayerSetDataPacket(getUUID(), "stats", getString("stats"))), null);
    }

    public void increaseStatistic(String statistic, int incrBy) {
        batchIncreaseStatistics(new HashMap<String, Integer>() {{
            put(statistic, incrBy);
        }});
    }

    public void increaseStatistic(String statistic) {
        increaseStatistic(statistic, 1);
    }

    /*
    Shops
     */
    public int getShopLevel(String shopId, String itemId) {
        JsonElement item = getJsonElement("shop", shopId + "." + itemId);
        return item == null ? 0 : item.getAsInt();
    }

    public void setShopLevel(String shopId, String itemId, int level) {
        setJsonElement("shop", shopId + "." + itemId, new JsonPrimitive(level), value -> FProtocolManager.get().broadcast(new PlayerSetDataPacket(getUUID(), "shop", getString("shop"))), null);
    }

    public boolean hasShopItem(String shopId, String itemId) {
        return getShopLevel(shopId, itemId) > 0;
    }

    public boolean isCurrentItem(String shopId, String itemId) {
        return itemId.equals(getCurrentItem(shopId));
    }

    public String getCurrentItem(String shopId) {
        JsonElement current = getJsonElement("shop", shopId + ".current");
        return current == null ? null : current.getAsString();
    }

    public void setCurrentItem(String shopId, String itemId) {
        setJsonElement("shop", shopId + ".current", itemId == null ? null : new JsonPrimitive(itemId), value -> FProtocolManager.get().broadcast(new PlayerSetDataPacket(getUUID(), "shop", getString("shop"))), null);
    }

    /*
    Custom data
     */
    public JsonElement getCustomData(String key) {
        return getJsonElement("data", key);
    }

    public void setCustomData(String key, JsonElement value) {
        setJsonElement("data", key, value, value1 -> FProtocolManager.get().broadcast(new PlayerSetDataPacket(getUUID(), "data", getString("data"))), null);
    }

    /*
    Settings
     */
    public void setSetting(String name, String value) {
        setJsonElement("settings", name, new JsonPrimitive(value), value1 -> FProtocolManager.get().broadcast(new PlayerSetDataPacket(getUUID(), "settings", getString("settings"))), null);
    }

    public String getSetting(String name, String def) {
        JsonElement element = getJsonElement("settings", name);
        return element == null ? def : element.getAsString();
    }

    public String getSetting(String name) {
        return getSetting(name, null);
    }

    public boolean isSetting(String name, String value) {
        return value.equals(getSetting(name));
    }

    public boolean isSetting(String name, String value, String def) {
        return value.equals(getSetting(name, def));
    }

    /*
    Friends
     */
    public Set<UUID> getFriends() {
        String id = getId() + "";
        return FriendEntry.where("friend_one=? or friend_two=?", id, id).stream().map(model -> {
            String opposite = model.getString("friend_one").equals(id) ? "friend_two" : "friend_one";
            return UUID.fromString(model.getString(opposite));
        }).collect(Collectors.toSet());
    }

    public boolean hasFriend(UUID uuid) {
        String id = getId() + "";
        return FriendEntry.where("(friend_one=? and friend_two=?) or (friend_one=? and friend_two=?)", uuid + "", id, id, uuid + "").size() > 0;
    }

    public boolean removeFriend(UUID uuid) {
        String id = getId() + "";
        return FriendEntry.delete("(friend_one=? and friend_two=?) or (friend_one=? and friend_two=?)", uuid + "", id, id, uuid + "") > 0;
    }

    public Set<UUID> getFriendRequests() {
        try (Jedis jedis = CoreAPI.get().getRedisManager().getResource()) {
            Set<String> keys = jedis.keys("friendrequests:*:" + getId() + "");
            return keys.stream().map(s -> UUID.fromString(s.split(":")[1])).collect(Collectors.toSet());
        }
    }

    public boolean declineFriendRequest(UUID uuid) {
        try (Jedis jedis = CoreAPI.get().getRedisManager().getResource()) {
            return jedis.del("friendrequests:" + uuid + ":" + getId()) > 0;
        }
    }

    public boolean acceptFriendRequest(UUID uuid) {
        try (Jedis jedis = CoreAPI.get().getRedisManager().getResource()) {
            if (jedis.del("friendrequests:" + uuid + ":" + getId()) > 0) {
                new FriendEntry(getUUID(), uuid);
                return true;
            }
            return false;
        }
    }
}
