package com.galvarez.ttw.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.EntitySystem;
import com.artemis.annotations.Wire;
import com.artemis.utils.ImmutableBag;
import com.galvarez.ttw.model.DiplomaticSystem.State;
import com.galvarez.ttw.model.components.Diplomacy;
import com.galvarez.ttw.model.components.Discoveries;
import com.galvarez.ttw.model.components.InfluenceSource;
import com.galvarez.ttw.model.components.Score;
import com.galvarez.ttw.model.data.SessionSettings;

/**
 * For every empire, compute score every turn.
 * <p>
 * Every turn get one point per influenced tile, half the turn score from
 * tributary and a quarter of the turn score from allies.
 * </p>
 * 
 * @author Guillaume Alvarez
 */
@Wire
public final class ScoreSystem extends EntitySystem {

  @SuppressWarnings("unused")
  private static final Logger log = LoggerFactory.getLogger(ScoreSystem.class);

  private ComponentMapper<Score> scores;

  private ComponentMapper<InfluenceSource> sources;

  private ComponentMapper<Diplomacy> relations;

  private ComponentMapper<Discoveries> discoveries;

  private final List<Item> list = new ArrayList<>();

  private final int nbDiscoveries;

  @SuppressWarnings("unchecked")
  public ScoreSystem(SessionSettings s) {
    super(Aspect.getAspectForAll(Score.class, InfluenceSource.class));

    nbDiscoveries = s.getDiscoveries().size();
  }

  @Override
  protected void inserted(Entity e) {
    super.inserted(e);
    Score score = scores.get(e);
    score.nbDiscoveriesMax = nbDiscoveries;
    list.add(new Item(e, score));
  }

  @Override
  protected void removed(Entity e) {
    super.removed(e);

    for (Iterator<Item> it = list.iterator(); it.hasNext();)
      if (it.next().empire.equals(e)) {
        it.remove();
        return;
      }
  }

  @Override
  protected boolean checkProcessing() {
    return true;
  }

  @Override
  protected void processEntities(ImmutableBag<Entity> entities) {
    for (Entity empire : entities) {
      Score score = scores.get(empire);
      score.lastTurnPoints = 0;
      score.nbControlled = 0;
    }
    int nbControlledMax = entities.size();

    for (Entity empire : entities) {
      InfluenceSource source = sources.get(empire);
      add(empire, source.influencedTiles.size(), 0);

      Score score = scores.get(empire);
      score.nbDiscoveries = discoveries.get(empire).done.size();
      score.nbControlledMax = nbControlledMax;
    }

    Collections.sort(list, Comparator.comparingInt((Item i) -> i.score.totalScore).reversed());
    for (int r = 0; r < list.size(); r++)
      list.get(r).score.rank = r + 1;
  }

  /** Recursive method to add score to all. */
  private void add(Entity empire, int delta, int nbControlled) {
    if (delta > 0
    // do not forget an empire can be deleted
        && scores.has(empire)) {
      Diplomacy diplomacy = relations.get(empire);

      Score score = scores.get(empire);
      score.lastTurnPoints += delta;
      score.totalScore += delta;
      score.nbControlled += nbControlled;

      // add to overlords and allies
      for (Entry<Entity, State> e : diplomacy.relations.entrySet()) {
        if (e.getValue() == State.TREATY)
          add(e.getKey(), delta / 4, 0);
        else if (e.getValue() == State.TRIBUTE)
          // overlord controls me and my vassals
          add(e.getKey(), delta / 2, score.nbControlled + 1);
      }
    }
  }

  public final class Item {

    public final Entity empire;

    public final Score score;

    Item(Entity empire, Score score) {
      this.empire = empire;
      this.score = score;
    }

  }

  public List<Item> getScores() {
    return list;
  }

}
