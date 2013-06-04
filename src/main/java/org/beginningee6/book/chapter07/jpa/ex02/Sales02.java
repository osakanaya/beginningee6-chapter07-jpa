package org.beginningee6.book.chapter07.jpa.ex02;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * Item02エンティティへの１対多（一方向）
 * のリレーションシップが定義されている
 */
@Entity
@Table(name = "sales_ex02")
@NamedQuery(name = "Sales02.findAllSales", query = "SELECT s FROM Sales02 s")
public class Sales02 implements Serializable {

	private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue
    private Long id;
    private String customerName;
    private Float totalAmount;
    @Temporal(TemporalType.DATE)
    private Date salesDate;

    // Item02エンティティへの１対多のリレーション
    @OneToMany(fetch = FetchType.EAGER)
    @JoinColumn(name = "sales_fk")
    private List<Item02> items;
    
    public Sales02() {}

	public Sales02(String customerName, Date salesDate) {
		this.customerName = customerName;
		this.salesDate = salesDate;
	}

	public String getCustomerName() {
		return customerName;
	}

	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}

	public Date getSalesDate() {
		return salesDate;
	}

	public void setSalesDate(Date salesDate) {
		this.salesDate = salesDate;
	}

	public Long getId() {
		return id;
	}

	public List<Item02> getItems() {
		return items;
	}

	public void setItems(List<Item02> items) {
		this.items = items;
	}
	
	public Float getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(Float totalAmount) {
		this.totalAmount = totalAmount;
	}

	@Override
	public String toString() {
		return "Sales02 [id=" + id + ", customerName=" + customerName
				+ ", totalAmount=" + totalAmount + ", salesDate=" + salesDate
				+ "]";
	}

}
