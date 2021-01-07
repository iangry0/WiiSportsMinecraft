package i.lovematt.wiisports.arena;

import i.lovematt.wiisports.Core;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Score;

import java.util.List;

public class Timer extends BukkitRunnable {

    private Arena arena;
    private int start;
    private int lobby;
    private int end;

    public Timer(Arena arena) {
        this.arena = arena;
        this.start = arena.getMatchTime();
        this.lobby = 10;
        this.end = 10;
    }

    public void start() {
        this.runTaskTimer(Core.getInstance(), 1000, 20);
    }

    public void reset() {
        this.start = arena.getMatchTime();
        this.lobby = 10;
        this.end = 10;
    }

    @Override
    public void run() {

        if (arena.getState() == GameState.LOBBY) {
            final Score score = Core.getInstance().o.getScore(ChatColor.GREEN + "Starts In:"); //Making a offline player called "Time:" with a green name and adding it to the scoreboard
            score.setScore(lobby);
            lobby--;

            if (lobby <= 5) {
                arena.broadcastMessage(Core.getInstance().msgFile.getStartCountdownMsg(lobby));
            }

            if (lobby == 0) {
                arena.setState(GameState.START);
                arena.teleportToArena();
                arena.givePlayerKits();
            }
        }

        if (arena.getState() == GameState.START) {
            Core.getInstance().o.getScoreboard().resetScores(ChatColor.GREEN + "Starts In:");

            final Score score = Core.getInstance().o.getScore(ChatColor.GREEN + "Ends In:"); //Making a offline player called "Time:" with a green name and adding it to the scoreboard
            score.setScore(start);
            start--;

            if (start == 10) {
                arena.broadcastMessage(Core.getInstance().msgFile.getEndingMsg(start));
            }

            if (start <=5) {
                arena.broadcastMessage(Core.getInstance().msgFile.getEndingMsg(start));
            }

            if (start == 0) {
                arena.setState(GameState.END);
                arena.broadcastMessage(Core.getInstance().msgFile.getEndingTiedMsg());
                arena.setWinner("No one!");
            }
        }

        if (arena.getState() == GameState.END) {
            end--;

            if (end == 5) {

                arena.addPlayerStats();

                List<String> msg = Core.getInstance().msgFile.getGameWinMsg();

                for (String a : msg) {
                    arena.broadcastMessage(a.replaceAll("&", "§").replaceAll("%winner%", arena.getWinner()));

                    //winner reward command
                        String replacedcmd = Core.getInstance().msgFile.rewardcmds().replace("%player%", arena.getWinner());
                        Bukkit.dispatchCommand(Bukkit.getServer().getConsoleSender(), replacedcmd.replace("[", "".replace("]", "")));
                    }
                }

            }

            if (end == 0) {
                arena.reset();
                arena.setState(GameState.IDLE);
            }
        }

    }