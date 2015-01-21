package gpps.service;

import gpps.model.Help;

import java.util.Map;

import org.apache.ibatis.annotations.Param;

public interface IHelpService {
	public void createPublic(Help help);
	public void createPrivate(Help help);
	public Help find(Integer id);
	/**
	 * 翻页查找信息展示
	 * @param publicType -1 不限 0： 新手问题；	1： 常见问题
	 * @param offset
	 * @param recnum
	 * @return
	 */
	public Map<String,Object> findPublicHelps(int publicType,int offset,int recnum);
//	/**翻页获取Private Help
//	 * questionerType:-1为不限,0:Lender,1:borrower
//	 * questionerId:当questionerType不为-1时判断，null为不限
//	 */
	/**
	 * 翻页查找我的帮助
	 * @param type -1不限 1:未回答 2：已回答
	 * @param offset
	 * @param recnum
	 * @return
	 */
	public Map<String,Object> findMyHelps(int type,int offset,int recnum);
	
	public Map<String,Object> findPrivateHelps(int type,int offset,int recnum);
	/**
	 * 回答帮助问题
	 * @param id
	 * @param answer
	 */
	public void answer(Integer id,String answer);
}
