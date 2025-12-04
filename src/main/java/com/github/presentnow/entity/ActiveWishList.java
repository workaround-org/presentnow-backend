package com.github.presentnow.entity;

import java.util.UUID;

public class ActiveWishList
{
private final UUID id;
private final String name;
private final String description;
private final int presentIdeasCount;
private final Boolean active;

public ActiveWishList(WishList wishList)
{
this.id = wishList.getId();
this.name = wishList.getName();
this.description = wishList.getDescription();
this.presentIdeasCount = wishList.getPresentIdeas() != null ? wishList.getPresentIdeas().size() : 0;
this.active = wishList.getActive();
}

public UUID getId()
{
return id;
}

public String getName()
{
return name;
}

public String getDescription()
{
return description;
}

public int getPresentIdeasCount()
{
return presentIdeasCount;
}

public Boolean getActive()
{
return active;
}
}
