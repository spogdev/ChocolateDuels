package live.spog.chocolateDuels.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;

public class Message {
    private String contents;

    public Message(String contents){
        this.contents = contents;
    }

    public String getRawContents(){
        return this.contents;
    }

    public Message setContents(String contents){
        this.contents = contents;
        return this;
    }

    public TextComponent asFormattedComponent() {
        LegacyComponentSerializer legacy_hex = LegacyComponentSerializer.builder()
                .character('&')
                .hexColors()
                .useUnusualXRepeatedCharacterHexFormat()
                .build();

        return legacy_hex.deserialize(this.contents);
    }

    public TextComponent asComponent() {
        return Component.text(this.contents);
    }

    public void send(Player player) {
        player.sendMessage(this.asFormattedComponent());
    }

    public static Message of(String contents) {
        return new Message(contents);
    }

    public void send(MessageReceivers receivers) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (receivers == MessageReceivers.EVERYONE) {
                player.sendMessage(this.asFormattedComponent());
            } else if (receivers == MessageReceivers.STAFF && player.hasPermission("demon.s2.admin")) {
                player.sendMessage(this.asFormattedComponent());
            } else if (receivers == MessageReceivers.OPERATORS && player.isOp()) {
                player.sendMessage(this.asFormattedComponent());
            }
        }
    }

    public void send(Permission permission) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.hasPermission(permission)) {
                player.sendMessage(this.asFormattedComponent());
            }
        }
    }

    public Message replacePlaceholder(String key, String value) {
        String contents = this.getRawContents();
        this.setContents(contents.replace("{" + key + "}", value));
        return this;
    }

    public Message replacePlaceholder(String key, String value, char delimiter) {
        String contents = this.getRawContents();
        this.setContents(contents.replace(delimiter + key + delimiter, value));
        return this;
    }
}