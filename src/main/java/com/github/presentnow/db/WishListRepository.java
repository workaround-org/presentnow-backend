package com.github.presentnow.db;

import com.github.presentnow.entity.WishList;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;

@ApplicationScoped
public class WishListRepository implements PanacheRepository<WishList>
{

	public List<WishList> getActive()
	{
		return find("active", true).list();
	}
}
