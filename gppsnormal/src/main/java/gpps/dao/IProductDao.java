/**
 * 
 */
package gpps.dao;

import gpps.model.Product;
import gpps.service.exception.IllegalConvertException;

import java.math.BigDecimal;
import java.util.List;

import org.apache.ibatis.annotations.Param;

/**
 * @author wangm
 *
 */
public interface IProductDao {
	public void create(Product product);
	public void changeState(@Param("id")Integer id,@Param("state")int state,@Param("lastmodifytime")long lastmodifytime);
	public Product find(Integer id);
	public List<Product> findByGovermentOrder(Integer orderId);
	public List<Product> findByState(@Param("states")List<Integer> states,@Param("offset")int offset,@Param("recnum")int recnum);
	public int countByState(@Param("states")List<Integer> states);
	public List<Product> findByProductSeriesAndState(@Param("productSeriesId")Integer productSeriesId,@Param("states")List<Integer> states,@Param("offset")int offset,@Param("recnum")int recnum);
	public int countByProductSeriesAndState(@Param("productSeriesId")Integer productSeriesId,@Param("states")List<Integer> state);
	public void changeBuyLevel(@Param("id")Integer id,@Param("levelToBuy")int levelToBuy);
	public void buy(@Param("id")Integer id,@Param("amount")BigDecimal amount);
	public void delete(Integer id);
	public String findAccessory(Integer productId);
	public void updateAccessory(@Param("productId")Integer productId,@Param("accessory")String accessory);
	public void update(@Param("id")Integer id,@Param("expectAmount")BigDecimal expectAmount,@Param("rate")BigDecimal rate,@Param("incomeEndtime")long incomeEndtime,@Param("minimum")int minimum,@Param("miniAdd")int miniAdd,@Param("levelToBuy")int levelToBuy);
}
