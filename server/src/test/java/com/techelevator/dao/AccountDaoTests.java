package com.techelevator.dao;

import ch.qos.logback.core.subst.Token;
import com.techelevator.tenmo.dao.AccountDao;
import com.techelevator.tenmo.dao.JdbcAccountDao;
import com.techelevator.tenmo.dao.JdbcUserDao;
import com.techelevator.tenmo.dao.UserDao;
import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.User;
import com.techelevator.tenmo.security.jwt.TokenProvider;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.stereotype.Component;

import static org.mockito.Mockito.when;


public class AccountDaoTests extends BaseDaoTests{

    private static final Account ACCOUNT_1 = new Account(2001, 1001, 1000);
    private static final Account ACCOUNT_2 = new Account(2002, 1002, 800);


    private JdbcAccountDao sut;
    private JdbcUserDao userDao;
    //private TokenProvider token;

    private Account testAccount;


    //private JdbcAccountDao accountDao;

    // Need to update tests

    @Before
    public void setup() {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        sut = new JdbcAccountDao(jdbcTemplate);
    }

    @Test
    public void testGetAccountByAccountId_accountId_2001_returns_account1() {
        Account account = sut.getAccountByAccountId(2001);

        assertAccountsMatch(ACCOUNT_1, account);
    }


    private void assertAccountsMatch(Account expected, Account actual) {
        Assert.assertEquals(expected.getAccountId(), actual.getAccountId());
        Assert.assertEquals(expected.getUserId(), actual.getUserId());
        Assert.assertEquals(expected.getBalance(), actual.getBalance(), .01);

    }






//    @Before
//    public void setup() {
//        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
//        sut = new JdbcAccountDao(jdbcTemplate);
//        userDao = new JdbcUserDao(jdbcTemplate);
//        testAccount = new Account(2, 2, 2);
//        boolean userCreated = userDao.create("TEST_USER", "pass");
//        User user = userDao.findByUsername("TEST_USER");
//       token.createToken(user. )
//    }

//    @Test
//    public void createAccount_returns_new_account_with_userId_1001_accountId_2001_balance_1000() {
//
//
//        int accountId = result.getAccountId();
//        int userId = result.getUserId();
//        double balance = result.getBalance();
//
//        int expectedAccountId = 2001;
//        int expectedUserId = 1001;
//        double expectedBalance = 1000;
//
//        Assert.assertEquals(expectedAccountId, accountId);
//        Assert.assertEquals(expectedUserId, userId);
//        Assert.assertEquals(expectedBalance, balance, 0.001);
//
//    }

//    @Test
//    public void createAccount_returns_new_account_with_userId_1001_accountId_2001_balance_1000() {
//
//        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
//
//        when(sut.createAccount(testAccount, auth)).thenReturn(sut.createAccount(testAccount, auth));
//        Account result = sut.createAccount(testAccount, auth);
//
//        int accountId = result.getAccountId();
//        int userId = result.getUserId();
//        double balance = result.getBalance();
//
//        int expectedAccountId = 2001;
//        int expectedUserId = 1001;
//        double expectedBalance = 1000;
//
//        Assert.assertEquals(expectedAccountId, accountId);
//        Assert.assertEquals(expectedUserId, userId);
//        Assert.assertEquals(expectedBalance, balance, 0.001);
//
//    }





}
