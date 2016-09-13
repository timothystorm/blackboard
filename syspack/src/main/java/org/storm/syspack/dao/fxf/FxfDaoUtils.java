package org.storm.syspack.dao.fxf;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.storm.syspack.domain.User;

/**
 * Utilities for working with parameters of {@link FxfDao}
 */
public class FxfDaoUtils {

  /**
   * Converts a collection of {@link User}s to a singleton map (one key, many values) with unique account numbers
   * 
   * @param key
   *          - singleton map key
   * @param users
   *          - to extract account numbers from
   * @return singleton map of unique account numbers
   */
  public static Map<String, List<String>> singletonAccountParam(String paramName, Collection<User> users) {
    List<String> accounts = users.stream().flatMap((usr) -> {
      return usr.getAccounts().stream();
    }).distinct().collect(Collectors.toList());

    return Collections.singletonMap(paramName, accounts);
  }

  /**
   * Converts a collection of {@link User}s to a singleton map (one key, many values) with unique phone numbers
   * 
   * @param key
   *          - singleton map key
   * @param users
   *          - to extract phone numbers from
   * @return singleton map of unique phone numbers
   */
  public static Map<String, List<Double>> singletonPhoneParam(String key, Collection<User> users) {
    List<Double> phones = users.stream().map(u -> {
      return u.getPhone().getNumber();
    }).distinct().collect(Collectors.toList());

    return Collections.singletonMap(key, phones);
  }

  /**
   * Converts a collection of {@link User}s to a singleton map (one key, many values) with unique uuids
   * 
   * @param key
   *          - singleton map key
   * @param users
   *          - to extract uuids from
   * @return singleton map of unique uuids
   */
  public static Map<String, List<String>> singletonUuidParam(String paramName, Collection<User> users) {
    List<String> uuids = users.stream().map(u -> {
      return u.getUuid();
    }).distinct().collect(Collectors.toList());

    return Collections.singletonMap(paramName, uuids);
  }
}
