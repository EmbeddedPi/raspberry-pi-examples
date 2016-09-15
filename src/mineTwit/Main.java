/******************************************************************************
 * Copyright (c) 2014 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 *    EmbeddedPi - Converted scope of usage to Minecraft server notification
 *****************************************************************************/
package mineTwit;

import java.text.DecimalFormat;
import java.util.Date;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
//import org.bukkit.event.block.BlockPlaceEvent;
//import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityTameEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
//import org.bukkit.event.player.PlayerFishEvent;
//import org.bukkit.event.player.PlayerKickEvent;
//import org.bukkit.event.player.PlayerTeleportEvent;
//import org.bukkit.event.vehicle.VehicleEnterEvent;
import org.bukkit.Location;
//import org.bukkit.Material;
//import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
//import org.bukkit.entity.Vehicle;
import org.bukkit.entity.LivingEntity;

public class Main extends JavaPlugin implements Listener {

  private String localMessage = "";
  private String recentPlayer = "";
  private String recentPlayerIP = "";
  private Location recentPlayerLocation;
  private String locationMessage = "";
  private boolean recentJoin = false;
  private String[] exemptionList = {"Banana_Skywalker", "JeannieInABottle"}; 
  private static final String entryMessage = "Server's up, time to get crafting!";
  private static final String exitMessage = "The server has joined the choir invisibule.";
  private static final boolean TWITTER_CONFIGURED = false;
  private static final String API_KEY = "XXXX";
  private static final String API_SECRET = "YYYY";
  private static final String token = "ZZZ";
  private static final String secret = "ABABAB";
  private static Twitter twitter;
  private class notificationList {
    String type;
    boolean status;
  }
  
  @Override
  public void onEnable() {
    // Register listener
    getServer().getPluginManager().registerEvents(this, this);
    // Set up Twitter
    try {
      twitter = setupTwitter();
      updateStatus(twitter, entryMessage);
      } catch (TwitterException e) {
      getLogger().info("Twitter is broken because of " + e);
      } finally {
      getLogger().info("mineTwit goes tweet tweet");
      }
    initialiseNotifications();
  }
  
  @Override
  public void onDisable() {
    // Server down notification
    try {
      twitter = setupTwitter();
      updateStatus(twitter, exitMessage);
      } catch (TwitterException e) {
      getLogger().info("Twitter is broken because of " + e);
      } finally {
      getLogger().info("mineTwit has fallen off the perch");
      }
  }
  
  @EventHandler
  public void onLogin(PlayerJoinEvent event) throws Exception {
    // if (myNotifications[0].status) {
      recentJoin = true;
      recentPlayer = event.getPlayer().getName();
      recentPlayerIP = event.getPlayer().getAddress().getHostString();
      recentPlayerLocation = event.getPlayer().getLocation();
      locationMessage = parseLocation(recentPlayerLocation);
      localMessage = setLocalMessage(recentJoin);
      getLogger().info(locationMessage);
      updateStatus(twitter, recentPlayer + " flew in." + localMessage + "\n" + locationMessage);
      localMessage = "";
    /* } else {
      return;
    } */
  }
  
  @EventHandler
  public void onLogout (PlayerQuitEvent event) throws Exception {
    // if (myNotifications[0].status) {
      recentJoin = false;
      recentPlayer = event.getPlayer().getName();
      recentPlayerIP = event.getPlayer().getAddress().getHostString();
      recentPlayerLocation = event.getPlayer().getLocation();
      locationMessage = parseLocation(recentPlayerLocation);
      localMessage = setLocalMessage(recentJoin);
      getLogger().info(locationMessage);
      updateStatus(twitter, recentPlayer + " flew away." + localMessage + "\n" + locationMessage);
      localMessage = "";
    /*  else {
      return;
    } */
  }
  
//TODO Sort this out
@SuppressWarnings("unused")
  private boolean onCommand(CommandSender sender, Command cmd, String label, String[] args, notificationList[] myNotifications) {    
    if (cmd.getName().equalsIgnoreCase("setNotification")) { 
      // Check a single argument for IPAddress
      if (args.length < 2) {
        sender.sendMessage("This needs two arguments!");
            return false;
        } else if (args.length >2) {
          sender.sendMessage("Calm down, too many arguments!");
            return false;
        } else {
      // output label to check it's OK
      sender.sendMessage("label is " + label); 
      sender.sendMessage("args[0] is " + args[0]);
      sender.sendMessage("args[1] is " + args[1]);      
      sender.sendMessage("Sent by " + sender);
      // Check first argument is a valid command
      // Check second argument is valid boolean
      return true;}
    } else if (cmd.getName().equalsIgnoreCase("listNotification")) {
      sender.sendMessage("Number of types is " + myNotifications.length);
      sender.sendMessage(myNotifications[0].type + "   " + String.valueOf(myNotifications[0].status));
      sender.sendMessage(myNotifications[1].type + "   " + String.valueOf(myNotifications[1].status));
      sender.sendMessage(myNotifications[2].type + "   " + String.valueOf(myNotifications[2].status));
      sender.sendMessage(myNotifications[3].type + "   " + String.valueOf(myNotifications[3].status));
      sender.sendMessage(myNotifications[4].type + "   " + String.valueOf(myNotifications[4].status));
      sender.sendMessage(myNotifications[5].type + "   " + String.valueOf(myNotifications[5].status));
      sender.sendMessage(myNotifications[6].type + "   " + String.valueOf(myNotifications[6].status));
      sender.sendMessage(myNotifications[7].type + "   " + String.valueOf(myNotifications[7].status));
      return true;
    } else {
      getLogger().info("Gibberish or a typo, either way it ain't happening");
    return false; 
    }
  }
    
 private void initialiseNotifications() {
  notificationList[] myNotifications = new notificationList[8]; 
  for (int i=0; i<myNotifications.length; i++) {
    myNotifications[i]= new notificationList(); 
  }
  // Set defaults
  myNotifications[0].type = "loggingInOut";
  myNotifications[0].status = true;
  myNotifications[1].type= "blockPlacing";
  // Set to false as will overload twitter update limits if building
  myNotifications[1].status = false;
  myNotifications[2].type= "dying";
  myNotifications[2].status = true;
  myNotifications[3].type= "taming";
  myNotifications[3].status = true;
  myNotifications[4].type= "fishing";
  myNotifications[4].status = true;
  myNotifications[5].type= "kicking";
  myNotifications[5].status = true;
  myNotifications[6].type= "teleporting";
  myNotifications[6].status = true;
  myNotifications[7].type= "enteringVehicle"; 
  myNotifications[7].status = true;
 }
  
 /*
  @EventHandler
  public void onBlockPlace(BlockPlaceEvent event) {
    // if (myNotifications[1].status) {
      Player player = event.getPlayer();
      Block block = event.getBlock();
      Material mat = block.getType();
      // Tweet who placed which block.
      updateStatus(twitter, player.getName() + " placed a block of " + mat.toString().toLowerCase() + ".");
    // } else {
    //  return;
    // } 
  }
  
  @EventHandler
  public void onDeath (final EntityDeathEvent event) {
    if (myNotifications[2].status) {
      if (!(event.getEntity() instanceof Player)) {
      updateStatus(twitter, "Something kicked the bucket.");
      } else {
        final Player player = (Player)event.getEntity();
        updateStatus(twitter, player.getName() + " kicked the bucket.");
      }
     } else {
      return;
    } 
  }
 */
  
  @EventHandler
  public void onEntityTame (final EntityTameEvent event) {
    // if (myNotifications[3].status) {
      final Player player = (Player)event.getOwner();
      final LivingEntity entity = (LivingEntity)event.getEntity();
      updateStatus(twitter, player.getName() + " tamed a " + entity.getCustomName());
    //} else {
    //  return;
    //}
  }
  
  /*
  @EventHandler
  public void onFishing (final PlayerFishEvent event) {
    if (myNotifications[4].status) {
        final Player player = (Player)event.getPlayer();
        updateStatus(twitter, player.getName() + " went fishing.");
    } else {
      return;
    }
  }
  
  @EventHandler
  public void onPlayerKick (final PlayerKickEvent event) {
    if (myNotifications[5].status) {
      final Player player = (Player)event.getPlayer();
      updateStatus(twitter, player.getName() + " was unceremoniously booted off.");
    } else {
      return;
    }
  }
  
  @EventHandler
  public void onPlayerTeleport (final PlayerTeleportEvent event) {
    if (myNotifications[6].status) {
      final Player player = (Player)event.getPlayer();
      final Location from = event.getFrom();
      final Location to = event.getTo();
      if (from.getBlockX() == to.getBlockX() && from.getBlockZ() == to.getBlockZ()) {
        return;
      }
      updateStatus(twitter, player.getName() + " teleported from X" + String.valueOf(from.getBlockX()) + ",Y" + String.valueOf(from.getBlockY()) + ",Z" + String.valueOf(from.getBlockZ())+ " to X" + String.valueOf(to.getBlockX()) + ",Y" + String.valueOf(to.getBlockY()) + ",Z" + String.valueOf(to.getBlockZ()));
    } else {
      return;
    }
  }
 
  @EventHandler
  public void onVehicleEnter (final VehicleEnterEvent event) {
    if (myNotifications[7].status) {
      if (!(event.getEntered() instanceof Player)) {
        return;
      }
      final Player player = (Player)event.getEntered();
      final Vehicle vehicle = event.getVehicle();
      updateStatus(twitter, player.getName() + " got into a " + String.valueOf(vehicle) + ".");
    } else {
      return;
    }
  }
  */
  
  private String setLocalMessage (boolean recentJoin) {
    if (isLocal(recentPlayerIP)) {
      if (recentJoin) {
        return ("\n" + recentPlayer + " is a local person.");
      } else {
        return ("\n" + recentPlayer + " was a local person."); 
      }
    } else {
      if (recentJoin) {
        return ("\n" + recentPlayer + " is not local!");
      } else {
        return ("\n" + recentPlayer + " was not local."); 
      }
    }
  }
  
  private boolean isLocal(String recentPlayerIP) {
    // Check whether IP address is coming from router hence WAN
    if (recentPlayerIP.equals("192.168.1.1")) {
      return false;
    }
    // Otherwise it must be local
    else if (recentPlayerIP.startsWith("192.168")) {
      return true;
    }
    // Any other address is from outside
    else {
      return false;
    }
  }
  
  private String parseLocation(Location location) {
    // 5 records to include currently unused pitch and yaw
    String playerLocation[] = {"","","","",""};
    String locationString = "";
    Boolean exemption = false;
    DecimalFormat df = new DecimalFormat("#.##");
    playerLocation[0] = df.format(location.getX());
    playerLocation[1] = df.format(location.getY());
    playerLocation[2] = df.format(location.getZ());
    /* Possibly use in future
    playerLocation[3] = df.format(location.getPitch());
    playerLocation[4] = df.format(location.getYaw());
    */
    for (String e : exemptionList) {
      if (e.contains(recentPlayer)) {
        exemption = true;
      }
    }
    if (exemption) {
      locationString = recentPlayer + " is sneaky and can't be seen!";
      getLogger().info(recentPlayer + " is exempt from co-ord display");
    }
    else {
      locationString = "X: " + playerLocation[0] + " Y: " + playerLocation[1] + " Z: " + playerLocation[2];
      getLogger().info(recentPlayer + " is not exempt from co-ord display");
    }
    return locationString;
  }
  
  private Twitter setupTwitter() throws TwitterException {
    if (TWITTER_CONFIGURED) {
      TwitterFactory factory = new TwitterFactory();
      final Twitter twitter = factory.getInstance();
      AccessToken accessToken = loadAccessToken();
      authenticateTwitter(accessToken, twitter);
      return twitter;
    }
    else {
      getLogger().info("Twitter is switched off you doughnut.");
    }
    return null;
  }

  private void updateStatus(Twitter twitter, String testMessage) {
    if (twitter != null) {
      try {
        twitter.updateStatus(testMessage + "\n" + new Date());
      } catch (TwitterException e) {
        getLogger().info("Twitter is broken because of " + e);
        throw new RuntimeException(e);
      }
    }
  }
  
  private static void authenticateTwitter(AccessToken accessToken, Twitter twitter) {
    twitter.setOAuthConsumer(API_KEY, API_SECRET);
    twitter.setOAuthAccessToken(accessToken);
  }

  private static AccessToken loadAccessToken() {
    String token = Main.token;
    String tokenSecret = secret;
    return new AccessToken(token, tokenSecret);
  }

}
