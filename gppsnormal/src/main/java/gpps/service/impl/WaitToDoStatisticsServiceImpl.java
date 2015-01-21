package gpps.service.impl;

import gpps.dao.IBorrowerDao;
import gpps.dao.IFinancingRequestDao;
import gpps.dao.IGovermentOrderDao;
import gpps.dao.IPayBackDao;
import gpps.model.Borrower;
import gpps.model.FinancingRequest;
import gpps.model.GovermentOrder;
import gpps.model.PayBack;
import gpps.service.IWaitToDoStatisticsService;
import gpps.service.WaitToDoStatistics;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
@Service
public class WaitToDoStatisticsServiceImpl implements IWaitToDoStatisticsService{
	@Autowired
	IBorrowerDao borrowerDao;
	@Autowired
	IFinancingRequestDao financingRequestDao;
	@Autowired
	IGovermentOrderDao govermentOrderDao;
	@Autowired
	IPayBackDao paybackDao;
	@Override
	public WaitToDoStatistics getStatistics() {
		WaitToDoStatistics statistisc=new WaitToDoStatistics();
		statistisc.setApplyBorrowerCount(borrowerDao.countByPrivilege(Borrower.PRIVILEGE_APPLY));
		statistisc.setFinancingRequestCount(financingRequestDao.countByState(FinancingRequest.STATE_INIT));
		List<Integer> orderStates=new ArrayList<Integer>();
		orderStates.add(GovermentOrder.STATE_PREPUBLISH);
		statistisc.setPrepublishOrderCount(govermentOrderDao.countByState(orderStates));
		
		statistisc.setUnCheckedOrderCount(govermentOrderDao.countAllUnCheckedOrder(System.currentTimeMillis()));
		
		orderStates.clear();
		orderStates.add(GovermentOrder.STATE_WAITINGCLOSE);
		statistisc.setWaitingCloseOrderCount(govermentOrderDao.countByState(orderStates));
		
		statistisc.setToAuditPaybackCount(paybackDao.findByProductsAndState(null, PayBack.STATE_WAITFORCHECK).size());
		return statistisc;
	}

}
