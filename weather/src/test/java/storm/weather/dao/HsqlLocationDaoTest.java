package storm.weather.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import storm.weather.AppConfig;
import storm.weather.domain.Location;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { AppConfig.class }, loader = AnnotationConfigContextLoader.class)
public class HsqlLocationDaoTest {
	@Autowired
	LocationDao _dao;

	@Before
	@After
	public void reset() throws Exception {
		if (_dao != null) _dao.deleteAll();
	}

	@Test
	public void test_readAll() throws Exception {
		List<Location> locations = _dao.readAll();
		assertNotNull(locations);
		assertTrue(locations.isEmpty());
	}

	@Test
	public void test_save() throws Exception {
		Location loc = new Location("80920");
		_dao.save(loc);
	}

	@Test
	public void test_lifecycle() throws Exception {
		// intial read
		List<Location> locations = _dao.readAll();
		assertNotNull(locations);
		assertTrue(locations.isEmpty());

		// save a few locations
		_dao.save(new Location("80920"));
		_dao.save(new Location("80121"));

		// verify save
		locations = _dao.readAll();
		assertNotNull(locations);
		assertFalse(locations.isEmpty());
		assertEquals(2, locations.size());

		// delete a single location
		_dao.delete(new Location("80920"));

		// verify delete
		locations = _dao.readAll();
		assertNotNull(locations);
		assertFalse(locations.isEmpty());
		assertEquals(1, locations.size());

		// delete all
		_dao.deleteAll();

		// verify delete all
		locations = _dao.readAll();
		assertNotNull(locations);
		assertTrue(locations.isEmpty());
	}

	@Test
	public void test_save_duplicates() throws Exception {
		// this should not fail
		_dao.save(new Location("80920"));
		_dao.save(new Location("80920"));

		// verify save
		List<Location> locations = _dao.readAll();
		assertNotNull(locations);
		assertEquals(1, locations.size());
	}
}
