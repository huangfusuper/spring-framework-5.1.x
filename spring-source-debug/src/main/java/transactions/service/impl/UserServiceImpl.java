package transactions.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import transactions.mapper.UserDao;
import transactions.service.UserService;

@Service
public class UserServiceImpl  implements UserService {

    @Autowired
    private UserDao userDao;

    @Override
    @Transactional
    public void addUser() {
        userDao.save("皇甫ceshi",15);
        int i = 1 / 0;
    }
}