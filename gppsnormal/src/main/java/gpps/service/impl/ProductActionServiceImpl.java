package gpps.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import gpps.dao.IProductActionDao;
import gpps.model.ProductAction;
import gpps.service.IProductActionService;

@Service
public class ProductActionServiceImpl implements IProductActionService {
	@Autowired
	IProductActionDao productActionDao;

	@Override
	public ProductAction create(ProductAction productAction) {
		productActionDao.create(productAction);
		return productAction;
	}

	@Override
	public List<ProductAction> findAllByProduct(Integer productId) {
		return productActionDao.findAllByProduct(productId);
	}

}
