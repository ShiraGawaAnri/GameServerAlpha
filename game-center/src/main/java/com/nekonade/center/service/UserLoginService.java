package com.nekonade.center.service;

import com.nekonade.center.logicconfig.GameCenterConfig;
import com.nekonade.center.logicconfig.GameCenterRedisKeyConifg;
import com.nekonade.center.messages.GameCenterError;
import com.nekonade.center.messages.request.LoginParam;
import com.nekonade.common.utils.GameUUIDUtil;
import com.nekonade.common.utils.JWTUtil;
import com.nekonade.dao.daos.AsyncUserAccountDao;
import com.nekonade.dao.daos.UserAccountDao;
import com.nekonade.dao.db.entity.UserAccount;
import com.nekonade.network.message.errors.GameErrorException;
import com.nekonade.network.message.errors.IServerError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Optional;

@Service
public class UserLoginService {

	@Autowired
	private UserAccountDao userAccountDao;
	@Autowired
	private AsyncUserAccountDao asyncUserAccountDao;
	@Autowired
	private StringRedisTemplate redisTemplate;
	@Autowired
	private GameCenterConfig gameCenterConfig;
	private final Logger logger = LoggerFactory.getLogger(UserLoginService.class);

	public IServerError verfiyLoginParam(LoginParam loginParam) {

		return null;
	}

	public IServerError verfiySdkToken(String openId, String token) {
		// 这里调用sdk服务端验证接口

		return null;
	}

	/**
	 * 根据用户名和密码登陆
	 * <p>
	 * Description:
	 * </p>
	 * 
	 * @param loginParam
	 * @return
	 * @author wgs
	 * @date 2019年8月29日 下午8:24:29
	 *
	 */
	public UserAccount loginByUserName(LoginParam loginParam) {
		String userName = loginParam.getUserName();
		userName = userName.intern();

		Optional<UserAccount> userAccountOp = this.userAccountDao.getUserAccountByUserName(userName);
		if (userAccountOp.isPresent()) {
			UserAccount userAccount = userAccountOp.get();
			if (userAccount.getPassword().equals(loginParam.getPassword())) {
				return userAccount;
			}
			throw GameErrorException.newBuilder(GameCenterError.PASSWORD_ERROR).build();
		} else {
			synchronized (userName) {
				// 用户不存在，自动注册
				if (loginParam.isUserNameLogin()) {
					String openId = GameUUIDUtil.getUId();
					loginParam.setOpenId(openId);
				}
				return this.register(loginParam);
			}
		}
	}

	public Optional<UserAccount> getUserAccountByUserName(String userName) {
		return this.userAccountDao.getUserAccountByUserName(userName);
	}

	public Optional<UserAccount> getUserAccountByUserId(long userId) {
		return this.userAccountDao.findByIdFromCacheOrLoader(userId);
	}

	private UserAccount register(LoginParam loginParam) {

		long userId = this.getNextUserId();// 使用redis自增保证userId全局唯一
		UserAccount userAccount = new UserAccount();
		userAccount.setCreateTime(System.currentTimeMillis());
		userAccount.setRegisterType(loginParam.getLoginType());
		userAccount.setUserId(userId);
		userAccount.setUserName(loginParam.getUserName());
		userAccount.setPassword(loginParam.getPassword());
		userAccount.setRegistIp(loginParam.getLoginIp());
		this.updateUserAccount(userAccount);
		this.userAccountDao.setUserNameIDMapper(userAccount);
		logger.debug("user {} 注册成功,userId:{}", userAccount.getUserName(), userAccount.getUserId());
		return userAccount;

	}

	public void updateUserAccount(UserAccount userAccount) {
		this.userAccountDao.saveOrUpdate(userAccount, userAccount.getUserId());
	}

	public String createUserToken(UserAccount userAccount, int loginType) {
		JWTUtil.TokenContent tokenContent = new JWTUtil.TokenContent();
		tokenContent.setLoginType(loginType);
		tokenContent.setUserId(userAccount.getUserId());
		tokenContent.setOpenId(userAccount.getUserName());
		return JWTUtil.createToken(tokenContent, Duration.ofDays(gameCenterConfig.getUserTokenExpire()));// 有效期
	}

	/**
	 * 
	 * <p>
	 * Description:更新用户的活跃时间
	 * </p>
	 * 
	 * @param userAccount
	 * @author wang guang shuai
	 * @date 2020年1月10日 下午3:59:16
	 *
	 */
	public void updateUserAccountExpire(UserAccount userAccount) {
		asyncUserAccountDao.updateUserAccountExpire(userAccount);

	}

	public long getNextUserId() {
		String key = GameCenterRedisKeyConifg.USER_ID_INCR.getKey();
		long userId = redisTemplate.opsForValue().increment(key);
		return userId;
	}

}
