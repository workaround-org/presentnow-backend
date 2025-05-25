package com.github.presentnow.db;

import com.github.presentnow.entity.PresentIdea;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class PresentIdeaRepository implements PanacheRepository<PresentIdea>
{
}
