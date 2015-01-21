/**
 * 
 */
package gpps.dao;

import java.util.List;

import gpps.model.ProductAction;

/**
 * @author wangm
 *
 */
public interface IProductActionDao {
	public void create(ProductAction productAction);
	public void delete(Integer id);
	public List<ProductAction> findAllByProduct(Integer productId);
}
