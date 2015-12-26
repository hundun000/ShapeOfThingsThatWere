package com.galvarez.ttw.model.components;

import com.artemis.Component;
import com.badlogic.gdx.utils.ObjectIntMap;
import com.galvarez.ttw.model.EventsSystem.EventHandler;

public class EventsCount extends Component {

  public final ObjectIntMap<EventHandler> scores = new ObjectIntMap<>();

  public final ObjectIntMap<EventHandler> increment = new ObjectIntMap<>();

  public final ObjectIntMap<EventHandler> display = new ObjectIntMap<>();

  public EventsCount() {
  }

}
