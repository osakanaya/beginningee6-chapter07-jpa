package org.beginningee6.book.chapter07.jpa.ex01;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.*;

import java.util.List;
import java.util.logging.Logger;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.UserTransaction;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * CD01エンティティのテスト。
 */
@RunWith(Arquillian.class)
public class CD01Test {
	
	private static final Logger logger = Logger.getLogger(CD01Test.class.getName());
	
	@Deployment
	public static Archive<?> createDeployment() {
		JavaArchive archive = ShrinkWrap.create(JavaArchive.class)
			.addPackage(CD01.class.getPackage())
			.addAsManifestResource("test-persistence.xml", "persistence.xml")
			.addAsManifestResource("jbossas-ds.xml")
			.addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml");

		logger.info(archive.toString(true));

		return archive;
	}
	
	@PersistenceContext
	EntityManager em;
	
	@Inject
	UserTransaction userTransaction;
	
	@Before
	public void setUp() throws Exception {
		clearData();
	}
	
	private void clearData() throws Exception {
		userTransaction.begin();
		em.joinTransaction();

		logger.info("Dumping old records...");
		
		em.createQuery("DELETE FROM CD01").executeUpdate();
		userTransaction.commit();
	}
	
	/**
	 * 1つのCD01エンティティを永続化するテスト。
	 */
	@Test
	public void testPersistACD() throws Exception {
		
		///// 準備 /////
		
        CD01 cd = new CD01(
        		"Title 1",
        		10.0F,
        		"Title 1 Description",
        		null,
        		"Music Company 1",
        		1,
        		100.0F,
        		"male");

        ///// テスト /////
        
        userTransaction.begin();
        em.joinTransaction();
        
        em.persist(cd);
        
        userTransaction.commit();
        
        ///// 検証 /////
        
        assertThat(cd.getId(), is(notNullValue()));        
	}
	
	/**
	 * 複数（2つ）のCD01エンティティが永続化されている状態で、
	 * CD01クラスに定義した名前付きクエリを
	 * 実行すると永続化されたすべてのエンティティが
	 * 検索結果として得られることを確認する。
	 */
	@Test
	public void testFindTwoCDs() throws Exception {
		
		///// 準備 /////
		
        CD01 cd1 = new CD01(
        		"Title 1",
        		10.0F,
        		"Title 1 Description",
        		null,
        		"Music Company 1",
        		1,
        		100.0F,
        		"male");

        CD01 cd2 = new CD01(
        		"Title 2",
        		20.0F,
        		"Title 2 Description",
        		null,
        		"Music Company 2",
        		2,
        		200.0F,
        		"female");

        userTransaction.begin();
        em.joinTransaction();
        
        em.persist(cd1);
        em.persist(cd2);
        
        userTransaction.commit();
        em.clear();

        ///// テスト /////
        
        List<CD01> books = em.createNamedQuery("CD01.findAllCDs", CD01.class).getResultList();
        
        ///// 検証 /////
        
        assertThat(books.size(), is(2));
        assertThat(books, hasItems(cd1, cd2));
	}

}
