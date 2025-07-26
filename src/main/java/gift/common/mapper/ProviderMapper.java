package gift.common.mapper;

import gift.entity.type.Provider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class ProviderMapper {
    private final Map<String, Provider>  issuerToProviderMap;
    private final Map<Provider, String> providerToIssuerMap;

    public ProviderMapper(
            @Value("${gift.jwt.issuer") String localIssuer,
            @Value("${gift.oauth.provider.kakao.baseurl}") String kakaoIssuer
    ) {
        issuerToProviderMap = Map.of(
                localIssuer, Provider.EMAIL,
                kakaoIssuer, Provider.KAKAO
        );

        providerToIssuerMap = Map.of(
                Provider.EMAIL, localIssuer,
                Provider.KAKAO, kakaoIssuer
        );
    }

    public Provider toProvider(String  issuer) {
        return issuerToProviderMap.getOrDefault(issuer, Provider.UNKNOWN);
    }

    public String toIssuer(Provider provider) {
        return providerToIssuerMap.getOrDefault(provider, null);
    }
}
