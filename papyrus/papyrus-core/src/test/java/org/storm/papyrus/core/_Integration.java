package org.storm.papyrus.core;

import org.junit.experimental.categories.Categories;
import org.junit.experimental.categories.Categories.IncludeCategory;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.storm.papyrus.category.Integration;

@RunWith(Categories.class)
@IncludeCategory(Integration.class)
@Suite.SuiteClasses({ BasePapyrusTest.class, PapyrusConfigurationFactoryTest.class, SoftCacheTest.class,
    VersionTest.class })
public class _Integration {}
