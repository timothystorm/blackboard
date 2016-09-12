package org.storm.syspack.dao.fxf;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.storm.syspack.domain.User;

public class FxfDaoUtils {

  public static Map<String, List<Double>> singletonPhoneParam(String paramName, Collection<User> users) {
    return Collections.singletonMap(paramName, uniquePhones(users));
  }
  
  public static Map<String, List<String>> singletonAccountParam(String paramName, Collection<User> users){
    return Collections.singletonMap(paramName, uniqueAccounts(users));
  }

  public static List<Double> uniquePhones(Collection<User> users) {
    Set<Double> phones = new HashSet<>();
    users.forEach(u -> phones.add(u.getPhone().getNumber()));
    return new ArrayList<>(phones);
  }

  public static List<String> uniqueAccounts(Collection<User> users) {
    Set<String> accounts = new HashSet<>();
    users.forEach(u -> accounts.addAll(u.getAccounts()));
    return new ArrayList<>(accounts);
  }
}
