package org.hark7.fishingPlugin.playerdata;

import org.hark7.fishingPlugin.FishingPlugin;
import org.hark7.fishingPlugin.type.Fishable.*;

import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import static org.bukkit.Bukkit.getLogger;

public class PlayerDataManager {
    private Connection connection;

    public PlayerDataManager(FishingPlugin plugin) {
        try {
            var dbFile = new File(plugin.getDataFolder(), "fishing_plugin.db");
            if (!dbFile.exists()) {
                if (!dbFile.getParentFile().mkdirs()) {
                    getLogger().severe("Failed to create directory for database file.");
                }
                if (!dbFile.createNewFile()) {
                    getLogger().severe("Failed to create database file.");
                }
            }
            connection = DriverManager.getConnection("jdbc:sqlite:" + dbFile.getAbsolutePath());
            try (Statement statement = connection.createStatement()) {
                statement.setQueryTimeout(10);
                createPlayersTable("players", statement);
                createCountsTable("counts", statement);
            } catch (SQLException e) {
                getLogger().severe("Error creating database tables: " + e.getMessage());
            }
        } catch (IOException | SQLException e) {
            getLogger().severe("Error initializing PlayerDataController: " + e.getMessage());
        }
    }

    private void createPlayersTable(String tableName, Statement statement) throws SQLException {
        statement.executeUpdate(
                "CREATE TABLE IF NOT EXISTS " + tableName + " (" +
                "uuid TEXT PRIMARY KEY," +
                "name TEXT NOT NULL," +
                "level INTEGER NOT NULL DEFAULT 0," +
                "exp INTEGER NOT NULL DEFAULT 0" +
                ")"
        );
    }

    private void createCountsTable(String tableName, Statement statement) throws SQLException {
        statement.executeUpdate(
                "CREATE TABLE IF NOT EXISTS " + tableName + " (" +
                "uuid TEXT PRIMARY KEY," +
                "scrap INTEGER NOT NULL DEFAULT 0," +
                "common INTEGER NOT NULL DEFAULT 0," +
                "uncommon INTEGER NOT NULL DEFAULT 0," +
                "rare INTEGER NOT NULL DEFAULT 0," +
                "epic INTEGER NOT NULL DEFAULT 0," +
                "legendary INTEGER NOT NULL DEFAULT 0," +
                "FOREIGN KEY(uuid) REFERENCES players(uuid)" +
                ")"
        );
    }


    public void updatePlayerTableVersion() {
        try (Statement statement = connection.createStatement()) {
            statement.setQueryTimeout(10);
            statement.executeUpdate(
                    "CREATE TABLE IF NOT EXISTS players_new (" +
                            "uuid TEXT PRIMARY KEY," +
                            "name TEXT NOT NULL," +
                            "level INTEGER NOT NULL," +
                            "exp INTEGER NOT NULL" +
                            ")"
            );
            statement.executeUpdate(
                    "INSERT INTO players_new (uuid, name, level, exp) " +
                            "SELECT uuid, name, level, exp FROM players"
            );
            statement.executeUpdate("DROP TABLE players");
            statement.executeUpdate("ALTER TABLE players_new RENAME TO players");
        } catch (SQLException e) {
            getLogger().severe("Error updating players table: " + e.getMessage());
        }
    }

    public void updateCountTableVersion() {
        try (Statement statement = connection.createStatement()) {
            statement.setQueryTimeout(10);
            statement.executeUpdate(
                    "CREATE TABLE IF NOT EXISTS counts_new (" +
                            "uuid TEXT PRIMARY KEY," +
                            "scrap INTEGER NOT NULL DEFAULT 0," +
                            "common INTEGER NOT NULL DEFAULT 0," +
                            "uncommon INTEGER NOT NULL DEFAULT 0," +
                            "rare INTEGER NOT NULL DEFAULT 0," +
                            "epic INTEGER NOT NULL DEFAULT 0," +
                            "legendary INTEGER NOT NULL DEFAULT 0," +
                            "FOREIGN KEY(uuid) REFERENCES players(uuid)" +
                            ")"
            );
            statement.executeUpdate(
                    "INSERT INTO counts_new (uuid, scrap, common, uncommon, rare, epic, legendary) " +
                            "SELECT uuid, scrap, common, uncommon, rare, epic, legendary FROM counts"
            );
            statement.executeUpdate("DROP TABLE counts");
            statement.executeUpdate("ALTER TABLE counts_new RENAME TO counts");
        } catch (SQLException e) {
            getLogger().severe("Error updating counts table: " + e.getMessage());
        }
    }

    public synchronized PlayerData getPlayerData(UUID playerUUID) {
        String sql = "SELECT p.uuid, p.name, p.level, p.exp, " +
                     "c.scrap, c.common, c.uncommon, c.rare, c.epic, c.legendary " +
                     "FROM players p LEFT JOIN counts c ON p.uuid = c.uuid WHERE p.uuid = ?";
        var count = new ConcurrentHashMap<Rarity, Integer>();
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, playerUUID.toString());
            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    count.put(Rarity.SCRAP, rs.getInt("scrap"));
                    count.put(Rarity.COMMON, rs.getInt("common"));
                    count.put(Rarity.UNCOMMON, rs.getInt("uncommon"));
                    count.put(Rarity.RARE, rs.getInt("rare"));
                    count.put(Rarity.EPIC, rs.getInt("epic"));
                    count.put(Rarity.LEGENDARY, rs.getInt("legendary"));
                    return new PlayerData(
                            rs.getString("name"),
                            rs.getInt("level"),
                            rs.getInt("exp"),
                            count
                    );
                }
            }
        } catch (SQLException e) {
            getLogger().severe("Error retrieving counts for player " + playerUUID + ": " + e.getMessage());
        }
        return null;
    }

    public synchronized Map<UUID, PlayerData> getPlayerDataMap() {
        Map<UUID, PlayerData> playerDataMap = new ConcurrentHashMap<>();
        String sql = "SELECT p.uuid, p.name, p.level, p.exp, " +
                     "c.scrap, c.common, c.uncommon, c.rare, c.epic, c.legendary " +
                     "FROM players p LEFT JOIN counts c ON p.uuid = c.uuid";
        try (Statement statement = connection.createStatement();
             ResultSet rs = statement.executeQuery(sql)) {
            while (rs.next()) {
                UUID playerUUID = UUID.fromString(rs.getString("uuid"));
                var count = new ConcurrentHashMap<Rarity, Integer>();
                count.put(Rarity.SCRAP, rs.getInt("scrap"));
                count.put(Rarity.COMMON, rs.getInt("common"));
                count.put(Rarity.UNCOMMON, rs.getInt("uncommon"));
                count.put(Rarity.RARE, rs.getInt("rare"));
                count.put(Rarity.EPIC, rs.getInt("epic"));
                count.put(Rarity.LEGENDARY, rs.getInt("legendary"));
                PlayerData data = new PlayerData(
                        rs.getString("name"),
                        rs.getInt("level"),
                        rs.getInt("exp"),
                        count
                );
                playerDataMap.put(playerUUID, data);
            }
        } catch (SQLException e) {
            getLogger().severe("Error retrieving player data: " + e.getMessage());
        }
        return Map.copyOf(playerDataMap);
    }

    public synchronized void setPlayerData(UUID playerUUID, PlayerData playerData) {
        String playerSql =
                "INSERT INTO players (uuid, name, level, exp) VALUES (?, ?, ?, ?) " +
                "ON CONFLICT(uuid) DO UPDATE SET name=excluded.name, level=excluded.level, exp=excluded.exp";
        String countSql =
                "INSERT INTO counts (uuid, scrap, common, uncommon, rare, epic, legendary) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?) " +
                "ON CONFLICT(uuid) DO UPDATE SET " +
                "scrap=excluded.scrap, common=excluded.common, " +
                "uncommon=excluded.uncommon, rare=excluded.rare, " +
                "epic=excluded.epic, legendary=excluded.legendary";
        try (PreparedStatement playerPs = connection.prepareStatement(playerSql);
             PreparedStatement countPs = connection.prepareStatement(countSql)) {
            playerPs.setString(1, playerUUID.toString());
            playerPs.setString(2, playerData.playerName());
            playerPs.setInt(3, playerData.level());
            playerPs.setInt(4, playerData.exp());
            playerPs.executeUpdate();

            var count = playerData.getCounts();
            countPs.setString(1, playerUUID.toString());
            countPs.setInt(2, count.getOrDefault(Rarity.SCRAP, 0));
            countPs.setInt(3, count.getOrDefault(Rarity.COMMON, 0));
            countPs.setInt(4, count.getOrDefault(Rarity.UNCOMMON, 0));
            countPs.setInt(5, count.getOrDefault(Rarity.RARE, 0));
            countPs.setInt(6, count.getOrDefault(Rarity.EPIC, 0));
            countPs.setInt(7, count.getOrDefault(Rarity.LEGENDARY, 0));
            countPs.executeUpdate();
        } catch (SQLException e) {
            getLogger().severe("Error saving player data: " + e.getMessage());
        }
    }

    public synchronized void setPlayerExp(UUID playerUUID, int exp) {
        String sql = "UPDATE players SET exp = ? WHERE uuid = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, exp);
            ps.setString(2, playerUUID.toString());
            ps.executeUpdate();
        } catch (SQLException e) {
            getLogger().severe("Error updating experience for player " + playerUUID + ": " + e.getMessage());
        }
    }

    public synchronized void setPlayerLevel(UUID playerUUID, int level) {
        String sql = "UPDATE players SET level = ? WHERE uuid = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, level);
            ps.setString(2, playerUUID.toString());
            ps.executeUpdate();
        } catch (SQLException e) {
            getLogger().severe("Error updating level for player " + playerUUID + ": " + e.getMessage());
        }
    }

    public synchronized void setPlayerCount(UUID playerUUID, Rarity rarity, int count) {
        String sql = "UPDATE counts SET " + getRarityColumnName(rarity) + " = ? WHERE uuid = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, count);
            ps.setString(2, playerUUID.toString());
            ps.executeUpdate();
        } catch (SQLException e) {
            getLogger().severe("Error updating count for player " + playerUUID + ": " + e.getMessage());
        }
    }

    private String getRarityColumnName(Rarity rarity) {
        return switch (rarity) {
            case SCRAP -> "scrap";
            case COMMON -> "common";
            case UNCOMMON -> "uncommon";
            case RARE -> "rare";
            case EPIC -> "epic";
            case LEGENDARY -> "legendary";
        };
    }

    public synchronized void close() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                getLogger().severe("Error closing database connection: " + e.getMessage());
            }
        }
    }
}
