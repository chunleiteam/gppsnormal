package gpps.service;

import java.util.List;

public interface IContractService {
	public List<ContractItem> list(Integer pid) throws Exception;
	public String getProductName(Integer pid);
	public boolean isComplete(Integer pid);
	public boolean submitContract(Integer pid, Integer sid);
}
