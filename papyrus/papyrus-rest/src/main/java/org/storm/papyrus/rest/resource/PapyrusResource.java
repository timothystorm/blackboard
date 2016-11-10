package org.storm.papyrus.rest.resource;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.storm.papyrus.core.Papyrus;
import org.storm.papyrus.rest.UserScope;

@RestController
@RequestMapping("/papyrus")
public class PapyrusResource {
  
  private final Papyrus _papyrus;
  
  public PapyrusResource(Papyrus papyrus) {
    _papyrus = papyrus;
  }

  @GetMapping
  public String getProperty(String key, UserScope scope) {
    return _papyrus.getProperty(scope.getId(), key);
  }
}
