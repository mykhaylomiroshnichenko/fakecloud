package service;

import com.fakecloud.Application;
import com.fakecloud.model.User;
import com.fakecloud.service.UserService;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class UserServiceIntegrationTest {
    @Autowired
    private UserService userService;

    private final String user1UserName = "User1UserName@test.com";
    private final String user2UserName = "User2UserName@test.com";

    @Test
    public void test1_registerUser() throws Exception {
        User user1 = new User();
        user1.setFirstName("User1FirstName");
        user1.setLastName("User1LastName");
        user1.setUsername(user1UserName);
        user1.setPassword("1234");

        User registered1 = userService.register(user1);
        assertNotNull(registered1);
        assertEquals(registered1.getUsername(), user1.getUsername());

        User user2 = new User();
        user2.setFirstName("User2FirstName");
        user2.setLastName("User2LastName");
        user2.setUsername(user2UserName);
        user2.setPassword("1234");

        User registered2 = userService.register(user2);
        assertNotNull(registered2);
        assertEquals(registered2.getUsername(), user2.getUsername());
    }

    @Test
    public void test2_findAll_thenUserListShouldBeReturned() {
        List<User> foundUsers = userService.getAll();
        assertNotNull(foundUsers);
        assertEquals(2, foundUsers.size());
    }

    @Test
    public void test3_findByUsername_findById_thanUser1ShouldBeReturned() {
        User foundByUsername = userService.findByUsername(user1UserName);

        assertNotNull(foundByUsername);
        assertEquals(user1UserName, foundByUsername.getUsername());

        User foundById = userService.findById(foundByUsername.getId());

        assertNotNull(foundById);
        assertEquals(foundByUsername.getUsername(), foundById.getUsername());
        assertEquals(foundByUsername.getId(), foundById.getId());
    }

    @Test
    public void testDeleteUserById() {
        User foundByUsername = userService.findByUsername(user1UserName);
        userService.delete(foundByUsername.getId());

        List<User> foundUsers = userService.getAll();
        assertNotNull(foundUsers);
        assertEquals(1, foundUsers.size());
    }

}
