package com.lyano.ovh;

import java.util.ArrayList;
import java.util.List;

public class Island {
    private final String ownerName;
    private final int x, y, z;
    private final List<String> members = new ArrayList<>();

    public Island(String ownerName, int x, int y, int z) {
        this.ownerName = ownerName;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public String getOwnerName() { return ownerName; }
    public int getX() { return x; }
    public int getY() { return y; }
    public int getZ() { return z; }
    
    public void addMember(String name) {
        if (!members.contains(name)) members.add(name);
    }
    
    public void removeMember(String name) {
        members.remove(name);
    }
    
    public boolean isMember(String name) {
        return members.contains(name);
    }

    public boolean isMemberOrOwner(String name) {
        return ownerName.equalsIgnoreCase(name) || isMember(name);
    }
}