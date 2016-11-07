package com.lenis0012.chatango.bot.events.pm;

import com.lenis0012.chatango.bot.api.Friend;

public class PMFriendAddedEvent {
    private final Friend friend;

    public PMFriendAddedEvent(Friend friend) {
        this.friend = friend;
    }

    public Friend getFriend() {
        return friend;
    }
}
