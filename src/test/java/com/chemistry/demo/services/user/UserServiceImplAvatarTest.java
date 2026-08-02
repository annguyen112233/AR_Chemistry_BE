package com.chemistry.demo.services.user;

import com.chemistry.demo.entity.KnowledgePointWallet;
import com.chemistry.demo.entity.Role;
import com.chemistry.demo.entity.User;
import com.chemistry.demo.enums.RoleName;
import com.chemistry.demo.mapper.UserMapper;
import com.chemistry.demo.mapper.UserProfileMapper;
import com.chemistry.demo.repository.KnowledgePointWalletRepository;
import com.chemistry.demo.repository.RoleRepository;
import com.chemistry.demo.repository.UserRepository;
import com.chemistry.demo.services.aws.CognitoService;
import com.chemistry.demo.services.user.impl.UserServiceImpl;
import com.chemistry.demo.utils.SecurityUtils;
import com.chemistry.demo.utils.UserSecurityCacheService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

/**
 * Kiểm chứng logic lấy avatar/tên từ nhà cung cấp đăng nhập (Google qua Cognito)
 * trong {@link UserServiceImpl#syncUser}. Không cần DB — mock toàn bộ dependency.
 */
@ExtendWith(MockitoExtension.class)
class UserServiceImplAvatarTest {

    @Mock private UserRepository userRepository;
    @Mock private RoleRepository roleRepository;
    @Mock private UserSecurityCacheService userSecurityCacheService;
    @Mock private UserMapper userMapper;
    @Mock private UserProfileMapper userProfileMapper;
    @Mock private SecurityUtils securityUtils;
    @Mock private CognitoService cognitoService;
    @Mock private KnowledgePointWalletRepository knowledgePointWalletRepository;

    @InjectMocks private UserServiceImpl userService;

    @Captor private ArgumentCaptor<User> userCaptor;

    private static final String SUB = "google-sub-123";
    private static final String EMAIL = "bao@gmail.com";
    private static final String USERNAME = "google_123";
    private static final String GOOGLE_NAME = "Tran Dinh Bao";
    private static final String GOOGLE_PICTURE = "https://lh3.googleusercontent.com/a/avatar.jpg";

    @BeforeEach
    void setUp() {
        lenient().when(roleRepository.findByRoleName(RoleName.ROLE_STUDENT))
                .thenReturn(Optional.of(org.mockito.Mockito.mock(Role.class)));
        // save() trả về chính đối tượng được lưu để test đọc lại giá trị đã set
        lenient().when(userRepository.save(any(User.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        // ví đã tồn tại -> bỏ qua nhánh tạo ví
        lenient().when(knowledgePointWalletRepository.findByUserId(anyString()))
                .thenReturn(Optional.of(org.mockito.Mockito.mock(KnowledgePointWallet.class)));
    }

    @Test
    @DisplayName("User mới đăng nhập Google: lưu avatar và tên từ claim")
    void newUser_storesGoogleAvatarAndName() {
        when(userRepository.findByCognitoSub(SUB)).thenReturn(Optional.empty());

        userService.syncUser(EMAIL, USERNAME, SUB, GOOGLE_NAME, GOOGLE_PICTURE);

        verifySavedUser();
        User saved = userCaptor.getValue();
        assertThat(saved.getAvatarUrl()).isEqualTo(GOOGLE_PICTURE);
        assertThat(saved.getFullName()).isEqualTo(GOOGLE_NAME);
        assertThat(saved.getEmail()).isEqualTo(EMAIL);
    }

    @Test
    @DisplayName("User cũ chưa có avatar: backfill từ Google")
    void existingUserWithoutAvatar_backfilledFromGoogle() {
        User existing = User.builder().cognitoSub(SUB).email(EMAIL).build(); // avatar/name null
        when(userRepository.findByCognitoSub(SUB)).thenReturn(Optional.of(existing));

        userService.syncUser(EMAIL, USERNAME, SUB, GOOGLE_NAME, GOOGLE_PICTURE);

        verifySavedUser();
        User saved = userCaptor.getValue();
        assertThat(saved.getAvatarUrl()).isEqualTo(GOOGLE_PICTURE);
        assertThat(saved.getFullName()).isEqualTo(GOOGLE_NAME);
    }

    @Test
    @DisplayName("User đã tự đặt avatar: KHÔNG bị Google ghi đè")
    void existingCustomAvatar_notOverwritten() {
        User existing = User.builder()
                .cognitoSub(SUB).email(EMAIL)
                .avatarUrl("https://cdn.myapp.com/custom.png")
                .fullName("Ten Nguoi Dung Tu Dat")
                .build();
        when(userRepository.findByCognitoSub(SUB)).thenReturn(Optional.of(existing));

        userService.syncUser(EMAIL, USERNAME, SUB, GOOGLE_NAME, GOOGLE_PICTURE);

        verifySavedUser();
        User saved = userCaptor.getValue();
        assertThat(saved.getAvatarUrl()).isEqualTo("https://cdn.myapp.com/custom.png");
        assertThat(saved.getFullName()).isEqualTo("Ten Nguoi Dung Tu Dat");
    }

    @Test
    @DisplayName("Không có claim picture (token thiếu): avatar để trống, không lỗi")
    void missingPictureClaim_keepsAvatarNull() {
        when(userRepository.findByCognitoSub(SUB)).thenReturn(Optional.empty());

        userService.syncUser(EMAIL, USERNAME, SUB, null, null);

        verifySavedUser();
        assertThat(userCaptor.getValue().getAvatarUrl()).isNull();
    }

    private void verifySavedUser() {
        org.mockito.Mockito.verify(userRepository).save(userCaptor.capture());
    }
}
