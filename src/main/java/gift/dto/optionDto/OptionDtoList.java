package gift.dto.optionDto;

import gift.entity.ItemOption;

import java.util.ArrayList;
import java.util.List;

public record OptionDtoList(List<OptionResponseDto> optionResponseDtoList) {

    public static OptionDtoList from(List<ItemOption> optionList) {
        List<OptionResponseDto> responseList = new ArrayList<>();
        for (ItemOption option : optionList) {
            responseList.add(OptionResponseDto.from(option));
        }
        return new OptionDtoList(responseList);
    }
}