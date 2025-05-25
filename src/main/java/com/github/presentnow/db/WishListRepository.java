package com.github.presentnow.db;

import com.github.presentnow.entity.WishList;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;

@ApplicationScoped
public class WishListRepository implements PanacheRepository<WishList>
{

	public List<WishList> getActive(String username)
	{
		return find("active = ?1 and username = ?2", true, username).list();
	}
}
