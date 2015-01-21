package gpps.service;

import gpps.model.ProductAction;

import java.util.List;

public interface IProductActionService {
	public ProductAction create(ProductAction productAction);
	public List<ProductAction> findAllByProduct(Integer productId);
}
