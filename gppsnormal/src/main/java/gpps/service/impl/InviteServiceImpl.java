package gpps.service.impl;

import gpps.dao.IInviteDao;
import gpps.dao.ILenderDao;
import gpps.model.Invite;
import gpps.model.Lender;
import gpps.service.IInviteService;
import gpps.service.exception.InviteException;
import gpps.service.message.IMessageService;
import gpps.service.message.IMessageSupportService;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class InviteServiceImpl implements IInviteService {
@Autowired
IInviteDao inviteDao;
@Autowired
ILenderDao lenderDao;
@Autowired
IMessageSupportService messageService;
	@Override
	public List<String> allocate(Integer lenderId, int number, int batchCode)
			throws InviteException {
		if(number<=0 || number>MAX_ALLOC_NUMBER){
			throw new InviteException("分配的数量有问题，必须是大于0小于等于"+MAX_ALLOC_NUMBER+"的值！");
		}
		Lender lender = lenderDao.find(lenderId);
		if(lender==null){
			throw new InviteException("用户不存在！");
		}
		
		int maxValue = 0;
		List<String> res = new ArrayList<String>();
		
		Integer id = inviteDao.getMaxId();
		
		if(id!=null){
		Invite invite = inviteDao.find(id);
		 maxValue = Integer.parseInt(invite.getCode());
		}
		String name = (lender.getName()==null || lender.getName().equals(""))?lender.getLoginId():lender.getName();
		String message = "【春蕾政采贷】尊敬的"+name+"，非常感谢您对政采贷的参与和认可，作为回馈您将获得"+number+"个邀请码：";
//				"【春蕾政采贷】分配给您的"+number+"个邀请码是:";
		for(int i=0; i<number; i++){
			Random ran = new Random();
			maxValue = maxValue+ran.nextInt(100)+1;
			
			String ncode = maxValue+"";
			Invite inv = new Invite();
			inv.setAttributeTo(lenderId);
			inv.setCode(ncode);
			inv.setState(Invite.STATE_INIT);
			inv.setBatchCode(batchCode);
			res.add(ncode);
			inviteDao.create(inv);
			
			message += (ncode+", ");
		}
		
		message += "6月30日前，成功邀请好友在平台注册，您将获得20元奖励，好友完成一笔投资，您将获得100元奖励。奖励将在7月1日以转账方式直接打入您的政采贷账户，欢迎您积极参与！";
		
		List<String> tels = new ArrayList<String>();
		tels.add(lender.getTel());
		try{
		messageService.sendSMS(tels, message);
		}catch(Exception e){
			
		}
		System.out.println(message);
		return res;
	}

	@Override
	public List<Invite> getUnRegistered(Integer lenderId)
			throws InviteException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Invite> getRegistered(Integer lenderId) throws InviteException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void check(String code) throws InviteException {
		Invite invite = inviteDao.findByCode(code);
		if(invite==null){
			throw new InviteException("无效的邀请码！");
		}
		if(invite.getState()==Invite.STATE_REGISTERED){
			throw new InviteException("本邀请码已经被成功注册，无法再次注册！");
		}else if(invite.getState()==Invite.STATE_HANDLEING){
			throw new InviteException("本邀请码已被锁定，正在注册中！");
		}
		
		inviteDao.update(code, null, Invite.STATE_HANDLEING);
	}

	@Override
	public void register(String code, Integer lenderId) throws InviteException {
		Invite invite = inviteDao.findByCode(code);
		if(invite==null){
			throw new InviteException("无效的邀请码！");
		}
		if(invite.getState()==Invite.STATE_REGISTERED){
			throw new InviteException("本邀请码已经被成功注册，无法再次注册！");
		}
		inviteDao.update(code, lenderId, Invite.STATE_REGISTERED);
	}

	@Override
	public void release(String code) throws InviteException {
		Invite invite = inviteDao.findByCode(code);
		if(invite==null){
			throw new InviteException("无效的邀请码！");
		}
		if(invite.getState()==Invite.STATE_REGISTERED){
			throw new InviteException("本邀请码已经被成功注册，无法释放！");
		}
		inviteDao.update(code, null, Invite.STATE_INIT);
	}

}
