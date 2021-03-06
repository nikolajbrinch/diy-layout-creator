package org.diylc.app.online;

import org.diylc.app.online.presenter.LibraryPresenter;
import org.junit.Test;


public class LibraryPresenterTest {

	@Test
	public void testFetchProjects() throws Exception {
		LibraryPresenter pres = new LibraryPresenter();
		// pres.createUser("bancika", "pwd", "bancika@gmail.com");
		System.out.println(pres.fetchCategories());
		pres.login("bancika", "pwd");
		int id = pres.fetchMyProjectRows().get(0).getId();
		// InputStream stream = pres.downloadProjectContent(id);
		// byte[] buf = new byte[100];
		// int len = stream.read(buf);
		System.out.println(pres.downloadProjectContent(id));
		// System.out.println(pres.hashPassword("bancikabancikabancikabancikabancika"));
		pres.dispose();
	}

}
