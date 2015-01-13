package in.virit.vwscdn.directory.test;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.util.List;
import junit.framework.TestCase;
import in.virit.vwscdn.directory.Addon;
import in.virit.vwscdn.directory.Directory;

/**
 *
 * @author se
 */
public class TestDirectoryApi extends TestCase {

    public TestDirectoryApi(String testName) {
        super(testName);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testSearch() {
        List<Addon> results = Directory.search("7", "confirm");
        assertEquals(1, results.size());
        assertEquals("ConfirmDialog", results.get(0).getName());
    }
}
