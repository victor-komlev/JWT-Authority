package org.vkomlev.jwt.authority.server.endpoint.services;

import org.vkomlev.jwt.authority.server.data.UniversalDAO;
import org.vkomlev.jwt.authority.server.dto.User;
import org.vkomlev.jwt.authority.server.exceptions.UserCreationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by vkomlev on 2017-05-28.
 */
@Service
public class UserService {
    private static Logger LOGGER = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private UniversalDAO universalDAO;

    public List<User> getListOfCreatedUsers() {
        LOGGER.info("getListOfCreatedUsers()");
        List<User> users = universalDAO.getAllUsers();
        return users;
    }

    public void processCreateUser(String userName, String userPassword, String userRoles, Long tenantId) {
        LOGGER.info("processCreateUser({}, {}, {}, {})", userName, userPassword, userRoles, tenantId);
        try {
            if (StringUtils.isEmpty(userName))
                throw new IllegalArgumentException("User Name cannot be empty!");
            if (StringUtils.isEmpty(userPassword))
                throw new IllegalArgumentException("User Password cannot be empty!");
            if (StringUtils.isEmpty(userRoles))
                throw new IllegalArgumentException("User Roles cannot be empty!");
            if (universalDAO.getUserByName(userName) != null)
                throw new IllegalArgumentException("User " + userName + " already exists!");
            List<String> roleStrings = Arrays.asList(userRoles.split(",")).stream().map(role -> role.trim())
                    .collect(Collectors.toList());

            universalDAO.saveNewUser(userName, userPassword, roleStrings, tenantId);
        } catch (Exception e) {
            LOGGER.error("processCreateUser({}, {}, {}, {})", userName, userPassword, userRoles, tenantId, e);
            throw new UserCreationException(e);
        }
    }
}
