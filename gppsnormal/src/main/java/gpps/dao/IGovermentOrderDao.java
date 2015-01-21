package gpps.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import gpps.model.GovermentOrder;
import gpps.model.ref.Accessory.MimeItem;

public interface IGovermentOrderDao {
	public void create(GovermentOrder govermentOrder);
	public void changeState(@Param("orderId")Integer orderId,@Param("state")int state,@Param("lastModifytime")long lastModifytime);
	public List<GovermentOrder> findByStatesWithPaging(@Param("states")List<Integer> states,@Param("offset")int offset,@Param("recnum")int recnum);
	public List<GovermentOrder> findByStates(@Param("states")List<Integer> states);
	public int countByState(@Param("states")List<Integer> states);
	public List<GovermentOrder> findByBorrowerIdAndState(@Param("borrowerId")Integer borrowerId,@Param("states")List<Integer> states);
	public void delete(Integer id);
	public GovermentOrder find(Integer id);
	public String findAccessory(Integer orderId);
	public void updateAccessory(@Param("orderId")Integer orderId,@Param("material")String material);
	
	public GovermentOrder findByFinancingRequest(Integer financingRequestId);
	
	public List<GovermentOrder> findAllUnpublishOrders();
	public void update(@Param("id")Integer id,@Param("title")String title,@Param("financingStarttime")long financingStarttime,@Param("financingEndtime")long financingEndtime,@Param("incomeStarttime")long incomeStarttime,@Param("description")String description, @Param("formalName")String formalName, @Param("formalLevel")String formalLevel, @Param("formalAmount")String formalAmount, @Param("tenderUnits")String tenderUnits, @Param("formalLink")String formalLink);
	/**
	 * 计算待审核（状态为融资中且已过融资截止时间）的订单
	 * @return
	 */
	public int countAllUnCheckedOrder(long currentTime);
}
