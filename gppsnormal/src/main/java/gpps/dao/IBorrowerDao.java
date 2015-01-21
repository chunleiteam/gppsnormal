package gpps.dao;

import gpps.model.Borrower;

import java.util.List;

import org.apache.ibatis.annotations.Param;

public interface IBorrowerDao {
	public int countAll();
	public List<Borrower> findAll(@Param("offset")int offset,@Param("recnum")int recnum);
	public Borrower find(Integer id);
	public Borrower findByLoginId(String loginId);
	public Borrower findByLoginIdAndPassword(@Param("loginId") String loginId,@Param("password") String password);
	public List<Borrower> findByPrivilege(int privilege);
	public List<Borrower> findByTel(String tel);
	public List<Borrower> findByEmail(String email);
	public Borrower findByAccountID(Integer accountId);
	public Borrower findByIdentityCard(String identityCard);
	public List<Borrower> findByState(int state);
	public void create(Borrower borrower);
//	public void update(Borrower borrower);
	public void changePrivilege(@Param("id") Integer id,@Param("privilege") int privilege,@Param("lastModifyTime")long lastModifyTime);
	public void changePassword(@Param("id") Integer id,@Param("password") String password);
//	public void changeCreditValue(@Param("id")Integer id,@Param("creditValue")int creditValue);
	public void delete(Integer id);
//	public void addCreditValue(@Param("id")Integer id,@Param("creditValue")int creditValue);
	public void changeCreditValueAndLevel(@Param("id")Integer id,@Param("creditValue")int creditValue,@Param("level")int level);
	
	public String findAccessory(Integer borrowerId);
	public void updateAccessory(@Param("borrowerId")Integer borrowerId,@Param("material")String material);
	
	public String findContactor(Integer borrowerId);
	public void updateContactor(@Param("borrowerId")Integer borrowerId,@Param("contactor")String contactor);
	
	public void registerThirdPartyAccount(@Param("id")Integer id,@Param("thirdPartyAccount")String thirdPartyAccount,@Param("accountNumber")String accountNumber);
	/**
	 * @param state -1 不限
	 * @return
	 */
	public int countByPrivilege(@Param("privilege")int privilege);
	/**
	 * @param state -1 不限
	 * @return
	 */
	public List<Borrower> findByPrivilegeWithPaging(@Param("privilege")int privilege,@Param("offset")int offset,@Param("recnum")int recnum);
	public void update(@Param("id")Integer id,@Param("corporationName")String corporationName,@Param("corporationAddr")String corporationAddr,@Param("brange")String brange);
	public void bindCard(@Param("id")Integer id,@Param("cardId")Integer cardId);
	public Borrower findByThirdPartyAccount(String thirdPartyAccount);
	public void updateAuthorizeTypeOpen(@Param("id")Integer id,@Param("authorizeTypeOpen")int authorizeTypeOpen);
	public void updateTelAndEmail(@Param("id")Integer id,@Param("tel")String tel,@Param("email")String email);
}
