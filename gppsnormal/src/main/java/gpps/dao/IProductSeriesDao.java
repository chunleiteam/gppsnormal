/**
 * 
 */
package gpps.dao;

import gpps.model.ProductSeries;

import java.util.List;

/**
 * @author wangm
 *
 */
public interface IProductSeriesDao {
	public void create(ProductSeries productSeries);
	public ProductSeries find(Integer id);
	public ProductSeries findByType(Integer type);
	public List<ProductSeries> findAll();
	public void delete(Integer id);
}
