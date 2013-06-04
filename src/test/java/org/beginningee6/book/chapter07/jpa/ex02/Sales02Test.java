package org.beginningee6.book.chapter07.jpa.ex02;

import static org.beginningee6.book.chapter07.util.IsDate.*;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.UserTransaction;

import org.beginningee6.book.chapter07.util.IsDate;
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
 * Sales02及びItem02エンティティのテスト。
 * 
 * Sales02エンティティはItem02エンティティへの
 * １対多のリレーションを持っており、この
 * リレーションに従ってSales02エンティティと
 * このエンティティに関連づけられる１つまたは複数の
 * Item02エンティティを同時に永続化するテストを
 * 行っている。
 * 
 */
@RunWith(Arquillian.class)
public class Sales02Test {
	
	private static final Logger logger = Logger.getLogger(Sales02Test.class.getName());
	
	@Deployment
	public static Archive<?> createDeployment() {
		JavaArchive archive = ShrinkWrap.create(JavaArchive.class)
			.addPackage(Sales02.class.getPackage())
			.addPackage(IsDate.class.getPackage())
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
		
		em.createQuery("delete from Item02").executeUpdate();
		em.createQuery("delete from Sales02").executeUpdate();
		userTransaction.commit();
	}
	
	/**
	 * Item02エンティティを永続化するテスト
	 */
	@Test
	public void testPersistAnItem() throws Exception {
		
		///// 準備 /////
		
		Item02 item = new Item02();
        item.setTitle("Zoot Allure");
        item.setPrice(23f);
        item.setDescription("Another Zappa's master piece");

        ///// テスト /////
        
        userTransaction.begin();
        em.joinTransaction();
        
        em.persist(item);
        
        userTransaction.commit();
        
        ///// 検証 /////
        
        assertThat(item.getId(), is(notNullValue()));
        
        em.clear();
        Item02 persisted = em.find(Item02.class, item.getId());

        // Item02エンティティでオーバライドしたequals()メソッドにより
        // フィールドごとのAssertionを書かずともオブジェクトどうしの
        // 比較により等価性を検証できるようになっている。
        assertThat(persisted, is(item));
	}
	
	/**
	 * Sales02エンティティを永続化するテスト。
	 * 
	 * このテストでは、Sales02エンティティにItem02エンティティを
	 * 関連付けられない状態での永続化をテストしている。
	 * 
	 */
	@Test
	public void testPersistASalesWithoutItems() throws Exception {
		
		///// 準備 /////
		
		Sales02 sales = new Sales02();
		sales.setCustomerName("John Q Public");
		sales.setTotalAmount(0.0F);
		sales.setSalesDate(new Date());

        ///// テスト /////
        
        userTransaction.begin();
        em.joinTransaction();
        
        em.persist(sales);
        
        userTransaction.commit();
        
        ///// 検証 /////
        
        assertThat(sales.getId(), is(notNullValue()));

        em.clear();
        Sales02 persisted = em.find(Sales02.class, sales.getId());

        assertThat(persisted.getCustomerName(), is(sales.getCustomerName()));
        assertThat(persisted.getTotalAmount(), 	is(sales.getTotalAmount()));
        // dateOfというカスタムマッチャを用いて、
        // Date型の値の、年・月・日の部分だけ等しいことを確認する
        assertThat(persisted.getSalesDate(), 	is(dateOf(sales.getSalesDate())));
        // itemリストは空なので０
        assertThat(persisted.getItems().size(), is(0));
	}
	
	/**
	 * 
	 * Sales02エンティティを永続化するテスト。
	 * 
	 * このテストでは、Sales02エンティティに2つのItem02エンティティを
	 * 関連付けた状態での永続化をテストしている。
	 * 
	 */
	@Test
	public void testPersistASalesAndItems() throws Exception {
		
		///// 準備 /////
		
		Item02 item1 = new Item02("Zoot Allure", 23f, "Another Zappa's master piece");
		Item02 item2 = new Item02("The Hitchhiker's Guide to the Galaxy", 12.5F, "Science fiction comedy book");
		
		List<Item02> salesItems = new ArrayList<Item02>();
		salesItems.add(item1);
		salesItems.add(item2);
		
		Sales02 sales = new Sales02("John Q Public", new Date());
		sales.setTotalAmount(35.5f);
		sales.setItems(salesItems);

        ///// テスト /////
        
        userTransaction.begin();
        em.joinTransaction();

        em.persist(item1);
        em.persist(item2);
        em.persist(sales);
        
        userTransaction.commit();
        
        ///// 検証 /////
        
        assertThat(sales.getId(), is(notNullValue()));
        assertThat(item1.getId(), is(notNullValue()));
        assertThat(item2.getId(), is(notNullValue()));
        
        em.clear();
        
        Sales02 persisted = em.find(Sales02.class, sales.getId());
        
        assertThat(persisted, is(notNullValue()));
        assertThat(persisted.getCustomerName(), is(sales.getCustomerName()));

        // dateOfというカスタムマッチャを用いて、
        // Date型の値の、年・月・日の部分だけ等しいことを確認する
        assertThat(persisted.getSalesDate(), 	is(dateOf(sales.getSalesDate())));
        assertThat(persisted.getTotalAmount(),	is(sales.getTotalAmount()));

        // Sales02のitemsにitem1とitem2が含まれているを確認する
        // 
        // Item02エンティティでオーバライドしたequals()メソッドにより
        // オブジェクトの等価性を検証できるようにしているため、
        // hasItemsマッチャでオブジェクトを指定するだけで
        // そのオブジェクトがリストに含まれており、かつ、
        // そのオブジェクトとリストに含まれるオブジェクトの間で
        // すべてのフィールドの値が完全に一致していることを検証できる
        // ようになっている。
        assertThat(persisted.getItems(), hasItems(item1, item2));
	}
	
}
