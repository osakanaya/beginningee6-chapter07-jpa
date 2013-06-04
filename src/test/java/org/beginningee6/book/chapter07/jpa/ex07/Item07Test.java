package org.beginningee6.book.chapter07.jpa.ex07;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.*;

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
 * Item07エンティティのテスト。
 */
@RunWith(Arquillian.class)
public class Item07Test {
	private static final Logger logger = Logger.getLogger(Item07Test.class.getName());
	
	@Deployment
	public static Archive<?> PersistDeployment() {
		JavaArchive archive = ShrinkWrap.create(JavaArchive.class)
			.addPackage(Item07.class.getPackage())
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
		
		em.createQuery("DELETE FROM Item07").executeUpdate();
		userTransaction.commit();
	}
	
	/**
	 * Item07エンティティを永続化するテスト。
	 */
	@Test
	public void testPersistAnItem() throws Exception {
		
		///// 準備 /////
		
		Item07 item = new Item07();
        item.setTitle("The Hitchhiker's Guide to the Galaxy");
        item.setPrice(12.5F);
        item.setIsbn("1-84023-742-2");
        item.setNbOfPage(354);
        item.setIllustrations(false);

        ///// テスト /////
        
        userTransaction.begin();
        em.joinTransaction();
        
        em.persist(item);
        
        userTransaction.commit();
        
        ///// 検証 /////
        
        assertThat(item.getId(), is(notNullValue()));        
	}
}
