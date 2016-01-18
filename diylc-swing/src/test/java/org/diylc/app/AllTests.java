package org.diylc.app;

import org.diylc.app.menus.tools.BomMakerTest;
import org.diylc.components.ComponentProcessorTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;


@RunWith(Suite.class)
@SuiteClasses( { BomMakerTest.class, ComponentProcessorTest.class })
public class AllTests {

}
