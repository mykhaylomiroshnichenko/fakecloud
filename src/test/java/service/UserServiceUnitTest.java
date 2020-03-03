package service;

import com.fakecloud.Application;
import com.fakecloud.model.Role;
import com.fakecloud.model.User;
import com.fakecloud.repository.RoleRepository;
import com.fakecloud.repository.UserRepository;
import com.fakecloud.service.UserService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
public class UserServiceUnitTest {
    @Autowired
    private UserService userService;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private RoleRepository roleRepository;

    private User user1;
    private User user2;

    private Role roleUser;

    private final Long user1Number = 1L;
    private final Long user2Number = 2L;
    private final String user1UserName = "User1UserName@test.com";
    private final String user2UserName = "User2UserName@test.com";
    private final String roleUserName = "ROLE_USER";

    private final List<User> users = new ArrayList<>();

    @Before
    public void setup() {
        roleUser = new Role();
        roleUser.setId(1L);
        roleUser.setName(roleUserName);

        Mockito.when(roleRepository.findByName(roleUserName)).thenReturn(roleUser);
        Role roleUser = roleRepository.findByName("ROLE_USER");
        List<Role> userRoles = new ArrayList<>();
        userRoles.add(roleUser);

        user1 = new User();
        user1.setId(user1Number);
        user1.setFirstName("User1FirstName");
        user1.setLastName("User1LastName");
        user1.setUsername(user1UserName);
        user1.setPassword(passwordEncoder.encode("1234"));

        user1.setRoles(userRoles);

        user2 = new User();
        user2.setId(user2Number);
        user2.setFirstName("User2FirstName");
        user2.setLastName("User2LastName");
        user2.setUsername(user2UserName);
        user2.setPassword(passwordEncoder.encode("1234"));
        user2.setRoles(userRoles);

        users.add(user1);
        users.add(user2);

        Mockito.when(userRepository.findAll()).thenReturn(users);
        Mockito.when(userRepository.findById(user1Number)).thenReturn(java.util.Optional.ofNullable(user1));
        Mockito.when(userRepository.findByUsername(user1UserName)).thenReturn(user1);
        Mockito.when(userRepository.findByUsername(user2UserName)).thenReturn(null);
        Mockito.when(userRepository.save(user2)).thenReturn(user2);
    }

    @Test
    public void testFindAll_thenUserListShouldBeReturned() {
        List<User> foundUsers = userService.getAll();
        assertNotNull(foundUsers);
        assertEquals(2, foundUsers.size());
    }

    @Test
    public void testFindById_thanUser1ShouldBeReturned() {
        User found = userService.findById(user1Number);

        assertNotNull(found);
        assertEquals(user1.getUsername(), found.getUsername());
        assertEquals(user1.getId(), found.getId());
    }

    @Test
    public void testFindByUserName_thanUser1ShouldBeReturned() {
        User found = userService.findByUsername(user1UserName);

        assertNotNull(found);
        assertEquals(user1.getUsername(), found.getUsername());
        assertEquals(user1.getId(), found.getId());
    }

    @Test
    public void testDeleteUserById() {
        userService.delete(user1.getId());

        Mockito.verify(userRepository, Mockito.times(1)).deleteById(user1.getId());
    }

    @Test
    public void testRegisterUser_thenUserShouldBeReturned() throws Exception {
        User registered = userService.register(user2);

        assertNotNull(registered);
        assertEquals(user2.getUsername(), registered.getUsername());
        assertEquals(user2.getId(), registered.getId());
    }
}
