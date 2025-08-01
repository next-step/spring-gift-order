package gift.service.member;

import gift.config.KakaoClient;
import gift.dto.kakao.KakaoTokenResponseDto;
import gift.dto.kakao.KakaoUserInfoResponseDto;
import gift.dto.member.MemberRequestDto;
import gift.dto.member.MemberResponseDto;
import gift.dto.member.MemberRoleRequestDto;
import gift.dto.member.TokenResponseDto;
import gift.entity.Member;
import gift.entity.MemberKakaoToken;
import gift.entity.OAuthAccount;
import gift.entity.RoleType;
import gift.exception.member.InvalidCredentialsException;
import gift.exception.member.MemberAlreadyExistsException;
import gift.exception.member.MemberNotFoundException;
import gift.repository.MemberKakaoTokenRepository;
import gift.repository.MemberRepository;
import gift.repository.OAuthAccountRepository;
import gift.util.JwtTokenProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class MemberServiceImpl implements MemberService {

    private static final Logger log = LoggerFactory.getLogger(MemberServiceImpl.class);
    private final MemberRepository memberRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final KakaoClient kakaoClient;
    private final OAuthAccountRepository oAuthAccountRepository;
    private final MemberKakaoTokenRepository memberKakaoTokenRepository;

    public MemberServiceImpl(MemberRepository memberRepository,
                             JwtTokenProvider jwtTokenProvider,
                             KakaoClient kakaoClient,
                             OAuthAccountRepository oAuthAccountRepository,
                             MemberKakaoTokenRepository memberKakaoTokenRepository) {
        this.memberRepository = memberRepository;
        this.jwtTokenProvider = jwtTokenProvider;
        this.kakaoClient = kakaoClient;
        this.oAuthAccountRepository = oAuthAccountRepository;
        this.memberKakaoTokenRepository = memberKakaoTokenRepository;
    }

    @Transactional
    @Override
    public TokenResponseDto registerMember(MemberRequestDto dto) {

        String email = dto.getEmail();
        String password = dto.getPassword();
        RoleType role = RoleType.USER;

        if (memberRepository.findByEmail(email).isPresent()) {
            throw new MemberAlreadyExistsException("이미 해당 이메일로 가입된 회원이 존재합니다.");
        }

        Member newMember = new Member(email, password, role);
        Member savedMember = memberRepository.save(newMember);
        String token = jwtTokenProvider.getAccessToken(savedMember);

        return new TokenResponseDto(token);
    }

    @Override
    public TokenResponseDto loginMember(MemberRequestDto dto) {
        String email = dto.getEmail();
        String password = dto.getPassword();

        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() ->
                        new InvalidCredentialsException(
                                "아이디 또는 비밀번호가 잘못 되었습니다. 아이디와 비밀번호를 정확히 입력해 주세요."
                        )
                );

        if (!password.equals(member.getPassword())) {
            throw new InvalidCredentialsException("아이디 또는 비밀번호가 잘못 되었습니다. 아이디와 비밀번호를 정확히 입력해 주세요.");
        }

        String token = jwtTokenProvider.getAccessToken(member);

        return new TokenResponseDto(token);
    }

    @Transactional
    @Override
    public TokenResponseDto loginWithKakao(KakaoTokenResponseDto kakaoToken) {
        KakaoUserInfoResponseDto userInfo = kakaoClient.getUserInfo(kakaoToken.accessToken());
        String kakaoId = userInfo.id().toString();

        Member member = oAuthAccountRepository.findByKakaoId(kakaoId)
                .map(OAuthAccount::getMember)
                .orElseGet(() -> registerWithOAuth(
                        userInfo, kakaoId
                ));

        MemberKakaoToken memberKakaoToken = memberKakaoTokenRepository.findById(member.getId())
                .orElseGet(() -> new MemberKakaoToken(member, null, null, null, null));

        memberKakaoToken.updateToken(
                kakaoToken.accessToken(),
                kakaoToken.refreshToken(),
                kakaoToken.expiresIn(),
                kakaoToken.refreshTokenExpiresIn()
        );

        memberKakaoTokenRepository.save(memberKakaoToken);

        String accessToken = jwtTokenProvider.getAccessToken(member);
        return new TokenResponseDto(accessToken);
    }

    @Override
    public List<MemberResponseDto> findAllMembers() {
        List<Member> findList = memberRepository.findAll();

        List<MemberResponseDto> dtoList = findList
                .stream()
                .map(x -> new MemberResponseDto(
                        x.getId(),
                        x.getEmail(),
                        x.getPassword(),
                        x.getRole()
                ))
                .toList();

        return dtoList;
    }

    @Override
    public Optional<MemberResponseDto> findMemberById(Long id) {

        return memberRepository.findById(id)
                .map(member -> new MemberResponseDto(
                        member.getId(),
                        member.getEmail(),
                        member.getPassword(),
                        member.getRole()
                ));
    }

    @Override
    public MemberResponseDto findMemberByIdElseThrow(Long id) {

        return memberRepository.findById(id)
                .map(member -> new MemberResponseDto(
                        member.getId(),
                        member.getEmail(),
                        member.getPassword(),
                        member.getRole()
                ))
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "해당 ID의 멤버은 존재하지 않습니다."
                        )
                );
    }

    @Transactional
    @Override
    public MemberResponseDto saveMember(MemberRequestDto dto) {

        String email = dto.getEmail();
        String password = dto.getPassword();
        RoleType role = RoleType.USER;

        if (memberRepository.findByEmail(email).isPresent()) {
            throw new MemberAlreadyExistsException("이미 해당 이메일로 가입된 회원이 존재합니다.");
        }

        Member newMember = new Member(email, password, role);
        Member savedMember = memberRepository.save(newMember);

        return new MemberResponseDto(savedMember.getId(), savedMember.getEmail(), savedMember.getPassword(), savedMember.getRole());
    }

    @Transactional
    @Override
    public MemberResponseDto updateMember(Long id, MemberRoleRequestDto dto) {

        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new MemberNotFoundException(
                        "해당 ID의 멤버는 존재하지 않습니다."
                ));

        member.setRole(dto.getRole());

        return new MemberResponseDto(
                member.getId(),
                member.getEmail(),
                member.getPassword(),
                member.getRole()
        );
    }

    @Transactional
    @Override
    public void deleteMember(Long id) {
        if (!memberRepository.existsById(id)) {
            throw new MemberNotFoundException(
                    "해당 ID의 멤버는 존재하지 않습니다."
            );
        }

        memberRepository.deleteById(id);
    }

    private Member registerWithOAuth(KakaoUserInfoResponseDto userInfo, String kakaoId) {

        Member newMember = new Member(
                userInfo.kakaoAccount().email(),
                UUID.randomUUID().toString(),
                RoleType.USER
        );
        memberRepository.save(newMember);

        OAuthAccount oAuthAccount = new OAuthAccount(kakaoId, newMember);
        oAuthAccountRepository.save(oAuthAccount);

        return newMember;
    }
}
