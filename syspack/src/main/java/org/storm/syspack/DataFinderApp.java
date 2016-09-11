package org.storm.syspack;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.storm.syspack.dao.fxf.FxfDao;
import org.storm.syspack.dao.fxf.FxfDaoFactory;

public class DataFinderApp implements Runnable {

  public static void main(String[] args) throws Exception {
    ApplicationContext cntx = new AnnotationConfigApplicationContext(Config.class);
    FxfDaoFactory fxfDaoFactory = cntx.getBean(FxfDaoFactory.class);
    FxfDao dao = fxfDaoFactory.getFxfDao("ANI_CUSTOMER");
    
    System.out.println(dao);
  }

  @Override
  public void run() {

  }
}
