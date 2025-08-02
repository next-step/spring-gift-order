package gift.service;

import gift.client.KakaoApiClient;
import gift.domain.OptionBuying;
import gift.dto.KakaoTemplateObjectDto;
import gift.dto.OptionBuyingRequestDto;
import gift.dto.OptionBuyingResponseDto;
import gift.entity.OptionBuyingEntity;
import gift.entity.OptionEntity;
import gift.entity.member.SocialMemberEntity;
import gift.repository.OptionBuyingRepository;
import gift.repository.OptionRepository;
import gift.repository.member.CommonMemberRepository;
import gift.repository.member.SocialMemberRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
public class OptionBuyingService {

    private final String serverUrl;

    private final OptionBuyingRepository optionBuyingRepository;
    private final OptionRepository optionRepository;
    private final OptionService optionService;
    private final SocialMemberRepository socialMemberRepository;
    private final CommonMemberRepository commonMemberRepository;
    private final WishService wishService;
    private final KakaoApiClient kakaoApiClient;

    public OptionBuyingService(OptionBuyingRepository optionBuyingRepository, OptionRepository optionRepository, OptionService optionService, SocialMemberRepository socialMemberRepository, CommonMemberRepository commonMemberRepository, WishService wishService, KakaoApiClient kakaoApiClient, @Value("${server.url}") String serverUrl) {
        this.optionBuyingRepository = optionBuyingRepository;
        this.optionRepository = optionRepository;
        this.optionService = optionService;
        this.socialMemberRepository = socialMemberRepository;
        this.commonMemberRepository = commonMemberRepository;
        this.wishService = wishService;
        this.kakaoApiClient = kakaoApiClient;
        this.serverUrl = serverUrl;
    }

    public OptionBuyingResponseDto buyOption(long memberId, OptionBuyingRequestDto optionBuyingRequestDto) {

        OptionEntity optionEntity = optionRepository.findById(optionBuyingRequestDto.optionId())
                .orElseThrow(() -> new IllegalArgumentException("Option Not Found"));

        optionService.subtractOptionQuantity(
                optionBuyingRequestDto.optionId(),
                optionBuyingRequestDto.quantity()
        );

        OptionBuyingEntity optionBuyingEntity = new OptionBuyingEntity(optionBuyingRequestDto, optionEntity);

        OptionBuyingEntity savedOptionBuyingEntity = optionBuyingRepository.save(optionBuyingEntity);

        int wishCount = wishService.getWishCount(memberId, optionEntity.getId());

        if (wishCount > 0) {
            int updatedWishCount = Math.max(wishCount - optionBuyingRequestDto.quantity(), 0);

            wishService.updateWishCount(memberId, optionEntity.getId(), updatedWishCount);
        }

        sendMessage(memberId, savedOptionBuyingEntity);

        return new OptionBuyingResponseDto(savedOptionBuyingEntity);
    }

    private void sendMessage(long memberId, OptionBuyingEntity optionBuyingEntity) {
        commonMemberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("Member not Found"));

        Optional<SocialMemberEntity> optionalSocialMemberEntity = socialMemberRepository.findById(memberId);

        if (optionalSocialMemberEntity.isEmpty()) {
            // this member is not Send Message
            return;
        }

        kakaoApiClient.sendKakaoMessage(optionalSocialMemberEntity
                .get().getAccessToken(),
                makeTemplateObjectDto(
                        optionBuyingEntity.toDomain()
                )
        );
    }

    private KakaoTemplateObjectDto makeTemplateObjectDto(OptionBuying optionBuying) {
        return new KakaoTemplateObjectDto(
                optionBuying.createMessage(),
                serverUrl
        );
    }
}
